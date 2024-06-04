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
package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SavingsAccountTransactionRepository
        extends JpaRepository<SavingsAccountTransaction, Long>, JpaSpecificationExecutor<SavingsAccountTransaction> {

    @Query("select sat from SavingsAccountTransaction sat where sat.id = :transactionId and sat.savingsAccount.id = :savingsId")
    SavingsAccountTransaction findOneByIdAndSavingsAccountId(@Param("transactionId") Long transactionId,
            @Param("savingsId") Long savingsId);

    // @Query("SELECT sat FROM SavingsAccountTransaction sat WHERE sat.id IN (:ids)")
    // List<SavingsAccountTransaction> getTransactionsByIds(@Param("ids") List<Long> savingsId);

    List<SavingsAccountTransaction> findByIdIn(List<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select st from SavingsAccountTransaction st where st.savingsAccount = :savingsAccount and st.dateOf >= :transactionDate order by st.dateOf,st.createdDate,st.id")
    List<SavingsAccountTransaction> findTransactionsAfterPivotDate(@Param("savingsAccount") SavingsAccount savingsAccount,
            @Param("transactionDate") LocalDate transactionDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select st from SavingsAccountTransaction st where st.savingsAccount = :savingsAccount and st.dateOf = :date and st.reversalTransaction <> 1 and st.reversed <> 1 order by st.id")
    List<SavingsAccountTransaction> findTransactionRunningBalanceBeforePivotDate(@Param("savingsAccount") SavingsAccount savingsAccount,
            @Param("date") LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<SavingsAccountTransaction> findBySavingsAccount(@Param("savingsAccount") SavingsAccount savingsAccount);

    @Query("select sat from SavingsAccountTransaction sat where sat.refNo = :refNo")
    List<SavingsAccountTransaction> findAllTransactionByRefNo(@Param("refNo") String refNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<SavingsAccountTransaction> findBySavingsAccountId(@Param("savingsAccountId") Long savingsAccountId);

    @Query("SELECT sat FROM SavingsAccountTransaction sat WHERE sat.savingsAccount.id = :savingsId ORDER BY sat.dateOf, sat.createdDate, sat.id")
    List<SavingsAccountTransaction> getTransactionsByAccountId(@Param("savingsId") Long savingsId);

    @Query("SELECT sat FROM SavingsAccountTransaction sat WHERE sat.savingsAccount.id = :savingsId and sat.typeOf = :type ORDER BY sat.dateOf, sat.createdDate, sat.id")
    List<SavingsAccountTransaction> getTransactionsByAccountIdAndType(@Param("savingsId") Long savingsId, @Param("type") Integer type);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select st from SavingsAccountTransaction st where st.savingsAccount = :savingsAccount and st.dateOf > :transactionDate order by st.dateOf DESC")
    List<SavingsAccountTransaction> findTransactionsAfterTransactionCurrentDate(@Param("savingsAccount") SavingsAccount savingsAccount,
            @Param("transactionDate") LocalDate transactionDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select st from SavingsAccountTransaction st  INNER JOIN FETCH st.paymentDetail where st.savingsAccount = :savingsAccount and st.dateOf <= :transactionDate and st.typeOf = 3 and st.reversed = false  and st.paymentDetail.parentSavingsAccountTransactionId IS NULL and st.paymentDetail.parentTransactionPaymentDetailsId  IS NULL  order by st.dateOf DESC")
    List<SavingsAccountTransaction> findInterestPostingToBeRevokedOnVaultTribe(@Param("savingsAccount") SavingsAccount savingsAccount,
            @Param("transactionDate") LocalDate transactionDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select st from SavingsAccountTransaction st  INNER JOIN FETCH st.paymentDetail where  st.typeOf = 2 AND st.paymentDetail IS NOT NULL AND st.paymentDetail.parentSavingsAccountTransactionId = :transactionId AND  st.paymentDetail.parentTransactionPaymentDetailsId = :paymentDetailsId ")
    SavingsAccountTransaction findRevokedInterestTransaction(@Param("transactionId") Long transactionId,
            @Param("paymentDetailsId") Long paymentDetailsId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select st from SavingsAccountTransaction st INNER JOIN FETCH st.savingsAccount  where  st.id = :transactionId AND st.savingsAccount.id = :savingsAccountId ")
    SavingsAccountTransaction findSavingsAccountTransaction(@Param("transactionId") Long transactionId,
            @Param("savingsAccountId") Long savingsAccountId);

    // fetch transactions by date , amount and savings account id and type
    @Query("select st from SavingsAccountTransaction st where st.savingsAccount = :savingsAccount and st.dateOf = :transactionDate and st.amount = :amount and st.typeOf = :type")
    List<SavingsAccountTransaction> findTransactionByDateAmountAndType(@Param("savingsAccount") SavingsAccount savingsAccount,
            @Param("transactionDate") LocalDate transactionDate, @Param("amount") BigDecimal amount, @Param("type") Integer type);

}
