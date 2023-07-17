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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientHouseholdExpensesDataValidator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientHouseholdExpenses;
import org.apache.fineract.portfolio.client.domain.ClientHouseholdExpensesRepository;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.OtherClientHouseholdExpenses;
import org.apache.fineract.portfolio.client.domain.OtherClientHouseholdExpensesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClientHouseholdExpensesWritePlatformServiceImpl implements ClientHouseholdExpensesWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHouseholdExpensesWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;

    private final ClientHouseholdExpensesRepository clientHouseholdExpensesRepository;

    private final ClientHouseholdExpensesDataValidator clientHouseholdExpensesDataValidator;

    private final ClientRepositoryWrapper clientRepository;

    private final CodeValueRepositoryWrapper codeValueRepository;

    private final OtherClientHouseholdExpensesRepository otherClientHouseholdExpensesRepository;

    @Override
    public CommandProcessingResult addClientHouseholdExpenses(long clientId, JsonCommand command) {
        this.clientHouseholdExpensesDataValidator.validateAdd(command.json());

        try {
            final Client client = clientRepository.getActiveClientInUserScope(clientId);
            var existingClientHouseholdExpenses = clientHouseholdExpensesRepository.findByClient(client);
            if (existingClientHouseholdExpenses.isPresent()) {
                throw new PlatformDataIntegrityException("the.client.has.existing.household.expenses",
                        "The client has already an existing household expenses ", client.getId());
            }
            var clientHouseholdExpenses = ClientHouseholdExpenses.fromJson(client, command);
            clientHouseholdExpensesRepository.saveAndFlush(clientHouseholdExpenses);

            if (command.arrayOfParameterNamed(ClientApiConstants.otherExpensesListParamName) != null) {
                Set<OtherClientHouseholdExpenses> otherExpenses = getOtherExpensesList(command, clientHouseholdExpenses);
                clientHouseholdExpenses.updateOtherExpenses(otherExpenses);
            }
            return new CommandProcessingResultBuilder() //
                    .withEntityId(clientHouseholdExpenses.getId()) //
                    .withClientId(client.getId()) //
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            throw new PlatformDataIntegrityException(dve.getMessage(), dve.getLocalizedMessage());
        }

    }

    @Override
    public CommandProcessingResult updateClientHouseholdExpenses(Long householdExpenseId, JsonCommand command) {
        this.clientHouseholdExpensesDataValidator.validateUpdate(command.json());
        try {
            var clientHouseholdExpenses = clientHouseholdExpensesRepository.findById(householdExpenseId)
                    .orElseThrow(() -> new EntityNotFoundException());
            var changes = clientHouseholdExpenses.update(command);

            if (command.arrayOfParameterNamed(ClientApiConstants.otherExpensesListParamName) != null) {
                Set<OtherClientHouseholdExpenses> otherExpenses = getOtherExpensesList(command, clientHouseholdExpenses);
                clientHouseholdExpenses.updateOtherExpenses(otherExpenses);
            }
            clientHouseholdExpensesRepository.saveAndFlush(clientHouseholdExpenses);
            return new CommandProcessingResultBuilder() //
                    .withEntityId(clientHouseholdExpenses.getId()) //
                    .with(changes) //
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            throw new PlatformDataIntegrityException(dve.getMessage(), dve.getLocalizedMessage());
        }
    }

    private Set<OtherClientHouseholdExpenses> getOtherExpensesList(JsonCommand command, ClientHouseholdExpenses clientHouseholdExpenses) {
        Set<OtherClientHouseholdExpenses> otherExpenses = new HashSet<>();
        final JsonArray otherExpensesList = command.arrayOfParameterNamed(ClientApiConstants.otherExpensesListParamName);
        otherExpensesList.forEach(otherExpensesElement -> {
            JsonObject otherExpensesObject = otherExpensesElement.getAsJsonObject();
            final Long otherExpensesId = otherExpensesObject.get(ClientApiConstants.otherExpensesIdParamName).getAsLong();
            CodeValue otherExpense = getOtherExpensesId(otherExpensesId);
            final BigDecimal otherExpensesAmount = otherExpensesObject.get(ClientApiConstants.otherExpensesAmountParamName)
                    .getAsBigDecimal();
            otherExpenses.add(new OtherClientHouseholdExpenses(clientHouseholdExpenses, otherExpense, otherExpensesAmount));
        });

        return otherExpenses;
    }

    @Override
    public CommandProcessingResult deleteClientHouseholdExpenses(Long householdExpenseId, JsonCommand command) {
        try {
            var clientHouseholdExpenses = clientHouseholdExpensesRepository.findById(householdExpenseId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No client expenses found by the given Id"));
            clientHouseholdExpenses.updateOtherExpenses(new HashSet<>());
            clientHouseholdExpensesRepository.delete(clientHouseholdExpenses);
            return new CommandProcessingResultBuilder() //
                    .withEntityId(clientHouseholdExpenses.getId()) //
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            throw new PlatformDataIntegrityException(dve.getMessage(), dve.getLocalizedMessage());
        }
    }

    @Nullable
    private CodeValue getOtherExpensesId(Long id) {
        CodeValue otherExpenses = null;
        if (id != null) {
            otherExpenses = this.codeValueRepository
                    .findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.OTHER_HOUSEHOLD_EXPENSES, id);
        }
        return otherExpenses;
    }

}
