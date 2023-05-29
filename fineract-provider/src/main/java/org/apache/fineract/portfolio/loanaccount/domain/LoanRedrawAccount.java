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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "m_loan_redraw_account")
@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class LoanRedrawAccount extends AbstractPersistableCustom {

    public LoanRedrawAccount() {

    }

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @Column(name = "withdrawn_on_date")
    private LocalDateTime withdrawnOnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "withdrawn_by")
    private AppUser withdrawnBy;

    @Column(name = "createdby_id", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "lastmodifiedby_id")
    private Long lastModifiedBy;

    @Column(name = "lastmodified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "redraw_balance", scale = 6, precision = 19, nullable = false)
    private BigDecimal redrawBalance;

    public void withdraw(final BigDecimal transactionAmount, final AppUser withdrawnBy, final LocalDateTime withdrawnOnDate) {
        this.setRedrawBalance(this.redrawBalance.subtract(transactionAmount));
        this.setWithdrawnBy(withdrawnBy);
        this.setWithdrawnOnDate(withdrawnOnDate);
    }
}
