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

import java.util.Map;
import javax.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientRecruitmentSurveyDataValidator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRecruitmentSurvey;
import org.apache.fineract.portfolio.client.domain.ClientRecruitmentSurveyRepository;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ClientRecruitmentSurveyAlreadyHadDoneException;
import org.apache.fineract.portfolio.client.exception.ClientRecruitmentSurveyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientRecruitmentSurveyWritePlatformServiceImpl implements ClientRecruitmentSurveyWritePlatformService {

    private final PlatformSecurityContext context;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final ClientRecruitmentSurveyRepository clientRecruitmentSurveyRepository;
    private final ClientRecruitmentSurveyDataValidator fromApiJsonDeserializer;

    private final ConfigurationReadPlatformService configurationReadPlatformService;
    private static final Logger LOG = LoggerFactory.getLogger(ClientRecruitmentSurveyWritePlatformServiceImpl.class);

    @Override
    public CommandProcessingResult create(final Long clientId, final JsonCommand command) {

        final ClientRecruitmentSurvey recruitmentSurvey = this.clientRecruitmentSurveyRepository.getByClientId(clientId);
        if (recruitmentSurvey != null) {
            throw new ClientRecruitmentSurveyAlreadyHadDoneException(clientId);
        }

        final GlobalConfigurationPropertyData recruitmentSurveyConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Enable-client-recruitment-survey");
        final Boolean isRecruitmentSurveyConfigInfoEnable = recruitmentSurveyConfig.isEnabled();

        if (!isRecruitmentSurveyConfigInfoEnable) {
            throw new GeneralPlatformDomainRuleException("error.msg.enable.client.recruitment.survey",
                    "Enable Client Recruitment Survey for proceeding operation");
        }
        fromApiJsonDeserializer.validateForCreate(clientId, command.json());
        final Client client = clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);

        CodeValue country = null;
        final Long countryId = command.longValueOfParameterNamed(ClientApiConstants.countryIdParamName);
        if (countryId != null) {
            country = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.COUNTRY, countryId);
        }

        CodeValue cohort = null;
        final Long cohortId = command.longValueOfParameterNamed(ClientApiConstants.cohortIdParamName);
        if (cohortId != null) {
            cohort = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.COHORT, cohortId);
        }

        CodeValue program = null;
        final Long programId = command.longValueOfParameterNamed(ClientApiConstants.programIdParamName);
        if (programId != null) {
            program = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.PROGRAM, programId);
        }

        CodeValue surveyLocation = null;
        final Long surveyLocationId = command.longValueOfParameterNamed(ClientApiConstants.surveyLocationIdParamName);
        if (surveyLocationId != null) {
            surveyLocation = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.SURVEY_LOCATION,
                    surveyLocationId);
        }

        ClientRecruitmentSurvey survey = ClientRecruitmentSurvey.createNew(command, client, country, cohort, program, surveyLocation);

        ClientRecruitmentSurvey newRecruitmentSurvey = clientRecruitmentSurveyRepository.saveAndFlush(survey);

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withClientId(clientId)
                .withEntityId(newRecruitmentSurvey.getId()).build();
    }

    @Override
    public CommandProcessingResult update(Long surveyId, JsonCommand command) {

        try {
            final GlobalConfigurationPropertyData recruitmentSurveyConfig = this.configurationReadPlatformService
                    .retrieveGlobalConfiguration("Enable-client-recruitment-survey");
            final Boolean isRecruitmentSurveyConfigInfoEnable = recruitmentSurveyConfig.isEnabled();

            if (!isRecruitmentSurveyConfigInfoEnable) {
                throw new GeneralPlatformDomainRuleException("error.msg.enable.client.recruitment.survey",
                        "Enable Client Recruitment Survey for proceeding operation");
            }

            final ClientRecruitmentSurvey survey = this.clientRecruitmentSurveyRepository.findById(surveyId)
                    .orElseThrow(() -> new ClientRecruitmentSurveyNotFoundException(surveyId));
            this.fromApiJsonDeserializer.validateForUpdate(command.json());
            final Map<String, Object> changes = survey.update(command);

            if (changes.containsKey(ClientApiConstants.countryIdParamName)) {
                final Long countryId = command.longValueOfParameterNamed(ClientApiConstants.countryIdParamName);
                CodeValue countryCodeValue = null;
                if (countryId != null) {

                    countryCodeValue = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.COUNTRY,
                            countryId);
                }
                survey.setCountry(countryCodeValue);
            }
            if (changes.containsKey(ClientApiConstants.cohortIdParamName)) {
                final Long cohortId = command.longValueOfParameterNamed(ClientApiConstants.cohortIdParamName);
                CodeValue cohortCodeValue = null;
                if (cohortId != null) {

                    cohortCodeValue = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.COHORT,
                            cohortId);
                }
                survey.setCohort(cohortCodeValue);
            }

            if (changes.containsKey(ClientApiConstants.programIdParamName)) {
                final Long programId = command.longValueOfParameterNamed(ClientApiConstants.programIdParamName);
                CodeValue programCodeValue = null;
                if (programId != null) {

                    programCodeValue = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.PROGRAM,
                            programId);
                }
                survey.setProgram(programCodeValue);
            }

            if (changes.containsKey(ClientApiConstants.surveyLocationIdParamName)) {
                final Long surveyLocationId = command.longValueOfParameterNamed(ClientApiConstants.surveyLocationIdParamName);
                CodeValue surveyLocationCodeValue = null;
                if (surveyLocationId != null) {

                    surveyLocationCodeValue = this.codeValueRepository
                            .findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.SURVEY_LOCATION, surveyLocationId);
                }
                survey.setSurveyLocation(surveyLocationCodeValue);
            }

            if (!changes.isEmpty()) {
                this.clientRecruitmentSurveyRepository.saveAndFlush(survey);
                LOG.info("Update successfully");
            }
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withClientId(survey.getClient().getId()) //
                    .withEntityId(survey.getId()) //
                    .with(changes) //
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            return CommandProcessingResult.empty();
        }

    }
}
