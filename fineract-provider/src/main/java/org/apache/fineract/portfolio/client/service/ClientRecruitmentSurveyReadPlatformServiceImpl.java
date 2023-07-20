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
package org.apache.fineract.portfolio.client.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientRecruitmentSurveyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientRecruitmentSurveyReadPlatformServiceImpl implements ClientRecruitmentSurveyReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    @Autowired
    public ClientRecruitmentSurveyReadPlatformServiceImpl(final PlatformSecurityContext context, final JdbcTemplate jdbcTemplate,
            final CodeValueReadPlatformService codeValueReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
        this.codeValueReadPlatformService = codeValueReadPlatformService;

    }

    private static final class ClientRecruitmentSurveyMapper implements RowMapper<ClientRecruitmentSurveyData> {

        public String schema() {
            return "s.id AS id, s.client_id AS clientId, s.country_cv_id AS countryId, s.cohort_cv_id AS cohortId, cv.code_value as countryName, s.program_cv_id AS programId,"
                    + "cvn.code_value AS cohortName, cy.code_value AS programName, s.survey_name AS surveyName, s.survey_location AS surveyLocation, s.start_date as startDate, s.end_date as endDate"
                    + " FROM m_client_recruitment_survey s" + " left join m_code_value cvn on s.cohort_cv_id=cvn.id"
                    + " left join m_code_value cv on s.country_cv_id=cv.id left join m_code_value cy on s.program_cv_id=cy.id";
        }

        @Override
        public ClientRecruitmentSurveyData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final long id = rs.getLong("id");
            final long clientId = rs.getLong("clientId");

            final Long countryId = rs.getLong("countryId");
            final String countryName = rs.getString("countryName");
            final CodeValueData country = CodeValueData.instance(countryId, countryName);

            final Long cohortId = rs.getLong("cohortId");
            final String cohortName = rs.getString("cohortName");
            final CodeValueData cohort = CodeValueData.instance(cohortId, cohortName);

            final Long programId = rs.getLong("programId");
            final String programName = rs.getString("programName");
            final CodeValueData program = CodeValueData.instance(programId, programName);

            final String surveyName = rs.getString("surveyName");
            final String surveyLocation = rs.getString("surveyLocation");
            final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
            final LocalDate endDate = JdbcSupport.getLocalDate(rs, "endDate");
            return ClientRecruitmentSurveyData.instance(id, clientId, surveyName, surveyLocation, country, cohort, program, startDate,
                    endDate);
        }
    }

    @Override
    public Collection<ClientRecruitmentSurveyData> retrieveAll(long clientId) {

        this.context.authenticatedUser();

        final ClientRecruitmentSurveyMapper rm = new ClientRecruitmentSurveyMapper();
        final String sql = "select " + rm.schema() + " where s.client_id=?";

        return this.jdbcTemplate.query(sql, rm, new Object[] { clientId }); // NOSONAR
    }

    @Override
    public ClientRecruitmentSurveyData retrieveOne(Long id) {

        this.context.authenticatedUser();

        final ClientRecruitmentSurveyMapper rm = new ClientRecruitmentSurveyMapper();
        final String sql = "select " + rm.schema() + " where s.id=? ";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id }); // NOSONAR
    }

    @Override
    public ClientRecruitmentSurveyData retrieveTemplate() {

        final List<CodeValueData> countryOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.COUNTRY));
        final List<CodeValueData> cohortOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.COHORT));
        final List<CodeValueData> programOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.PROGRAM));
        return ClientRecruitmentSurveyData.template(countryOptions, cohortOptions, programOptions);
    }

}
