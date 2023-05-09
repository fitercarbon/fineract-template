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
package org.apache.fineract.organisation.tasks.service;

import java.util.Map;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.tasks.domain.Task;
import org.apache.fineract.organisation.tasks.domain.TaskRepository;
import org.apache.fineract.organisation.tasks.domain.TaskStatusTypes;
import org.apache.fineract.organisation.tasks.exception.TaskNotFoundException;
import org.apache.fineract.organisation.tasks.serialization.TaskCommandFromApiJsonDeserializer;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskWritePlatformServiceJpaRepositoryImpl implements TaskWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskWritePlatformServiceJpaRepositoryImpl.class);

    private final TaskCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final TaskRepository taskRepository;
    private final PlatformSecurityContext context;

    @Autowired
    public TaskWritePlatformServiceJpaRepositoryImpl(final TaskCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final TaskRepository taskRepository, final PlatformSecurityContext context) {
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.taskRepository = taskRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult createTask(final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForCreate(command.json());

            final Long officeId = command.longValueOfParameterNamed("officeId");

            final Task task = Task.fromJson(command);

            final AppUser currentUser = this.context.authenticatedUser();

            task.setUserId(currentUser.getId());
            task.setStatus(TaskStatusTypes.PENDING);

            this.taskRepository.saveAndFlush(task);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(task.getId()).withOfficeId(officeId) //
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleStaffDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleStaffDataIntegrityIssues(command, throwable, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateTask(final Long taskId, final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForUpdate(command.json(), taskId);

            final Task taskForUpdate = this.taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
            final Map<String, Object> changesOnly = taskForUpdate.update(command);

            if (!changesOnly.isEmpty()) {
                this.taskRepository.saveAndFlush(taskForUpdate);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(taskId).with(changesOnly).build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleStaffDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleStaffDataIntegrityIssues(command, throwable, dve);
            return CommandProcessingResult.empty();
        }
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue is.
     */
    private void handleStaffDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        if (realCause.getMessage().contains("title")) {
            final String title = command.stringValueOfParameterNamed("title");
            throw new PlatformDataIntegrityException("error.msg.task.duplicate.title",
                    "A task with the given title '" + title + "' already exists", "title", title);
        }

        LOG.error("Error occured.", dve);
        throw new PlatformDataIntegrityException("error.msg.task.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}
