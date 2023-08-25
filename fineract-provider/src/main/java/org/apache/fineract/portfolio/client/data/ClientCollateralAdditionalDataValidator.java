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
public final class ClientCollateralAdditionalDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ClientCollateralAdditionalDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ClientApiConstants.CLIENT_COLLATERAL_ADDITIONAL_DATA_RESPONSE_REQUEST_PARAMETER);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_COLLATERAL_ADDITIONAL_DATA_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.upiNoParamName, element)) {
            final String upiNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.upiNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.upiNoParamName).value(upiNo).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.chassisNoParamName, element)) {
            final String cassisNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.chassisNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.chassisNoParamName).value(cassisNo).notBlank();
        }
        final String collateralOwnerFirst = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.collateralOwnerFirstParamName,
                element);
        baseDataValidator.reset().parameter(ClientApiConstants.collateralOwnerFirstParamName).value(collateralOwnerFirst).notBlank()
                .notExceedingLengthOf(200);

        final String idNoOfCollateralOwnerFirst = this.fromApiJsonHelper
                .extractStringNamed(ClientApiConstants.idNoOfCollateralOwnerFirstParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.idNoOfCollateralOwnerFirstParamName).value(idNoOfCollateralOwnerFirst)
                .notBlank().notExceedingLengthOf(200);

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.collateralOwnerSecondParamName, element)) {
            final String collateralOwnerSecond = this.fromApiJsonHelper
                    .extractStringNamed(ClientApiConstants.collateralOwnerSecondParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.collateralOwnerSecondParamName).value(collateralOwnerSecond).notBlank()
                    .notExceedingLengthOf(200);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.idNoOfCollateralOwnerSecondParamName, element)) {
            final String idNoOfCollateralOwnerSecond = this.fromApiJsonHelper
                    .extractStringNamed(ClientApiConstants.idNoOfCollateralOwnerSecondParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.idNoOfCollateralOwnerSecondParamName).value(idNoOfCollateralOwnerSecond)
                    .notBlank().notExceedingLengthOf(200);
        }

        final BigDecimal worthOfCollateral = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(ClientApiConstants.worthOfCollateralParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.worthOfCollateralParamName).value(worthOfCollateral).notNull()
                .positiveAmount();

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.provinceIdParamName, element)) {
            final Integer provinceId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.provinceIdParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.provinceIdParamName).value(provinceId).notBlank()
                    .notExceedingLengthOf(200);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.districtIdParamName, element)) {
            final Integer districtId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.districtIdParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.districtIdParamName).value(districtId).notBlank()
                    .notExceedingLengthOf(200);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.sectorIdParamName, element)) {
            final Integer sectorId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.sectorIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.sectorIdParamName).value(sectorId).notBlank().notExceedingLengthOf(200);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.cellIdParamName, element)) {
            final Integer cellId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.cellIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.cellIdParamName).value(cellId).notBlank().notExceedingLengthOf(200);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.villageIdParamName, element)) {
            final Integer villageId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.villageIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.villageIdParamName).value(villageId).notBlank()
                    .notExceedingLengthOf(200);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateForUpdate(String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ClientApiConstants.CLIENT_COLLATERAL_ADDITIONAL_DATA_RESPONSE_REQUEST_PARAMETER);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_COLLATERAL_ADDITIONAL_DATA_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.upiNoParamName, element)) {
            final String upiNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.upiNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.upiNoParamName).value(upiNo).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.chassisNoParamName, element)) {
            final String cassisNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.chassisNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.chassisNoParamName).value(cassisNo).notBlank();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.collateralOwnerFirstParamName, element)) {
            final String collateralOwnerFirst = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.collateralOwnerFirstParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.collateralOwnerFirstParamName).value(collateralOwnerFirst).notBlank()
                    .notExceedingLengthOf(200);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.idNoOfCollateralOwnerFirstParamName, element)) {
            final String idNoOfCollateralOwnerFirst = this.fromApiJsonHelper
                    .extractStringNamed(ClientApiConstants.idNoOfCollateralOwnerFirstParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.idNoOfCollateralOwnerFirstParamName).value(idNoOfCollateralOwnerFirst)
                    .notBlank().notExceedingLengthOf(200);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.collateralOwnerSecondParamName, element)) {
            final String collateralOwnerSecond = this.fromApiJsonHelper
                    .extractStringNamed(ClientApiConstants.collateralOwnerSecondParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.collateralOwnerSecondParamName).value(collateralOwnerSecond).notBlank()
                    .notExceedingLengthOf(200);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.idNoOfCollateralOwnerSecondParamName, element)) {
            final String idNoOfCollateralOwnerSecond = this.fromApiJsonHelper
                    .extractStringNamed(ClientApiConstants.idNoOfCollateralOwnerSecondParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.idNoOfCollateralOwnerSecondParamName).value(idNoOfCollateralOwnerSecond)
                    .notBlank().notExceedingLengthOf(200);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.worthOfCollateralParamName, element)) {
            final BigDecimal worthOfCollateral = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(ClientApiConstants.worthOfCollateralParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.worthOfCollateralParamName).value(worthOfCollateral).notNull()
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.provinceIdParamName, element)) {
            final Integer provinceId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.provinceIdParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.provinceIdParamName).value(provinceId).notBlank()
                    .notExceedingLengthOf(200);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.districtIdParamName, element)) {
            final Integer districtId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.districtIdParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.districtIdParamName).value(districtId).notBlank()
                    .notExceedingLengthOf(200);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.sectorIdParamName, element)) {
            final Integer sectorId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.sectorIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.sectorIdParamName).value(sectorId).notBlank().notExceedingLengthOf(200);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.cellIdParamName, element)) {
            final Integer cellId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.cellIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.cellIdParamName).value(cellId).notBlank().notExceedingLengthOf(200);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.villageIdParamName, element)) {
            final Integer villageId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.villageIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.villageIdParamName).value(villageId).notBlank()
                    .notExceedingLengthOf(200);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }
    }

}
