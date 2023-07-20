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

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;

@Entity
@Table(name = "m_client_recruitment_survey")
public class ClientRecruitmentSurvey extends AbstractPersistableCustom {

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "country_cv_id", nullable = false)
    private CodeValue country;

    @ManyToOne
    @JoinColumn(name = "cohort_cv_id", nullable = false)
    private CodeValue cohort;

    @ManyToOne
    @JoinColumn(name = "program_cv_id", nullable = false)
    private CodeValue program;

    @Column(name = "survey_name", nullable = false)
    private String surveyName;

    @Column(name = "survey_location", nullable = false)
    private String surveyLocation;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public ClientRecruitmentSurvey() {}

    public ClientRecruitmentSurvey(Client client, CodeValue country, CodeValue cohort, CodeValue program, String surveyName,
            String surveyLocation, LocalDate startDate, LocalDate endDate) {
        this.client = client;
        this.country = country;
        this.cohort = cohort;
        this.program = program;
        this.surveyName = surveyName;
        this.surveyLocation = surveyLocation;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static ClientRecruitmentSurvey createNew(JsonCommand command, Client client, final CodeValue country, final CodeValue cohort,
            final CodeValue program) {

        final String surveyName = command.stringValueOfParameterNamed(ClientApiConstants.surveyNameParamName);
        final String surveyLocation = command.stringValueOfParameterNamed(ClientApiConstants.surveyLocationParamName);
        final LocalDate startDate = command.localDateValueOfParameterNamed(ClientApiConstants.startDateParamName);
        final LocalDate endDate = command.localDateValueOfParameterNamed(ClientApiConstants.endDateParamName);
        return new ClientRecruitmentSurvey(client, country, cohort, program, surveyName, surveyLocation, startDate, endDate);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);

        if (command.isChangeInStringParameterNamed(ClientApiConstants.surveyNameParamName, this.surveyName)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.surveyNameParamName);
            actualChanges.put(ClientApiConstants.surveyNameParamName, newValue);
            this.surveyName = newValue;
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.surveyLocationParamName, this.surveyLocation)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.surveyLocationParamName);
            actualChanges.put(ClientApiConstants.surveyLocationParamName, newValue);
            this.surveyLocation = newValue;
        }

        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.startDateParamName, this.startDate)) {
            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.startDateParamName);
            actualChanges.put(ClientApiConstants.startDateParamName, newValue);
            this.startDate = newValue;
        }

        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.endDateParamName, this.endDate)) {
            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.endDateParamName);
            actualChanges.put(ClientApiConstants.endDateParamName, newValue);
            this.endDate = newValue;
        }
        if (command.isChangeInLongParameterNamed(ClientApiConstants.countryIdParamName, countryId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.countryIdParamName);
            actualChanges.put(ClientApiConstants.countryIdParamName, newValue);
        }
        if (command.isChangeInLongParameterNamed(ClientApiConstants.cohortIdParamName, cohortId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.cohortIdParamName);
            actualChanges.put(ClientApiConstants.cohortIdParamName, newValue);
        }
        if (command.isChangeInLongParameterNamed(ClientApiConstants.programIdParamName, programId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.programIdParamName);
            actualChanges.put(ClientApiConstants.programIdParamName, newValue);
        }

        return actualChanges;
    }

    public Client getClient() {
        return client;
    }

    private Long cohortId() {
        Long cohort = null;
        if (this.cohort != null) {
            cohort = this.cohort.getId();
        }
        return cohort;
    }

    private Long programId() {
        Long program = null;
        if (this.program != null) {
            program = this.program.getId();
        }
        return program;
    }

    private Long countryId() {
        Long country = null;
        if (this.country != null) {
            country = this.country.getId();
        }
        return country;
    }

    public void setCountry(CodeValue country) {
        this.country = country;
    }

    public void setCohort(CodeValue cohort) {
        this.cohort = cohort;
    }

    public void setProgram(CodeValue program) {
        this.program = program;
    }

}
