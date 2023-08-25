/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.client.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import javax.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientBusinessDetailDataValidator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientBusinessDetail;
import org.apache.fineract.portfolio.client.domain.ClientBusinessDetailRepository;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.MonthEnum;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessDetailWritePlatformServiceImpl implements BusinessDetailWritePlatformService {

    private final PlatformSecurityContext context;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final ClientBusinessDetailRepository clientBusinessDetailRepository;
    private final ClientBusinessDetailDataValidator fromApiJsonDeserializer;
    private final ConfigurationReadPlatformService configurationReadPlatformService;
    private final FromJsonHelper fromJsonHelper;

    @Transactional
    @Override
    public CommandProcessingResult addBusinessDetail(Long clientId, JsonCommand command) {
        this.context.authenticatedUser();
        final Client client = clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);

        final GlobalConfigurationPropertyData businessDetailConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Enable-Client-Business-Detail");
        final Boolean isClientBusinessDetailsEnable = businessDetailConfig.isEnabled();

        if (!isClientBusinessDetailsEnable) {
            throw new GeneralPlatformDomainRuleException("error.msg.Enable-Client-Business-Detail.is.not.set",
                    "Enable-Client-Business-Detail settings is not set. So this operation is not permitted");
        }

        this.fromApiJsonDeserializer.validateForCreate(command.json());
        // Validate If the externalId is already registered
        final String externalId = this.fromJsonHelper.extractStringNamed("externalId", command.parsedJson());
        if (StringUtils.isNotBlank(externalId)) {
            final boolean existByExternalId = this.clientBusinessDetailRepository.existsByExternalId(externalId);
            if (existByExternalId) {
                throw new GeneralPlatformDomainRuleException("error.msg.client-business-details.with.externalId.already.used",
                        "Client Details with externalId is already registered.");
            }
        }
        final Integer bestMonthParamValue = command.integerValueOfParameterNamed(ClientApiConstants.BEST_MONTH);
        if (bestMonthParamValue != null) {
            MonthEnum bestMonth = MonthEnum.fromInt(bestMonthParamValue);
            if (bestMonth == null) {
                throw new GeneralPlatformDomainRuleException("error.msg.bestMonth.enum-type-doesn't.exist",
                        "Enum type provided for Best Month doesn't exist. Provided value is not supported");
            }
        }

        final Integer worstMonthParamValue = command.integerValueOfParameterNamed(ClientApiConstants.WORST_MONTH);
        if (worstMonthParamValue != null) {
            MonthEnum worstMonth = MonthEnum.fromInt(worstMonthParamValue);
            if (worstMonth == null) {
                throw new GeneralPlatformDomainRuleException("error.msg.worstMonth.enum-type-doesn't.exist",
                        "Enum type provided for Worst Month doesn't exist. Provided value is not supported");
            }
        }
        final Integer whenLastPurchaseParamValue = command.integerValueOfParameterNamed(ClientApiConstants.WHEN_LAST_PURCHASE);
        if (whenLastPurchaseParamValue != null) {
            MonthEnum whenLastPurchase = MonthEnum.fromInt(whenLastPurchaseParamValue);
            if (whenLastPurchase == null) {
                throw new GeneralPlatformDomainRuleException("error.msg.when-last-purchase.enum-type-doesn't.exist",
                        "Enum type provided for Last Purchase doesn't exist. Provided value is not supported");
            }
        }

        ClientBusinessDetail businessDetail = clientBusinessDetailRepository.saveAndFlush(createBusinessDetail(command, client));

        return new CommandProcessingResultBuilder().withCommandId(command.commandId())
                .withResourceIdAsString(businessDetail.getId().toString()).withClientId(clientId).build();
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteBusinessDetail(Long clientId, Long businessDetailId) {
        try {
            this.context.authenticatedUser();
            final GlobalConfigurationPropertyData businessDetailConfig = this.configurationReadPlatformService
                    .retrieveGlobalConfiguration("Enable-Client-Business-Detail");
            final Boolean isClientBusinessDetailsEnable = businessDetailConfig.isEnabled();

            if (!isClientBusinessDetailsEnable) {
                throw new GeneralPlatformDomainRuleException("error.msg.Enable-Client-Business-Detail.is.not.set",
                        "Enable-Client-Business-Detail settings is not set. So this operation is not permitted");
            }

            final Client client = clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            final ClientBusinessDetail clientBusinessDetail = clientBusinessDetailRepository.findById(businessDetailId).orElseThrow();

            this.clientBusinessDetailRepository.delete(clientBusinessDetail);
            this.clientBusinessDetailRepository.flush();
            return new CommandProcessingResultBuilder() //
                    .withOfficeId(client.officeId()) //
                    .withClientId(clientId) //
                    .withEntityId(clientBusinessDetail.getId()) //
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            throw new PlatformDataIntegrityException("error.msg.client.business.detail.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.", dve);
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateBusinessDetail(Long clientId, Long businessDetailId, JsonCommand command) {
        try {
            this.context.authenticatedUser();

            final GlobalConfigurationPropertyData businessDetailConfig = this.configurationReadPlatformService
                    .retrieveGlobalConfiguration("Enable-Client-Business-Detail");
            final Boolean isClientBusinessDetailsEnable = businessDetailConfig.isEnabled();

            if (!isClientBusinessDetailsEnable) {
                throw new GeneralPlatformDomainRuleException("error.msg.Enable-Client-Business-Detail.is.not.set",
                        "Enable-Client-Business-Detail settings is not set. So this operation is not permitted");
            }

            this.fromApiJsonDeserializer.validateForUpdate(command.json());

            clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);

            ClientBusinessDetail businessDetail = this.clientBusinessDetailRepository.findById(businessDetailId).orElseThrow();

            final Map<String, Object> changes = businessDetail.update(command);

            if (changes.containsKey(ClientApiConstants.BUSINESS_TYPE)) {
                final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.BUSINESS_TYPE);
                CodeValue businessType = null;
                if (newValue != null) {
                    businessType = this.codeValueRepository
                            .findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.BUSINESS_TYPE_CODE, newValue);
                }
                businessDetail.updateBusinessType(businessType);
            }

            if (changes.containsKey(ClientApiConstants.SOURCE_OF_CAPITAL)) {
                final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.SOURCE_OF_CAPITAL);
                CodeValue sourceOfCapital = null;
                if (newValue != null) {
                    sourceOfCapital = this.codeValueRepository
                            .findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.SOURCE_OF_CAPITAL_CODE, newValue);
                }
                businessDetail.updateSourceOfCapital(sourceOfCapital);
            }

            if (!changes.isEmpty()) {
                this.clientBusinessDetailRepository.saveAndFlush(businessDetail);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withClientId(clientId) //
                    .withEntityId(businessDetail.getId()) //
                    .with(changes) //
                    .build();
        } catch (final JpaSystemException ex) {
            return CommandProcessingResult.empty();
        } catch (final PersistenceException dve) {
            return CommandProcessingResult.empty();
        }
    }

    private ClientBusinessDetail createBusinessDetail(final JsonCommand command, Client client) {

        CodeValue businessType = null;
        CodeValue sourceOfCapital = null;

        final Long businessTypeId = command.longValueOfParameterNamed(ClientApiConstants.BUSINESS_TYPE);
        if (businessTypeId != null) {
            businessType = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.BUSINESS_TYPE_CODE,
                    businessTypeId);
        }

        final Long sourceOfCapitalId = command.longValueOfParameterNamed(ClientApiConstants.SOURCE_OF_CAPITAL);
        if (sourceOfCapitalId != null) {
            sourceOfCapital = this.codeValueRepository
                    .findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.SOURCE_OF_CAPITAL_CODE, sourceOfCapitalId);
        }
        final LocalDate businessCreationDate = command.localDateValueOfParameterNamed(ClientApiConstants.BUSINESS_CREATION_DATE);
        BigDecimal startingCapital = command.bigDecimalValueOfParameterNamed(ClientApiConstants.STARTING_CAPITAL);
        Long totalEmployee = command.longValueOfParameterNamed(ClientApiConstants.TOTAL_EMPLOYEE);
        BigDecimal businessRevenue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.BUSINESS_REVENUE);
        BigDecimal averageMonthlyRevenue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.AVERAGE_MONTHLY_REVENUE);
        Integer bestMonth = command.integerValueOfParameterNamed(ClientApiConstants.BEST_MONTH);
        String reasonForBestMonth = command.stringValueOfParameterNamed(ClientApiConstants.REASON_FOR_BEST_MONTH);
        Integer worstMonth = command.integerValueOfParameterNamed(ClientApiConstants.WORST_MONTH);
        String reasonForWorstMonth = command.stringValueOfParameterNamed(ClientApiConstants.REASON_FOR_WORST_MONTH);
        Long numberOfPurchase = command.longValueOfParameterNamed(ClientApiConstants.NUMBER_OF_PURCHASE);
        String purchaseFrequency = command.stringValueOfParameterNamed(ClientApiConstants.PURCHASE_FREQUENCY);
        BigDecimal totalPurchaseLastMonth = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_PURCHASE_LAST_MONTH);
        Integer whenLastPurchase = command.integerValueOfParameterNamed(ClientApiConstants.WHEN_LAST_PURCHASE);
        BigDecimal lastPurchaseAmount = command.bigDecimalValueOfParameterNamed(ClientApiConstants.LAST_PURCHASE_AMOUNT);
        BigDecimal businessAssetAmount = command.bigDecimalValueOfParameterNamed(ClientApiConstants.BUSINESS_ASSET_AMOUNT);
        BigDecimal amountAtCash = command.bigDecimalValueOfParameterNamed(ClientApiConstants.AMOUNT_AT_CASH);
        BigDecimal amountAtSaving = command.bigDecimalValueOfParameterNamed(ClientApiConstants.AMOUNT_AT_SAVING);
        BigDecimal amountAtInventory = command.bigDecimalValueOfParameterNamed(ClientApiConstants.AMOUNT_AT_INVENTORY);
        BigDecimal fixedAssetCost = command.bigDecimalValueOfParameterNamed(ClientApiConstants.FIXED_ASSET_COST);
        BigDecimal totalInTax = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_IN_TAX);
        BigDecimal totalInTransport = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_IN_TRANSPORT);
        BigDecimal totalInRent = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_IN_RENT);
        BigDecimal totalInCommunication = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_IN_COMMUNICATION);
        String otherExpense = command.stringValueOfParameterNamed(ClientApiConstants.OTHER_EXPENSE);
        BigDecimal otherExpenseAmount = command.bigDecimalValueOfParameterNamed(ClientApiConstants.OTHER_EXPENSE_AMOUNT);
        BigDecimal totalUtility = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_UTILITY);
        BigDecimal totalWorkerSalary = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_WORKER_SALARY);
        BigDecimal totalWage = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_WAGE);
        BigDecimal society = command.bigDecimalValueOfParameterNamed(ClientApiConstants.SOCIETY);
        String externalId = command.stringValueOfParameterNamed(ClientApiConstants.EXTERNAL_ID);

        ClientBusinessDetail clientBusinessDetail = ClientBusinessDetail.createNew(client, businessType, sourceOfCapital,
                businessCreationDate, startingCapital, totalEmployee, businessRevenue, averageMonthlyRevenue, bestMonth, reasonForBestMonth,
                worstMonth, reasonForWorstMonth, numberOfPurchase, purchaseFrequency, totalPurchaseLastMonth, whenLastPurchase,
                lastPurchaseAmount, businessAssetAmount, amountAtCash, amountAtSaving, amountAtInventory, fixedAssetCost, totalInTax,
                totalInTransport, totalInRent, totalInCommunication, otherExpense, otherExpenseAmount, totalUtility, totalWorkerSalary,
                totalWage, externalId, society);
        return clientBusinessDetail;
    }
}
