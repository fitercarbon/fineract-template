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
package org.apache.fineract.organisation.tasks.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;

/**
 * Immutable data object representing staff data.
 */
public final class TaskData implements Serializable {

    private final Long id;
    private final Long userId;
    private final String title;
    private final String description;
    private final String status;
    private final LocalDate dueDate;
    private final LocalDate createdDate;
    private final LocalDate updatedDate;
    private final String username;
    final Collection<CodeValueData> statusOptions;
    // import fields
    private transient Integer rowIndex;
    private String dateFormat;
    private String locale;

    public static TaskData importInstance(Long id, String title, String description, String status, Long userId, LocalDate createdDate,
            LocalDate updatedDate, LocalDate dueDate, String username) {
        return new TaskData(id, title, description, status, userId, createdDate, updatedDate, dueDate, username);

    }

    private TaskData(Long id, String title, String description, String status, Long userId, LocalDate createdDate, LocalDate updatedDate,
            LocalDate dueDate, String username) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.userId = userId;
        this.username = username;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.dueDate = dueDate;
        this.statusOptions = null;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public static TaskData templateData(final TaskData task, final Collection<CodeValueData> statusOptions) {
        return new TaskData(task.id, task.title, task.description, task.status, task.userId, task.createdDate, task.updatedDate,
                task.dueDate, task.username, statusOptions);
    }

    public static TaskData instance(final Long id, final String title, final String description, final String status, final Long userId,
            final LocalDate createdDate, final LocalDate updatedDate, final LocalDate dueDate, final String username) {
        return new TaskData(id, title, description, status, userId, createdDate, updatedDate, dueDate, username);
    }

    private TaskData(final Long id, final String title, final String description, final String status, final Long userId,
            final LocalDate createdDate, final LocalDate updatedDate, final LocalDate dueDate, final String username,
            final Collection<CodeValueData> statusOptions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.username = username;
        this.userId = userId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.dueDate = dueDate;
        this.statusOptions = statusOptions;
    }

    public String getStatus() {
        return this.status;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

}
