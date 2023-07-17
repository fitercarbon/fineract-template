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
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;

@Entity
@Table(name = "m_business_detail")
public class ClientBusinessDetail extends AbstractAuditableCustom {

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "business_type_id")
    private CodeValue businessType;
    @Column(name = "business_creation_date")
    private LocalDate businessCreationDate;
    @Column(name = "starting_capital")
    private BigDecimal startingCapital;
    @ManyToOne
    @JoinColumn(name = "source_of_capital")
    private CodeValue sourceOfCapital;
    @Column(name = "total_employee")
    private Long totalEmployee;
    @Column(name = "business_revenue")
    private BigDecimal businessRevenue;
    @Column(name = "average_monthly_revenue")
    private BigDecimal averageMonthlyRevenue;

    @Column(name = "best_month")
    private Integer bestMonth;
    @Column(name = "reason_for_best_month")
    private String reasonForBestMonth;

    @Column(name = "worst_month")
    private Integer worstMonth;
    @Column(name = "reason_for_worst_month")
    private String reasonForWorstMonth;
    @Column(name = "number_of_purchase")
    private Long numberOfPurchase;
    @Column(name = "purchase_frequency")
    private String purchaseFrequency;
    @Column(name = "total_purchase_last_month")
    private BigDecimal totalPurchaseLastMonth;
    @Column(name = "last_purchase")
    private Integer whenLastPurchase;
    @Column(name = "last_purchase_amount")
    private BigDecimal lastPurchaseAmount;
    @Column(name = "business_asset_amount")
    private BigDecimal businessAssetAmount;
    @Column(name = "amount_at_cash")
    private BigDecimal amountAtCash;
    @Column(name = "amount_at_saving")
    private BigDecimal amountAtSaving;
    @Column(name = "amount_at_inventory")
    private BigDecimal amountAtInventory;
    @Column(name = "fixed_asset_cost")
    private BigDecimal fixedAssetCost;
    @Column(name = "total_in_tax")
    private BigDecimal totalInTax;
    @Column(name = "total_in_transport")
    private BigDecimal totalInTransport;
    @Column(name = "total_in_rent")
    private BigDecimal totalInRent;
    @Column(name = "total_in_communication")
    private BigDecimal totalInCommunication;
    @Column(name = "other_expense")
    private String otherExpense;
    @Column(name = "other_expense_amount")
    private BigDecimal otherExpenseAmount;
    @Column(name = "total_utility")
    private BigDecimal totalUtility;
    @Column(name = "total_worker_salary")
    private BigDecimal totalWorkerSalary;
    @Column(name = "total_wage")
    private BigDecimal totalWage;
    @Column(name = "external_id")
    private String externalId;
    @Column(name = "society")
    private BigDecimal society;

    public ClientBusinessDetail() {}

    public static ClientBusinessDetail createNew(final Client client, final CodeValue businessType, final CodeValue sourceOfCapital,
            LocalDate businessCreationDate, BigDecimal startingCapital, Long totalEmployee, BigDecimal businessRevenue,
            BigDecimal averageMonthlyRevenue, Integer bestMonth, String reasonForBestMonth, Integer worstMonth, String reasonForWorstMonth,
            Long numberOfPurchase, String purchaseFrequency, BigDecimal totalPurchaseLastMonth, Integer lastPurchase,
            BigDecimal lastPurchaseAmount, BigDecimal businessAssetAmount, BigDecimal amountAtCash, BigDecimal amountAtSaving,
            BigDecimal amountAtInventory, BigDecimal fixedAssetCost, BigDecimal totalInTax, BigDecimal totalInTransport,
            BigDecimal totalInRent, BigDecimal totalInCommunication, String otherExpense, BigDecimal otherExpenseAmount,
            BigDecimal totalUtility, BigDecimal totalWorkerSalary, BigDecimal totalWage, String externalId, BigDecimal society) {

        return new ClientBusinessDetail(client, businessType, businessCreationDate, startingCapital, sourceOfCapital, totalEmployee,
                businessRevenue, averageMonthlyRevenue, bestMonth, reasonForBestMonth, worstMonth, reasonForWorstMonth, numberOfPurchase,
                purchaseFrequency, totalPurchaseLastMonth, lastPurchase, lastPurchaseAmount, businessAssetAmount, amountAtCash,
                amountAtSaving, amountAtInventory, fixedAssetCost, totalInTax, totalInTransport, totalInRent, totalInCommunication,
                otherExpense, otherExpenseAmount, totalUtility, totalWorkerSalary, totalWage, externalId, society);
    }

    public ClientBusinessDetail(Client client, CodeValue businessType, LocalDate businessCreationDate, BigDecimal startingCapital,
            CodeValue sourceOfCapital, Long totalEmployee, BigDecimal businessRevenue, BigDecimal averageMonthlyRevenue, Integer bestMonth,
            String reasonForBestMonth, Integer worstMonth, String reasonForWorstMonth, Long numberOfPurchase, String purchaseFrequency,
            BigDecimal totalPurchaseLastMonth, Integer lastPurchase, BigDecimal lastPurchaseAmount, BigDecimal businessAssetAmount,
            BigDecimal amountAtCash, BigDecimal amountAtSaving, BigDecimal amountAtInventory, BigDecimal fixedAssetCost,
            BigDecimal totalInTax, BigDecimal totalInTransport, BigDecimal totalInRent, BigDecimal totalInCommunication,
            String otherExpense, BigDecimal otherExpenseAmount, BigDecimal totalUtility, BigDecimal totalWorkerSalary, BigDecimal totalWage,
            String externalId, BigDecimal society) {
        this.client = client;
        this.businessType = businessType;
        this.businessCreationDate = businessCreationDate;
        this.startingCapital = startingCapital;
        this.sourceOfCapital = sourceOfCapital;
        this.totalEmployee = totalEmployee;
        this.businessRevenue = businessRevenue;
        this.averageMonthlyRevenue = averageMonthlyRevenue;
        this.bestMonth = bestMonth;
        this.reasonForBestMonth = reasonForBestMonth;
        this.worstMonth = worstMonth;
        this.reasonForWorstMonth = reasonForWorstMonth;
        this.numberOfPurchase = numberOfPurchase;
        this.purchaseFrequency = purchaseFrequency;
        this.totalPurchaseLastMonth = totalPurchaseLastMonth;
        this.whenLastPurchase = lastPurchase;
        this.lastPurchaseAmount = lastPurchaseAmount;
        this.businessAssetAmount = businessAssetAmount;
        this.amountAtCash = amountAtCash;
        this.amountAtSaving = amountAtSaving;
        this.amountAtInventory = amountAtInventory;
        this.fixedAssetCost = fixedAssetCost;
        this.totalInTax = totalInTax;
        this.totalInTransport = totalInTransport;
        this.totalInRent = totalInRent;
        this.totalInCommunication = totalInCommunication;
        this.otherExpense = otherExpense;
        this.otherExpenseAmount = otherExpenseAmount;
        this.totalUtility = totalUtility;
        this.totalWorkerSalary = totalWorkerSalary;
        this.totalWage = totalWage;
        this.externalId = externalId;
        this.society = society;
    }

    public Client getClient() {
        return client;
    }

    public CodeValue getBusinessType() {
        return businessType;
    }

    public LocalDate getBusinessCreationDate() {
        return businessCreationDate;
    }

    public BigDecimal getStartingCapital() {
        return startingCapital;
    }

    public CodeValue getSourceOfCapital() {
        return sourceOfCapital;
    }

    public Long getTotalEmployee() {
        return totalEmployee;
    }

    public String getExternalId() {
        return externalId;
    }

    public Long getBusinessTypeId() {
        Long businessTypeId = null;
        if (this.businessType != null) {
            businessTypeId = this.businessType.getId();
        }
        return businessTypeId;
    }

    public void updateBusinessType(CodeValue businessType) {
        this.businessType = businessType;
    }

    public Long getSourceOfCapitalId() {
        Long sourceOfCapitalId = null;
        if (this.sourceOfCapital != null) {
            sourceOfCapitalId = this.sourceOfCapital.getId();
        }
        return sourceOfCapitalId;
    }

    public void updateSourceOfCapital(CodeValue sourceOfCapital) {
        this.sourceOfCapital = sourceOfCapital;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);

        if (command.isChangeInLongParameterNamed(ClientApiConstants.BUSINESS_TYPE, getBusinessTypeId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.BUSINESS_TYPE);
            actualChanges.put(ClientApiConstants.BUSINESS_TYPE, newValue);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.externalIdParamName);
            actualChanges.put(ClientApiConstants.externalIdParamName, newValue);
            this.externalId = newValue;
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();
        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.BUSINESS_CREATION_DATE, this.businessCreationDate)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.BUSINESS_CREATION_DATE);
            actualChanges.put(ClientApiConstants.BUSINESS_CREATION_DATE, newValue);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);
            this.businessCreationDate = command.localDateValueOfParameterNamed(ClientApiConstants.BUSINESS_CREATION_DATE);
            ;
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.SOURCE_OF_CAPITAL, getSourceOfCapitalId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.SOURCE_OF_CAPITAL);
            actualChanges.put(ClientApiConstants.SOURCE_OF_CAPITAL, newValue);
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.STARTING_CAPITAL, this.startingCapital)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.STARTING_CAPITAL);
            actualChanges.put(ClientApiConstants.STARTING_CAPITAL, newValue);
            this.startingCapital = newValue;
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.TOTAL_EMPLOYEE, this.totalEmployee)) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.TOTAL_EMPLOYEE);
            actualChanges.put(ClientApiConstants.TOTAL_EMPLOYEE, newValue);
            this.totalEmployee = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.BUSINESS_REVENUE, this.businessRevenue)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.BUSINESS_REVENUE);
            actualChanges.put(ClientApiConstants.BUSINESS_REVENUE, newValue);
            this.businessRevenue = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.AVERAGE_MONTHLY_REVENUE, this.averageMonthlyRevenue)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.AVERAGE_MONTHLY_REVENUE);
            actualChanges.put(ClientApiConstants.AVERAGE_MONTHLY_REVENUE, newValue);
            this.averageMonthlyRevenue = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ClientApiConstants.BEST_MONTH, this.bestMonth)) {
            final Integer newValue = command.integerValueOfParameterNamed(ClientApiConstants.BEST_MONTH);
            actualChanges.put(ClientApiConstants.BEST_MONTH, newValue);
            this.bestMonth = newValue;
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.REASON_FOR_BEST_MONTH, this.reasonForBestMonth)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.REASON_FOR_BEST_MONTH);
            actualChanges.put(ClientApiConstants.REASON_FOR_BEST_MONTH, newValue);
            this.reasonForBestMonth = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ClientApiConstants.WORST_MONTH, this.worstMonth)) {
            final Integer newValue = command.integerValueOfParameterNamed(ClientApiConstants.WORST_MONTH);
            actualChanges.put(ClientApiConstants.WORST_MONTH, newValue);
            this.worstMonth = newValue;
        }
        if (command.isChangeInIntegerParameterNamed(ClientApiConstants.WHEN_LAST_PURCHASE, this.whenLastPurchase)) {
            final Integer newValue = command.integerValueOfParameterNamed(ClientApiConstants.WHEN_LAST_PURCHASE);
            actualChanges.put(ClientApiConstants.WHEN_LAST_PURCHASE, newValue);
            this.whenLastPurchase = newValue;
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.REASON_FOR_WORST_MONTH, this.reasonForWorstMonth)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.REASON_FOR_WORST_MONTH);
            actualChanges.put(ClientApiConstants.REASON_FOR_WORST_MONTH, newValue);
            this.reasonForWorstMonth = newValue;
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.NUMBER_OF_PURCHASE, this.numberOfPurchase)) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.NUMBER_OF_PURCHASE);
            actualChanges.put(ClientApiConstants.NUMBER_OF_PURCHASE, newValue);
            this.numberOfPurchase = newValue;
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.PURCHASE_FREQUENCY, this.purchaseFrequency)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.PURCHASE_FREQUENCY);
            actualChanges.put(ClientApiConstants.PURCHASE_FREQUENCY, newValue);
            this.purchaseFrequency = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.LAST_PURCHASE_AMOUNT, this.lastPurchaseAmount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.LAST_PURCHASE_AMOUNT);
            actualChanges.put(ClientApiConstants.LAST_PURCHASE_AMOUNT, newValue);
            this.lastPurchaseAmount = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.BUSINESS_ASSET_AMOUNT, this.businessAssetAmount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.BUSINESS_ASSET_AMOUNT);
            actualChanges.put(ClientApiConstants.BUSINESS_ASSET_AMOUNT, newValue);
            this.businessAssetAmount = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.AMOUNT_AT_CASH, this.amountAtCash)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.AMOUNT_AT_CASH);
            actualChanges.put(ClientApiConstants.AMOUNT_AT_CASH, newValue);
            this.amountAtCash = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.AMOUNT_AT_SAVING, this.amountAtSaving)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.AMOUNT_AT_SAVING);
            actualChanges.put(ClientApiConstants.AMOUNT_AT_SAVING, newValue);
            this.amountAtSaving = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.AMOUNT_AT_INVENTORY, this.amountAtInventory)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.AMOUNT_AT_INVENTORY);
            actualChanges.put(ClientApiConstants.AMOUNT_AT_INVENTORY, newValue);
            this.amountAtInventory = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.FIXED_ASSET_COST, this.fixedAssetCost)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.FIXED_ASSET_COST);
            actualChanges.put(ClientApiConstants.FIXED_ASSET_COST, newValue);
            this.fixedAssetCost = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.TOTAL_IN_TAX, this.totalInTax)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_IN_TAX);
            actualChanges.put(ClientApiConstants.TOTAL_IN_TAX, newValue);
            this.totalInTax = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.TOTAL_IN_TRANSPORT, this.totalInTransport)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_IN_TRANSPORT);
            actualChanges.put(ClientApiConstants.TOTAL_IN_TRANSPORT, newValue);
            this.totalInTransport = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.TOTAL_IN_RENT, this.totalInRent)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_IN_RENT);
            actualChanges.put(ClientApiConstants.TOTAL_IN_RENT, newValue);
            this.totalInRent = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.TOTAL_IN_COMMUNICATION, this.totalInCommunication)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_IN_COMMUNICATION);
            actualChanges.put(ClientApiConstants.TOTAL_IN_COMMUNICATION, newValue);
            this.totalInCommunication = newValue;
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.OTHER_EXPENSE, this.otherExpense)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.OTHER_EXPENSE);
            actualChanges.put(ClientApiConstants.OTHER_EXPENSE, newValue);
            this.otherExpense = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.OTHER_EXPENSE_AMOUNT, this.otherExpenseAmount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.OTHER_EXPENSE_AMOUNT);
            actualChanges.put(ClientApiConstants.OTHER_EXPENSE_AMOUNT, newValue);
            this.otherExpenseAmount = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.TOTAL_UTILITY, this.totalUtility)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_UTILITY);
            actualChanges.put(ClientApiConstants.TOTAL_UTILITY, newValue);
            this.totalUtility = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.TOTAL_WORKER_SALARY, this.totalWorkerSalary)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_WORKER_SALARY);
            actualChanges.put(ClientApiConstants.TOTAL_WORKER_SALARY, newValue);
            this.totalWorkerSalary = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.TOTAL_WAGE, this.totalWage)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_WAGE);
            actualChanges.put(ClientApiConstants.TOTAL_WAGE, newValue);
            this.totalWage = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.SOCIETY, this.society)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.SOCIETY);
            actualChanges.put(ClientApiConstants.SOCIETY, newValue);
            this.society = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientApiConstants.TOTAL_PURCHASE_LAST_MONTH, this.totalPurchaseLastMonth)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientApiConstants.TOTAL_PURCHASE_LAST_MONTH);
            actualChanges.put(ClientApiConstants.TOTAL_PURCHASE_LAST_MONTH, newValue);
            this.totalPurchaseLastMonth = newValue;
        }

        return actualChanges;
    }
}
