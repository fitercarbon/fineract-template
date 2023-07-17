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

package org.apache.fineract.portfolio.client.data;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ClientBusinessDetailDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ClientBusinessDetailDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ClientApiCollectionConstants.CLIENT_BUSINESS_DETAIL_CREATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_BUSINESS_DETAIL_RESOURCE_NAME);

        final String businessTypeParameterName = "businessType";
        final Long businessType = this.fromApiJsonHelper.extractLongNamed(businessTypeParameterName, element);
        baseDataValidator.reset().parameter(businessTypeParameterName).value(businessType).notNull().integerGreaterThanZero();

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIdParamName).value(externalId).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }

        LocalDate businessCreationDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.BUSINESS_CREATION_DATE, element);
        baseDataValidator.reset().parameter(ClientApiConstants.BUSINESS_CREATION_DATE).value(businessCreationDate).notNull();

        final String sourceOfCapitalParameterName = "sourceOfCapital";
        final Long sourceOfCapital = this.fromApiJsonHelper.extractLongNamed(sourceOfCapitalParameterName, element);
        baseDataValidator.reset().parameter(sourceOfCapitalParameterName).value(sourceOfCapital).notNull().integerGreaterThanZero();

        final BigDecimal startingCapital = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("startingCapital", element);
        baseDataValidator.reset().parameter("startingCapital").value(startingCapital).notNull().positiveAmount();

        final Long totalEmployee = this.fromApiJsonHelper.extractLongNamed("totalEmployee", element);
        baseDataValidator.reset().parameter("totalEmployee").value(totalEmployee).notNull().integerGreaterThanZero();

        final BigDecimal businessRevenue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("businessRevenue", element);
        baseDataValidator.reset().parameter("businessRevenue").value(businessRevenue).notNull().positiveAmount();

        final BigDecimal averageMonthlyRevenue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("averageMonthlyRevenue", element);
        baseDataValidator.reset().parameter("averageMonthlyRevenue").value(averageMonthlyRevenue).notNull().positiveAmount();

        final String reasonForBestMonthParameterName = "reasonForBestMonth";
        final String reasonForBestMonth = this.fromApiJsonHelper.extractStringNamed(reasonForBestMonthParameterName, element);
        baseDataValidator.reset().parameter(reasonForBestMonthParameterName).value(reasonForBestMonth).notNull().notExceedingLengthOf(500);

        final String bestMonthParameterName = "bestMonth";
        final Integer bestMonth = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(bestMonthParameterName, element);
        baseDataValidator.reset().parameter(bestMonthParameterName).value(bestMonth).notNull().notExceedingLengthOf(12);

        final String reasonForWorstMonthParameterName = "reasonForWorstMonth";
        final String reasonForWorstMonth = this.fromApiJsonHelper.extractStringNamed(reasonForWorstMonthParameterName, element);
        baseDataValidator.reset().parameter(reasonForWorstMonthParameterName).value(reasonForWorstMonth).notNull()
                .notExceedingLengthOf(500);

        final String worstMonthParameterName = "worstMonth";
        final Integer worstMonth = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(worstMonthParameterName, element);
        baseDataValidator.reset().parameter(worstMonthParameterName).value(worstMonth).notNull().notExceedingLengthOf(12);

        final Long numberOfPurchase = this.fromApiJsonHelper.extractLongNamed("numberOfPurchase", element);
        baseDataValidator.reset().parameter("numberOfPurchase").value(numberOfPurchase).notNull().integerGreaterThanZero();

        final String purchaseFrequencyParameterName = "purchaseFrequency";
        final String purchaseFrequency = this.fromApiJsonHelper.extractStringNamed(purchaseFrequencyParameterName, element);
        baseDataValidator.reset().parameter(purchaseFrequencyParameterName).value(purchaseFrequency).notNull().notExceedingLengthOf(500);

        final BigDecimal lastPurchaseAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("lastPurchaseAmount", element);
        baseDataValidator.reset().parameter("lastPurchaseAmount").value(lastPurchaseAmount).notNull().positiveAmount();

        final BigDecimal businessAssetAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("businessAssetAmount", element);
        baseDataValidator.reset().parameter("businessAssetAmount").value(businessAssetAmount).notNull().positiveAmount();

        final BigDecimal amountAtCash = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amountAtCash", element);
        baseDataValidator.reset().parameter("amountAtCash").value(amountAtCash).notNull().positiveAmount();

        final BigDecimal amountAtSaving = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amountAtSaving", element);
        baseDataValidator.reset().parameter("amountAtSaving").value(amountAtSaving).notNull().positiveAmount();

        final BigDecimal amountAtInventory = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amountAtInventory", element);
        baseDataValidator.reset().parameter("amountAtInventory").value(amountAtInventory).notNull().positiveAmount();

        final BigDecimal fixedAssetCost = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("fixedAssetCost", element);
        baseDataValidator.reset().parameter("fixedAssetCost").value(fixedAssetCost).notNull().positiveAmount();

        final BigDecimal totalInTax = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("totalInTax", element);
        baseDataValidator.reset().parameter("totalInTax").value(totalInTax).notNull().positiveAmount();

        final BigDecimal totalInTransport = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("totalInTransport", element);
        baseDataValidator.reset().parameter("totalInTransport").value(totalInTransport).notNull().positiveAmount();

        final BigDecimal totalInRent = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("totalInRent", element);
        baseDataValidator.reset().parameter("totalInRent").value(totalInRent).notNull().positiveAmount();

        final BigDecimal totalInCommunication = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("totalInCommunication", element);
        baseDataValidator.reset().parameter("totalInCommunication").value(totalInCommunication).notNull().positiveAmount();

        final String otherExpenseParameterName = "otherExpense";
        if (this.fromApiJsonHelper.parameterExists(otherExpenseParameterName, element)) {
            final String otherExpense = this.fromApiJsonHelper.extractStringNamed(otherExpenseParameterName, element);
            baseDataValidator.reset().parameter(otherExpenseParameterName).value(otherExpense).ignoreIfNull().notExceedingLengthOf(500);
        }

        final String otherExpenseAmountParameterName = "otherExpenseAmount";
        if (this.fromApiJsonHelper.parameterExists(otherExpenseAmountParameterName, element)) {
            final BigDecimal otherExpenseAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(otherExpenseAmountParameterName,
                    element);
            baseDataValidator.reset().parameter(otherExpenseAmountParameterName).value(otherExpenseAmount).ignoreIfNull().positiveAmount();
        }

        final BigDecimal totalUtility = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("totalUtility", element);
        baseDataValidator.reset().parameter("totalUtility").value(totalUtility).notNull().positiveAmount();

        final String totalWorkerSalaryParameterName = "totalWorkerSalary";
        if (this.fromApiJsonHelper.parameterExists(totalWorkerSalaryParameterName, element)) {
            final BigDecimal totalWorkerSalary = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(totalWorkerSalaryParameterName,
                    element);
            baseDataValidator.reset().parameter(totalWorkerSalaryParameterName).value(totalWorkerSalary).ignoreIfNull().positiveAmount();
        }
        final String totalWageParameterName = "totalWage";
        if (this.fromApiJsonHelper.parameterExists(totalWageParameterName, element)) {
            final BigDecimal totalWage = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(totalWageParameterName, element);
            baseDataValidator.reset().parameter(totalWageParameterName).value(totalWage).ignoreIfNull().positiveAmount();
        }

        final String societyName = "society";
        if (this.fromApiJsonHelper.parameterExists(societyName, element)) {
            final BigDecimal society = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(societyName, element);
            baseDataValidator.reset().parameter(societyName).value(society).ignoreIfNull().positiveAmount();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }
    }

    public void validateForUpdate(String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ClientApiCollectionConstants.CLIENT_BUSINESS_DETAIL_UPDATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_BUSINESS_DETAIL_RESOURCE_NAME);

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.BUSINESS_TYPE, element)) {
            final Long businessType = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.BUSINESS_TYPE, element);
            baseDataValidator.reset().parameter(ClientApiConstants.BUSINESS_TYPE).value(businessType).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIdParamName).value(externalId).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.BUSINESS_CREATION_DATE, element)) {
            LocalDate businessCreationDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.BUSINESS_CREATION_DATE,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.BUSINESS_CREATION_DATE).value(businessCreationDate).notNull();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.SOURCE_OF_CAPITAL, element)) {
            final Long sourceOfCapital = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.SOURCE_OF_CAPITAL, element);
            baseDataValidator.reset().parameter(ClientApiConstants.SOURCE_OF_CAPITAL).value(sourceOfCapital).notNull()
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.STARTING_CAPITAL, element)) {
            final BigDecimal startingCapital = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.STARTING_CAPITAL,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.STARTING_CAPITAL).value(startingCapital).notNull().positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.TOTAL_EMPLOYEE, element)) {
            final Long totalEmployee = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.TOTAL_EMPLOYEE, element);
            baseDataValidator.reset().parameter(ClientApiConstants.TOTAL_EMPLOYEE).value(totalEmployee).notNull().integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.BUSINESS_REVENUE, element)) {
            final BigDecimal businessRevenue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.BUSINESS_REVENUE,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.BUSINESS_REVENUE).value(businessRevenue).notNull().positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.AVERAGE_MONTHLY_REVENUE, element)) {
            final BigDecimal averageMonthlyRevenue = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(ClientApiConstants.AVERAGE_MONTHLY_REVENUE, element);
            baseDataValidator.reset().parameter(ClientApiConstants.AVERAGE_MONTHLY_REVENUE).value(averageMonthlyRevenue).notNull()
                    .positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.REASON_FOR_BEST_MONTH, element)) {
            final String reasonForBestMonth = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.REASON_FOR_BEST_MONTH, element);
            baseDataValidator.reset().parameter(ClientApiConstants.REASON_FOR_BEST_MONTH).value(reasonForBestMonth).notNull()
                    .notExceedingLengthOf(500);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.BEST_MONTH, element)) {
            final Integer bestMonth = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(ClientApiConstants.BEST_MONTH, element);
            baseDataValidator.reset().parameter(ClientApiConstants.BEST_MONTH).value(bestMonth).notNull().notExceedingLengthOf(12);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.REASON_FOR_WORST_MONTH, element)) {
            final String reasonForWorstMonth = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.REASON_FOR_WORST_MONTH,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.REASON_FOR_WORST_MONTH).value(reasonForWorstMonth).notNull()
                    .notExceedingLengthOf(500);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.WORST_MONTH, element)) {
            final Integer worstMonth = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(ClientApiConstants.WORST_MONTH, element);
            baseDataValidator.reset().parameter(ClientApiConstants.WORST_MONTH).value(worstMonth).notNull().notExceedingLengthOf(12);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.NUMBER_OF_PURCHASE, element)) {
            final Long numberOfPurchase = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.NUMBER_OF_PURCHASE, element);
            baseDataValidator.reset().parameter(ClientApiConstants.NUMBER_OF_PURCHASE).value(numberOfPurchase).notNull()
                    .integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.PURCHASE_FREQUENCY, element)) {
            final String purchaseFrequency = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.PURCHASE_FREQUENCY, element);
            baseDataValidator.reset().parameter(ClientApiConstants.PURCHASE_FREQUENCY).value(purchaseFrequency).notNull();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.LAST_PURCHASE_AMOUNT, element)) {
            final BigDecimal lastPurchaseAmount = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(ClientApiConstants.LAST_PURCHASE_AMOUNT, element);
            baseDataValidator.reset().parameter(ClientApiConstants.LAST_PURCHASE_AMOUNT).value(lastPurchaseAmount).notNull()
                    .positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.BUSINESS_ASSET_AMOUNT, element)) {
            final BigDecimal businessAssetAmount = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(ClientApiConstants.BUSINESS_ASSET_AMOUNT, element);
            baseDataValidator.reset().parameter(ClientApiConstants.BUSINESS_ASSET_AMOUNT).value(businessAssetAmount).notNull()
                    .positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.AMOUNT_AT_CASH, element)) {
            final BigDecimal amountAtCash = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.AMOUNT_AT_CASH,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.AMOUNT_AT_CASH).value(amountAtCash).notNull().positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.AMOUNT_AT_SAVING, element)) {
            final BigDecimal amountAtSaving = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.AMOUNT_AT_SAVING,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.AMOUNT_AT_SAVING).value(amountAtSaving).notNull().positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.AMOUNT_AT_INVENTORY, element)) {
            final BigDecimal amountAtInventory = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(ClientApiConstants.AMOUNT_AT_INVENTORY, element);
            baseDataValidator.reset().parameter(ClientApiConstants.AMOUNT_AT_INVENTORY).value(amountAtInventory).notNull().positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.FIXED_ASSET_COST, element)) {
            final BigDecimal fixedAssetCost = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.FIXED_ASSET_COST,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.FIXED_ASSET_COST).value(fixedAssetCost).notNull().positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.TOTAL_IN_TAX, element)) {
            final BigDecimal totalInTax = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.TOTAL_IN_TAX, element);
            baseDataValidator.reset().parameter(ClientApiConstants.TOTAL_IN_TAX).value(totalInTax).notNull().positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.TOTAL_IN_TRANSPORT, element)) {
            final BigDecimal totalInTransport = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(ClientApiConstants.TOTAL_IN_TRANSPORT, element);
            baseDataValidator.reset().parameter(ClientApiConstants.TOTAL_IN_TRANSPORT).value(totalInTransport).notNull().positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.TOTAL_IN_RENT, element)) {
            final BigDecimal totalInRent = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.TOTAL_IN_RENT,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.TOTAL_IN_RENT).value(totalInRent).notNull().positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.TOTAL_IN_COMMUNICATION, element)) {
            final BigDecimal totalInCommunication = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(ClientApiConstants.TOTAL_IN_COMMUNICATION, element);
            baseDataValidator.reset().parameter(ClientApiConstants.TOTAL_IN_COMMUNICATION).value(totalInCommunication).notNull()
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.OTHER_EXPENSE, element)) {
            final String otherExpense = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.OTHER_EXPENSE, element);
            baseDataValidator.reset().parameter(ClientApiConstants.OTHER_EXPENSE).value(otherExpense).ignoreIfNull()
                    .notExceedingLengthOf(500);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.OTHER_EXPENSE_AMOUNT, element)) {
            final BigDecimal otherExpenseAmount = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(ClientApiConstants.OTHER_EXPENSE_AMOUNT, element);
            baseDataValidator.reset().parameter(ClientApiConstants.OTHER_EXPENSE_AMOUNT).value(otherExpenseAmount).ignoreIfNull()
                    .positiveAmount();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.TOTAL_UTILITY, element)) {
            final BigDecimal totalUtility = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.TOTAL_UTILITY,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.TOTAL_UTILITY).value(totalUtility).notNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.TOTAL_WORKER_SALARY, element)) {
            final BigDecimal totalWorkerSalary = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(ClientApiConstants.TOTAL_WORKER_SALARY, element);
            baseDataValidator.reset().parameter(ClientApiConstants.TOTAL_WORKER_SALARY).value(totalWorkerSalary).ignoreIfNull()
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.TOTAL_WAGE, element)) {
            final BigDecimal totalWage = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.TOTAL_WAGE, element);
            baseDataValidator.reset().parameter(ClientApiConstants.TOTAL_WAGE).value(totalWage).ignoreIfNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.SOCIETY, element)) {
            final BigDecimal society = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ClientApiConstants.SOCIETY, element);
            baseDataValidator.reset().parameter(ClientApiConstants.SOCIETY).value(society).ignoreIfNull().positiveAmount();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

}
