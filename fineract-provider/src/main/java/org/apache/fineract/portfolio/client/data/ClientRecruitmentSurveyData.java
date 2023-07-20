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
import java.time.LocalDate;
import java.util.Collection;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;

public final class ClientRecruitmentSurveyData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long clientId;

    private String surveyName;
    private String surveyLocation;

    private CodeValueData country;
    private CodeValueData cohort;

    private CodeValueData program;

    // template holder
    private Collection<CodeValueData> countryOptions;
    private Collection<CodeValueData> cohortOptions;
    private Collection<CodeValueData> programOptions;

    private LocalDate startDate;
    private LocalDate endDate;

    public ClientRecruitmentSurveyData(Long id, Long clientId, String surveyName, String surveyLocation, CodeValueData country,
            CodeValueData cohort, CodeValueData program, Collection<CodeValueData> countryOptions, Collection<CodeValueData> cohortOptions,
            Collection<CodeValueData> programOptions, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.clientId = clientId;
        this.surveyName = surveyName;
        this.surveyLocation = surveyLocation;
        this.country = country;
        this.cohort = cohort;
        this.program = program;
        this.countryOptions = countryOptions;
        this.cohortOptions = cohortOptions;
        this.programOptions = programOptions;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static ClientRecruitmentSurveyData template(final Collection<CodeValueData> countryOptions,
            final Collection<CodeValueData> cohortOptions, final Collection<CodeValueData> programOptions) {
        Long id = null;
        Long clientId = null;
        String surveyName = null;
        String surveyLocation = null;
        CodeValueData country = null;
        CodeValueData cohort = null;
        CodeValueData program = null;
        LocalDate startDate = null;
        LocalDate endDate = null;

        return new ClientRecruitmentSurveyData(id, clientId, surveyName, surveyLocation, country, cohort, program, countryOptions,
                cohortOptions, programOptions, startDate, endDate);
    }

    public static ClientRecruitmentSurveyData instance(final Long id, final Long clientId, final String surveyName,
            final String surveyLocation, final CodeValueData country, final CodeValueData cohort, final CodeValueData program,
            final LocalDate startDate, final LocalDate endDate) {
        return new ClientRecruitmentSurveyData(id, clientId, surveyName, surveyLocation, country, cohort, program, null, null, null,
                startDate, endDate);
    }

    public static ClientRecruitmentSurveyData templateWithData(ClientRecruitmentSurveyData data, ClientRecruitmentSurveyData templateData) {
        return new ClientRecruitmentSurveyData(data.id, data.clientId, data.surveyName, data.surveyLocation, data.country, data.cohort,
                data.program, templateData.countryOptions, templateData.cohortOptions, templateData.programOptions, data.startDate,
                data.endDate);
    }

}
