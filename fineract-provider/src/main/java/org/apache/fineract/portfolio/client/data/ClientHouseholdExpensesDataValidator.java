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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
public class ClientHouseholdExpensesDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ClientHouseholdExpensesDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateAdd(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiConstants.CLIENT_HOUSEHOLD_EXPENSES_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_HOUSEHOLD_EXPENSES_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final BigDecimal foodExpensesAmount = this.fromApiJsonHelper.extractBigDecimalNamed(ClientApiConstants.foodExpensesAmountParamName,
                element, Locale.US);
        baseDataValidator.reset().parameter(ClientApiConstants.foodExpensesAmountParamName).value(foodExpensesAmount).notNull()
                .positiveAmount();

        final BigDecimal schoolFessAmount = this.fromApiJsonHelper.extractBigDecimalNamed(ClientApiConstants.schoolFessAmountParamName,
                element, Locale.US);
        baseDataValidator.reset().parameter(ClientApiConstants.schoolFessAmountParamName).value(schoolFessAmount).positiveAmount();

        final BigDecimal utilitiesAmount = this.fromApiJsonHelper.extractBigDecimalNamed(ClientApiConstants.utilitiesAmountParamName,
                element, Locale.US);
        baseDataValidator.reset().parameter(ClientApiConstants.utilitiesAmountParamName).value(utilitiesAmount).positiveAmount();

        validateOtherExpensesList(baseDataValidator, element);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateOtherExpensesList(DataValidatorBuilder baseDataValidator, JsonElement element) {
        if (this.fromApiJsonHelper.extractJsonArrayNamed(ClientApiConstants.otherExpensesListParamName, element) != null) {
            final JsonArray otherExpensesList = this.fromApiJsonHelper.extractJsonArrayNamed(ClientApiConstants.otherExpensesListParamName,
                    element);
            otherExpensesList.forEach(otherExpenses -> {
                final BigDecimal otherExpensesAmount = this.fromApiJsonHelper
                        .extractBigDecimalNamed(ClientApiConstants.otherExpensesAmountParamName, otherExpenses, Locale.US);
                baseDataValidator.reset().parameter(ClientApiConstants.otherExpensesAmountParamName).value(otherExpensesAmount).notNull()
                        .positiveAmount();
                final Long otherExpensesEnum = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.otherExpensesIdParamName,
                        otherExpenses);
                baseDataValidator.reset().parameter(ClientApiConstants.otherExpensesIdParamName).value(otherExpensesEnum).notNull();
            });

        }
    }

    public void validateUpdate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiConstants.CLIENT_HOUSEHOLD_EXPENSES_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_HOUSEHOLD_EXPENSES_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        validateOtherExpensesList(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

}
