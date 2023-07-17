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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.database.DatabaseSpecificSQLGenerator;
import org.apache.fineract.infrastructure.dataqueries.service.EntityDatatableChecksReadService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.address.service.AddressReadPlatformService;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientBusinessDetailData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.domain.MonthEnum;
import org.apache.fineract.portfolio.client.exception.ClientBusinessDetailNotFoundException;
import org.apache.fineract.portfolio.client.exception.ClientNotFoundException;
import org.apache.fineract.portfolio.collateralmanagement.domain.ClientCollateralManagementRepositoryWrapper;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessDetailReadPlatformServiceImpl implements BusinessDetailReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final SavingsProductReadPlatformService savingsProductReadPlatformService;
    // data mappers
    private final PaginationHelper paginationHelper;
    private final DatabaseSpecificSQLGenerator sqlGenerator;

    private final CodeValueRepositoryWrapper codeValueRepository;
    private final AddressReadPlatformService addressReadPlatformService;
    private final ClientFamilyMembersReadPlatformService clientFamilyMembersReadPlatformService;
    private final ConfigurationReadPlatformService configurationReadPlatformService;
    private final EntityDatatableChecksReadService entityDatatableChecksReadService;
    private final ColumnValidator columnValidator;
    private final ClientCollateralManagementRepositoryWrapper clientCollateralManagementRepositoryWrapper;
    private final ClientBusinessOwnerReadPlatformService clientBusinessOwnerReadPlatformService;
    private final ClientReadPlatformService clientReadPlatformService;
    private final ClientBusinessDetailMapper clientBusinessDetailMapper = new ClientBusinessDetailMapper();

    @Override
    public ClientBusinessDetailData retrieveTemplate(Long clientId) {
        this.context.authenticatedUser();

        final List<CodeValueData> businessTypeOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.BUSINESS_TYPE_OPTIONS));
        final List<CodeValueData> sourceOfCapitalOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.SOURCE_OF_CAPITAL_OPTIONS));
        final List<EnumOptionData> monthEnumOptions = ClientEnumerations.monthEnum(MonthEnum.values());
        final ClientData clientAccount = this.clientReadPlatformService.retrieveOne(clientId);

        final GlobalConfigurationPropertyData enableClientBusinessDetail = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Enable-Client-Business-Detail");
        final Boolean isClientBusinessDetailEnabled = enableClientBusinessDetail.isEnabled();

        ClientBusinessDetailData clientBusinessDetailData = ClientBusinessDetailData.template(businessTypeOptions, sourceOfCapitalOptions,
                monthEnumOptions, monthEnumOptions, clientAccount);
        clientBusinessDetailData.setClientBusinessDetailEnabled(isClientBusinessDetailEnabled);
        return clientBusinessDetailData;

    }

    @Override
    public ClientBusinessDetailData retrieveBusinessDetail(Long clientId, Long businessDetailId) {
        try {
            this.context.authenticatedUser();

            final ClientData clientAccount = this.clientReadPlatformService.retrieveOne(clientId);

            if (clientAccount == null) {
                throw new ClientNotFoundException(clientId);
            }

            final String sql = "select " + this.clientBusinessDetailMapper.schema() + " where mc.id = ? AND d.id = ?";
            final ClientBusinessDetailData clientBusinessDetailData = this.jdbcTemplate.queryForObject(sql, this.clientBusinessDetailMapper,
                    clientId, businessDetailId);

            return clientBusinessDetailData;

        } catch (final EmptyResultDataAccessException e) {
            throw new ClientBusinessDetailNotFoundException(businessDetailId, e);
        }
    }

    private static final class ClientBusinessDetailMapper implements RowMapper<ClientBusinessDetailData> {

        private final String schema;

        ClientBusinessDetailMapper() {
            final StringBuilder builder = new StringBuilder(400);

            builder.append(
                    "d.id as id,d.client_id as clientId,codeBusinessType.id as businessTypeId,codeBusinessType.code_value as businessTypeCode,d.business_creation_date as businessCreationDate,d.starting_capital as startingCapital,");
            builder.append(
                    "codeSourceOfCapital.id as sourceOfCapitalId,codeSourceOfCapital.code_value as codeSourceOfCapitalCode,d.total_employee as totalEmployee,d.business_revenue as businessRevenue,d.average_monthly_revenue as averageMonthlyRevenue,");
            builder.append(
                    "d.best_month as bestMonth,d.reason_for_best_month as reasonForBestMonth,d.worst_month as worstMonth,d.reason_for_worst_month as reasonForWorstMonth,");
            builder.append(
                    "d.number_of_purchase as numberOfPurchase,d.purchase_frequency as purchaseFrequency,d.total_purchase_last_month as totalPurchaseLastMonth,d.last_purchase as lastPurchase,");
            builder.append("d.last_purchase_amount as lastPurchaseAmount,d.business_asset_amount as businessAssetAmount,");
            builder.append(
                    "d.amount_at_cash as amountAtCash,d.amount_at_saving as amountAtSaving,d.amount_at_inventory as amountAtInventory,");
            builder.append(
                    "d.fixed_asset_cost as fixedAssetCost,d.total_in_tax as totalInTax,d.total_in_transport as totalInTransport,d.total_in_rent as totalInRent,");
            builder.append("d.total_in_communication as totalInCommunication,d.other_expense as otherExpense,");
            builder.append("d.other_expense_amount as otherExpenseAmount,d.total_utility as totalUtility,");
            builder.append(
                    "d.total_worker_salary as totalWorkerSalary,d.total_wage as totalWage,d.society as society, d.external_id as externalId,");
            builder.append(
                    "d.createdby_id as createdById,d.lastmodifiedby_id as lastModifiedById,d.created_date as createdDate,d.lastmodified_date as lastModifiedDate");
            builder.append(" FROM m_business_detail d ");
            builder.append(" inner join m_client mc on d.client_id = mc.id");
            builder.append(" left join m_code_value codeBusinessType on d.business_type_id = codeBusinessType.id");
            builder.append(" left join m_code_value codeSourceOfCapital on d.source_of_capital = codeSourceOfCapital.id");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ClientBusinessDetailData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String externalId = rs.getString("externalId");

            final Long businessType = JdbcSupport.getLong(rs, "businessTypeId");
            final String businessTypeCode = rs.getString("businessTypeCode");
            final CodeValueData businessTypeId = CodeValueData.instance(businessType, businessTypeCode);

            final LocalDate businessCreationDate = JdbcSupport.getLocalDate(rs, "businessCreationDate");
            final BigDecimal startingCapital = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "startingCapital");

            final Long sourceOfCapital = JdbcSupport.getLong(rs, "sourceOfCapitalId");
            final String codeSourceOfCapitalCode = rs.getString("codeSourceOfCapitalCode");
            final CodeValueData sourceOfCapitalId = CodeValueData.instance(sourceOfCapital, codeSourceOfCapitalCode);

            final Long totalEmployee = JdbcSupport.getLong(rs, "totalEmployee");
            final BigDecimal businessRevenue = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "businessRevenue");
            final BigDecimal averageMonthlyRevenue = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "averageMonthlyRevenue");
            final Integer bestMonthEnum = JdbcSupport.getInteger(rs, "bestMonth");

            EnumOptionData bestMonth = null;
            if (bestMonthEnum != null) {
                bestMonth = ClientEnumerations.monthEnum(bestMonthEnum);
            }
            final String reasonForBestMonth = rs.getString("reasonForBestMonth");
            final Integer worstMonthEnum = JdbcSupport.getInteger(rs, "worstMonth");
            EnumOptionData worstMonth = null;
            if (worstMonthEnum != null) {
                worstMonth = ClientEnumerations.monthEnum(worstMonthEnum);
            }

            final String reasonForWorstMonth = rs.getString("reasonForWorstMonth");
            final Long numberOfPurchase = JdbcSupport.getLong(rs, "numberOfPurchase");
            final String purchaseFrequency = rs.getString("purchaseFrequency");
            final BigDecimal totalPurchaseLastMonth = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalPurchaseLastMonth");
            final Integer lastPurchaseEnum = JdbcSupport.getInteger(rs, "lastPurchase");

            EnumOptionData lastPurchase = null;
            if (lastPurchaseEnum != null) {
                lastPurchase = ClientEnumerations.monthEnum(lastPurchaseEnum);
            }

            final BigDecimal lastPurchaseAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lastPurchaseAmount");
            final BigDecimal businessAssetAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "businessAssetAmount");
            final BigDecimal amountAtCash = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "amountAtCash");
            final BigDecimal amountAtSaving = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "amountAtSaving");
            final BigDecimal amountAtInventory = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "amountAtInventory");
            final BigDecimal fixedAssetCost = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "fixedAssetCost");
            final BigDecimal totalInTax = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalInTax");
            final BigDecimal totalInTransport = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalInTransport");
            final BigDecimal totalInRent = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalInRent");
            final BigDecimal totalInCommunication = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalInCommunication");
            final String otherExpense = rs.getString("otherExpense");
            final BigDecimal otherExpenseAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "otherExpenseAmount");
            final BigDecimal totalUtility = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalUtility");
            final BigDecimal totalWorkerSalary = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalWorkerSalary");
            final BigDecimal totalWage = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalWage");
            final BigDecimal society = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "society");

            return ClientBusinessDetailData.instance(id, clientId, businessTypeId, businessCreationDate, startingCapital, sourceOfCapitalId,
                    totalEmployee, businessRevenue, averageMonthlyRevenue, bestMonth, reasonForBestMonth, worstMonth, reasonForWorstMonth,
                    numberOfPurchase, purchaseFrequency, totalPurchaseLastMonth, lastPurchase, lastPurchaseAmount, businessAssetAmount,
                    amountAtCash, amountAtSaving, amountAtInventory, fixedAssetCost, totalInTax, totalInTransport, totalInRent,
                    totalInCommunication, otherExpense, otherExpenseAmount, totalUtility, totalWorkerSalary, totalWage, externalId,
                    society);

        }
    }

}
