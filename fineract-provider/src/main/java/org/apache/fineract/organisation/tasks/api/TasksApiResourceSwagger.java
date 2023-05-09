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
package org.apache.fineract.organisation.tasks.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * Created by brian on 09/05/2023.
 */

final class TasksApiResourceSwagger {

    private TasksApiResourceSwagger() {

    }

    @Schema(description = "PostTaskRequest")
    public static final class PostTaskRequest {

        private PostTaskRequest() {

        }

        @Schema(example = "Create A Loan")
        public String title;
        @Schema(example = "Create a loan of amount 40,000 for client account no. 1000023423")
        public String description;
        @Schema(example = "09 May 2023")
        public String dueDate;
        @Schema(example = "en")
        public String locale;
        @Schema(example = "dd MMMM yyyy")
        public String dateFormat;

    }

    @Schema(description = "PostTaskResponse")
    public static final class CreateTaskResponse {

        private CreateTaskResponse() {

        }

        @Schema(example = "1")
        public Long resourceId;
    }

    @Schema(description = "GetTaskResponse")
    public static final class RetrieveOneResponse {

        private RetrieveOneResponse() {

        }

        @Schema(example = "1")
        public Long id;
        @Schema(example = "Create Loan Account")
        public String title;
        @Schema(example = "Create Loan Account for Given client 10231023")
        public String description;
        @Schema(example = "task.status.pending")
        public String status;
        @Schema(example = "[2023,5,9]")
        public LocalDate createdDate;
        @Schema(example = "[2023,8,10]")
        public LocalDate dueDate;

    }

    @Schema(description = "PutTaskRequest")
    public static final class PutTaskRequest {

        private PutTaskRequest() {

        }

        @Schema(example = "200")
        public Integer status;
        @Schema(example = "Approve Client Details")
        public String description;

    }

    @Schema(description = "PutTaskResponse")
    public static final class UpdateTaskResponse {

        private UpdateTaskResponse() {

        }

        static final class PutTaskResponseChanges {

            private PutTaskResponseChanges() {}

            @Schema(example = "false")
            public Integer status;
            @Schema(example = "Activate loan account 1020312")
            public String description;
        }

        @Schema(example = "1")
        public Long resourceId;

    }
}
