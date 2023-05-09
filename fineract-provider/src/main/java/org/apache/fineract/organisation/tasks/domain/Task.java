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
package org.apache.fineract.organisation.tasks.domain;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;

@Entity
@Table(name = "m_tasks")
public class Task extends AbstractPersistableCustom {

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "description", length = 300)
    private String description;

    @Column(name = "status")
    private Integer status;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "assigned_to", nullable = true)
    private Long assignedTo;

    @Column(name = "due_date", nullable = true)
    private LocalDate dueDate;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "updated_date", nullable = true)
    private LocalDate updatedDate;

    public static Task fromJson(final JsonCommand command) {

        final String title = command.stringValueOfParameterNamed("title");

        final String description = command.stringValueOfParameterNamed("description");

        final Integer status = command.integerValueOfParameterNamed("status");

        final Long userId = command.longValueOfParameterNamed("userId");

        final Long assignedTo = command.longValueOfParameterNamed("assignedTo");

        LocalDate dueDate = null;
        if (command.hasParameter("dueDate")) {
            dueDate = command.localDateValueOfParameterNamed("dueDate");
        }

        LocalDate createdDate = null;
        if (command.hasParameter("createdDate")) {
            createdDate = command.localDateValueOfParameterNamed("createdDate");
        } else
            createdDate = DateUtils.getLocalDateOfTenant();

        LocalDate updatedDate = null;
        if (command.hasParameter("updatedDate")) {
            updatedDate = command.localDateValueOfParameterNamed("updatedDate");
        }

        return new Task(title, description, status, userId, assignedTo, createdDate, updatedDate, dueDate);
    }

    protected Task() {
        //
    }

    private Task(final String title, final String description, final Integer status, final Long userId, final Long assignedTo,
            final LocalDate createdDate, final LocalDate updatedDate, final LocalDate dueDate) {
        this.title = title;
        this.description = StringUtils.defaultIfEmpty(description, null);
        this.status = status;
        this.userId = userId;
        this.assignedTo = assignedTo;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.dueDate = dueDate;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String assignedToParamName = "assignedTo";
        if (command.isChangeInLongParameterNamed(assignedToParamName, assignedTo)) {
            final Long newValue = command.longValueOfParameterNamed(assignedToParamName);
            actualChanges.put(assignedToParamName, newValue);
            this.assignedTo = newValue;
        }

        boolean descriptionChanged = false;
        final String descriptionParamName = "firstname";
        if (command.isChangeInStringParameterNamed(descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
            actualChanges.put(descriptionParamName, newValue);
            this.description = newValue;
            descriptionChanged = true;
        }

        final String dueDateParamName = "dueDate";
        if (command.isChangeInDateParameterNamed(dueDateParamName, this.dueDate)) {
            final LocalDate newValue = command.dateValueOfParameterNamed(dueDateParamName);
            actualChanges.put(dueDateParamName, newValue);
            this.dueDate = newValue;
        }

        return actualChanges;
    }

    public void setUserId(Long id) {
        this.userId = id;
    }

    public void setStatus(TaskStatusTypes pending) {
        this.status = pending.getValue();
    }
}
