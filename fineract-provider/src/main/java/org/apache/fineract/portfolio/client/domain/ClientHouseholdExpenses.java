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
package org.apache.fineract.portfolio.client.domain;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;

@Entity
@Getter
@Table(name = "m_client_household_expenses")
public class ClientHouseholdExpenses extends AbstractAuditableCustom {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "food_expenses_amount", scale = 6, precision = 19)
    private BigDecimal foodExpensesAmount;

    @Column(name = "school_fees_amount", scale = 6, precision = 19)
    private BigDecimal schoolFessAmount;

    @Column(name = "rent_amount", scale = 6, precision = 19)
    private BigDecimal rentAmount;

    @Column(name = "utilities_amount", scale = 6, precision = 19)
    private BigDecimal utilitiesAmount;

    @Column(name = "external_id", length = 100, unique = true)
    private String externalId;

    @OneToMany(mappedBy = "clientHouseholdExpenses", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<OtherClientHouseholdExpenses> otherExpenses;

    public ClientHouseholdExpenses() {}

    public ClientHouseholdExpenses(final Client client, final BigDecimal foodExpensesAmount, final BigDecimal schoolFessAmount,
            final BigDecimal rentAmount, final BigDecimal utilitiesAmount, final String externalId) {
        this.client = client;
        this.foodExpensesAmount = foodExpensesAmount;
        this.schoolFessAmount = schoolFessAmount;
        this.rentAmount = rentAmount;
        this.utilitiesAmount = utilitiesAmount;
        this.externalId = externalId;
    }

    public static ClientHouseholdExpenses fromJson(final Client client, final JsonCommand command) {
        final String externalId = command.stringValueOfParameterNamed(ClientApiConstants.externalIdParamName);
        final BigDecimal utilitiesAmount = command.bigDecimalValueOfParameterNamed(ClientApiConstants.utilitiesAmountParamName, Locale.US);
        final BigDecimal rentAmount = command.bigDecimalValueOfParameterNamed(ClientApiConstants.rentAmountParamName, Locale.US);
        final BigDecimal schoolFessAmount = command.bigDecimalValueOfParameterNamed(ClientApiConstants.schoolFessAmountParamName,
                Locale.US);
        final BigDecimal foodExpensesAmountParamName = command
                .bigDecimalValueOfParameterNamed(ClientApiConstants.foodExpensesAmountParamName, Locale.US);
        return new ClientHouseholdExpenses(client, foodExpensesAmountParamName, schoolFessAmount, rentAmount, utilitiesAmount, externalId);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(4);

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.utilitiesAmountParamName, this.utilitiesAmount, Locale.US)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.utilitiesAmountParamName, Locale.US);
            this.utilitiesAmount = newValue;
            actualChanges.put(ClientApiConstants.utilitiesAmountParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.externalIdParamName);
            this.externalId = newValue;
            actualChanges.put(ClientApiConstants.externalIdParamName, newValue);
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.rentAmountParamName, this.rentAmount, Locale.US)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.rentAmountParamName, Locale.US);
            this.rentAmount = newValue;
            actualChanges.put(ClientApiConstants.rentAmountParamName, newValue);
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.schoolFessAmountParamName, this.schoolFessAmount, Locale.US)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.schoolFessAmountParamName, Locale.US);
            this.schoolFessAmount = newValue;
            actualChanges.put(ClientApiConstants.schoolFessAmountParamName, newValue);
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.foodExpensesAmountParamName, this.foodExpensesAmount,
                Locale.US)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.foodExpensesAmountParamName, Locale.US);
            this.foodExpensesAmount = newValue;
            actualChanges.put(ClientApiConstants.foodExpensesAmountParamName, newValue);
        }

        return actualChanges;
    }

    public void updateOtherExpenses(Set<OtherClientHouseholdExpenses> updatedExpenses) {
        // Remove any expenses that are not in the updated list
        this.otherExpenses.removeIf(existingExpense -> !updatedExpenses.contains(existingExpense));

        // Add or update the expenses from the updated list
        for (OtherClientHouseholdExpenses updatedExpense : updatedExpenses) {
            Optional<OtherClientHouseholdExpenses> existingExpense = this.otherExpenses.stream()
                    .filter(expense -> expense.equals(updatedExpense)).findFirst();

            if (existingExpense.isPresent()) {
                // Update the existing expense
                existingExpense.get().setAmount(updatedExpense.getAmount());
            } else {
                // Add the new expense
                updatedExpense.setClientHouseholdExpenses(this);
                this.otherExpenses.add(updatedExpense);
            }
        }
    }

}
