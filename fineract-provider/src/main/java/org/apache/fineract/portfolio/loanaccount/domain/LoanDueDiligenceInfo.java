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

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;

@Data
@Entity
@Table(name = "m_loan_due_diligence_info")
public class LoanDueDiligenceInfo extends AbstractAuditableCustom {

    @OneToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;
    @OneToOne
    @JoinColumn(name = "loan_decision_id")
    private LoanDecision loanDecision;
    @Column(name = "survey_name")
    private String surveyName;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @ManyToOne
    @JoinColumn(name = "survey_location_cv_id")
    private CodeValue surveyLocation;
    @ManyToOne
    @JoinColumn(name = "cohort_cv_id")
    private CodeValue cohort;
    @ManyToOne
    @JoinColumn(name = "program_cv_id")
    private CodeValue program;
    @ManyToOne
    @JoinColumn(name = "country_cv_id")
    private CodeValue country;

    public LoanDueDiligenceInfo() {}

    public static LoanDueDiligenceInfo createNew(Loan loan, LoanDecision loanDecision, String surveyName, LocalDate startDate,
            LocalDate endDate, CodeValue surveyLocation, CodeValue cohort, CodeValue program, CodeValue country) {
        return new LoanDueDiligenceInfo(loan, loanDecision, surveyName, startDate, endDate, surveyLocation, cohort, program, country);
    }

    public LoanDueDiligenceInfo(Loan loan, LoanDecision loanDecision, String surveyName, LocalDate startDate, LocalDate endDate,
            CodeValue surveyLocation, CodeValue cohort, CodeValue program, CodeValue country) {
        this.loan = loan;
        this.loanDecision = loanDecision;
        this.surveyName = surveyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.surveyLocation = surveyLocation;
        this.cohort = cohort;
        this.program = program;
        this.country = country;
    }
}
