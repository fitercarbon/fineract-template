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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public final class ClientHouseholdExpensesData implements Serializable {

    private final Long id;

    private final Long clientId;

    private final BigDecimal foodExpensesAmount;

    private final BigDecimal schoolFessAmount;

    private final BigDecimal rentAmount;

    private final BigDecimal utilitiesAmount;

    private Collection<OtherExpensesData> otherExpensesData;

    private Collection<CodeValueData> otherExpenses;

    public static ClientHouseholdExpensesData instance(final Long id, final Long clientId, final BigDecimal foodExpensesAmount,
            final BigDecimal schoolFessAmount, final BigDecimal rentAmount, final BigDecimal utilitiesAmount) {
        return new ClientHouseholdExpensesData(id, clientId, foodExpensesAmount, schoolFessAmount, rentAmount, utilitiesAmount);
    }

    public static ClientHouseholdExpensesData withOtherExpensesTypes(ClientHouseholdExpensesData clientHouseholdExpensesData,
            final Collection<CodeValueData> otherExpenses) {
        return new ClientHouseholdExpensesData(clientHouseholdExpensesData.id, clientHouseholdExpensesData.clientId,
                clientHouseholdExpensesData.foodExpensesAmount, clientHouseholdExpensesData.schoolFessAmount,
                clientHouseholdExpensesData.rentAmount, clientHouseholdExpensesData.utilitiesAmount,
                clientHouseholdExpensesData.otherExpensesData, otherExpenses);
    }

    public static ClientHouseholdExpensesData withOtherExpensesData(ClientHouseholdExpensesData clientHouseholdExpensesData,
            final Collection<OtherExpensesData> otherExpensesData) {
        return new ClientHouseholdExpensesData(clientHouseholdExpensesData.id, clientHouseholdExpensesData.clientId,
                clientHouseholdExpensesData.foodExpensesAmount, clientHouseholdExpensesData.schoolFessAmount,
                clientHouseholdExpensesData.rentAmount, clientHouseholdExpensesData.utilitiesAmount, otherExpensesData,
                clientHouseholdExpensesData.otherExpenses);
    }

    public static ClientHouseholdExpensesData empty() {
        return new ClientHouseholdExpensesData(null, null, null, null, null, null, null, null);
    }
}
