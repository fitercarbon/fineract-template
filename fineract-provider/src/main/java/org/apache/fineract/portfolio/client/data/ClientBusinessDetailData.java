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
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.client.domain.ClientBusinessDetail;

public class ClientBusinessDetailData implements Serializable {

    private Long id;
    private Long clientId;
    private Collection<CodeValueData> businessType;
    private CodeValueData businessTypeId;
    private LocalDate businessCreationDate;
    private BigDecimal startingCapital;
    private Collection<CodeValueData> sourceOfCapital;
    private CodeValueData sourceOfCapitalId;
    private Long totalEmployee;
    private BigDecimal businessRevenue;
    private BigDecimal averageMonthlyRevenue;
    private Collection<EnumOptionData> bestMonth;
    private EnumOptionData bestMonthId;
    private String reasonForBestMonth;
    private List<EnumOptionData> worstMonth;
    private EnumOptionData worstMonthId;
    private String reasonForWorstMonth;
    private Long numberOfPurchase;
    private String purchaseFrequency;
    private BigDecimal totalPurchaseLastMonth;
    private EnumOptionData lastPurchase;
    private BigDecimal lastPurchaseAmount;
    private BigDecimal businessAsset;
    private BigDecimal amountAtCash;
    private BigDecimal amountAtSaving;
    private BigDecimal amountAtInventory;
    private BigDecimal fixedAssetCost;
    private BigDecimal totalInTax;
    private BigDecimal totalInTransport;
    private BigDecimal totalInRent;
    private BigDecimal totalInCommunication;
    private String otherExpense;
    private BigDecimal otherExpenseAmount;
    private BigDecimal totalUtility;
    private BigDecimal totalWorkerSalary;
    private BigDecimal totalWage;
    private String externalId;
    private BigDecimal society;
    private ClientData clientAccount;
    private Boolean isClientBusinessDetailEnabled;

    public ClientBusinessDetailData() {}

    public static ClientBusinessDetailData template(final Collection<CodeValueData> businessType,
            final Collection<CodeValueData> sourceOfCapital, List<EnumOptionData> bestMonth, List<EnumOptionData> worstMonth,
            ClientData clientAccount) {
        return new ClientBusinessDetailData(businessType, sourceOfCapital, bestMonth, worstMonth, clientAccount);
    }

    public ClientBusinessDetailData(Collection<CodeValueData> businessType, Collection<CodeValueData> sourceOfCapital,
            List<EnumOptionData> bestMonth, List<EnumOptionData> worstMonth, ClientData clientAccount) {
        this.businessType = businessType;
        this.sourceOfCapital = sourceOfCapital;
        this.bestMonth = bestMonth;
        this.worstMonth = worstMonth;
        this.clientAccount = clientAccount;
    }

    public static ClientBusinessDetailData previewClientBusinessDetail(ClientBusinessDetail clientBusinessDetail,
            CodeValueData businessType, CodeValueData sourceOfCapital) {
        return new ClientBusinessDetailData(clientBusinessDetail.getId(), clientBusinessDetail.getClient().getId(), businessType,
                clientBusinessDetail.getBusinessCreationDate(), clientBusinessDetail.getStartingCapital(), sourceOfCapital,
                clientBusinessDetail.getTotalEmployee(), clientBusinessDetail.getExternalId());
    }

    public ClientBusinessDetailData(Long id, Long clientId, CodeValueData businessTypeId, LocalDate businessCreationDate,
            BigDecimal startingCapital, CodeValueData sourceOfCapitalId, Long totalEmployee, String externalId) {
        this.id = id;
        this.clientId = clientId;
        this.businessTypeId = businessTypeId;
        this.businessCreationDate = businessCreationDate;
        this.startingCapital = startingCapital;
        this.sourceOfCapitalId = sourceOfCapitalId;
        this.totalEmployee = totalEmployee;
        this.externalId = externalId;
    }

    public static ClientBusinessDetailData instance(Long id, Long clientId, CodeValueData businessTypeId, LocalDate businessCreationDate,
            BigDecimal startingCapital, CodeValueData sourceOfCapitalId, Long totalEmployee, BigDecimal businessRevenue,
            BigDecimal averageMonthlyRevenue, EnumOptionData bestMonth, String reasonForBestMonth, EnumOptionData worstMonth,
            String reasonForWorstMonth, Long numberOfPurchase, String purchaseFrequency, BigDecimal totalPurchaseLastMonth,
            EnumOptionData lastPurchase, BigDecimal lastPurchaseAmount, BigDecimal businessAsset, BigDecimal amountAtCash,
            BigDecimal amountAtSaving, BigDecimal amountAtInventory, BigDecimal fixedAssetCost, BigDecimal totalInTax,
            BigDecimal totalInTransport, BigDecimal totalInRent, BigDecimal totalInCommunication, String otherExpense,
            BigDecimal otherExpenseAmount, BigDecimal totalUtility, BigDecimal totalWorkerSalary, BigDecimal totalWage, String externalId,
            BigDecimal society) {

        return new ClientBusinessDetailData(id, clientId, businessTypeId, businessCreationDate, startingCapital, sourceOfCapitalId,
                totalEmployee, businessRevenue, averageMonthlyRevenue, bestMonth, reasonForBestMonth, worstMonth, reasonForWorstMonth,
                numberOfPurchase, purchaseFrequency, totalPurchaseLastMonth, lastPurchase, lastPurchaseAmount, businessAsset, amountAtCash,
                amountAtSaving, amountAtInventory, fixedAssetCost, totalInTax, totalInTransport, totalInRent, totalInCommunication,
                otherExpense, otherExpenseAmount, totalUtility, totalWorkerSalary, totalWage, externalId, society);
    }

    public ClientBusinessDetailData(Long id, Long clientId, CodeValueData businessTypeId, LocalDate businessCreationDate,
            BigDecimal startingCapital, CodeValueData sourceOfCapitalId, Long totalEmployee, BigDecimal businessRevenue,
            BigDecimal averageMonthlyRevenue, EnumOptionData bestMonth, String reasonForBestMonth, EnumOptionData worstMonth,
            String reasonForWorstMonth, Long numberOfPurchase, String purchaseFrequency, BigDecimal totalPurchaseLastMonth,
            EnumOptionData lastPurchase, BigDecimal lastPurchaseAmount, BigDecimal businessAsset, BigDecimal amountAtCash,
            BigDecimal amountAtSaving, BigDecimal amountAtInventory, BigDecimal fixedAssetCost, BigDecimal totalInTax,
            BigDecimal totalInTransport, BigDecimal totalInRent, BigDecimal totalInCommunication, String otherExpense,
            BigDecimal otherExpenseAmount, BigDecimal totalUtility, BigDecimal totalWorkerSalary, BigDecimal totalWage, String externalId,
            BigDecimal society) {
        this.id = id;
        this.clientId = clientId;
        this.businessTypeId = businessTypeId;
        this.businessCreationDate = businessCreationDate;
        this.startingCapital = startingCapital;
        this.sourceOfCapitalId = sourceOfCapitalId;
        this.totalEmployee = totalEmployee;
        this.businessRevenue = businessRevenue;
        this.averageMonthlyRevenue = averageMonthlyRevenue;
        this.bestMonthId = bestMonth;
        this.reasonForBestMonth = reasonForBestMonth;
        this.worstMonthId = worstMonth;
        this.reasonForWorstMonth = reasonForWorstMonth;
        this.numberOfPurchase = numberOfPurchase;
        this.purchaseFrequency = purchaseFrequency;
        this.totalPurchaseLastMonth = totalPurchaseLastMonth;
        this.lastPurchase = lastPurchase;
        this.lastPurchaseAmount = lastPurchaseAmount;
        this.businessAsset = businessAsset;
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

    public void setClientBusinessDetailEnabled(Boolean clientBusinessDetailEnabled) {
        isClientBusinessDetailEnabled = clientBusinessDetailEnabled;
    }
}
