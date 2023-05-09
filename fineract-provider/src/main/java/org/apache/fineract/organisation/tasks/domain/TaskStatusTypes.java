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

public enum TaskStatusTypes {

    INVALID(0, "staffOrganisationalRoleType.invalid"), //
    PENDING(100, "task.status.pending"), //
    IN_PROGRESS(200, "task.status.progress"), //
    BLOCKED(300, "task.status.blocked"), //
    COMPLETED(400, "task.status.completed");

    private final Integer value;
    private final String code;

    TaskStatusTypes(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static TaskStatusTypes fromInt(final Integer statusValue) {
        TaskStatusTypes statusType = TaskStatusTypes.INVALID;
        switch (statusValue) {
            case 100:
                statusType = PENDING;
            break;
            case 200:
                statusType = IN_PROGRESS;
            break;
            case 300:
                statusType = BLOCKED;
            break;
            case 400:
                statusType = COMPLETED;
            break;
        }
        return statusType;
    }
}
