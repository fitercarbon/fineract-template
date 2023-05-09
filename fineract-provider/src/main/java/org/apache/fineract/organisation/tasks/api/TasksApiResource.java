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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.tasks.data.TaskData;
import org.apache.fineract.organisation.tasks.service.TasksReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/tasks")
@Component
@Scope("singleton")
@Tag(name = "Tasks", description = "Allows you to model tasks. You can create, update, tasks that you have to do.")
public class TasksApiResource {

    /**
     * The set of parameters that are supported in response for {@link TaskData}.
     */
    private final Set<String> responseDataParameters = new HashSet<>(Arrays.asList("id", "title", "description", "status", "userid"));

    private final String resourceNameForPermissions = "TASK";

    private final PlatformSecurityContext context;
    private final TasksReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<TaskData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public TasksApiResource(final PlatformSecurityContext context, final TasksReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<TaskData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Tasks", description = "Returns the list of tasks.\n" + "\n" + "Example Requests:\n" + "\n"
            + "task\n\n\n\n" + "\n" + "Retrieve a task by status\n" + "\n" + "Returns the details of a task based on status.\n" + "\n"
            + "By default it Returns all the tasks.\n" + "\n" + "If status=DONE, then it returns all tasks that are Completed.\n" + "\n"
            + "and for status=ALL, it Returns all PENDING, IN PROGRESS and DONE Tasks.\n" + "\n" + "Example Requests:\n" + "\n"
            + "tasks?status=PENDING")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TasksApiResourceSwagger.RetrieveOneResponse.class)))) })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("status") @Parameter(description = "status") final Long status) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<TaskData> tasks;

        tasks = this.readPlatformService.retrieveAllTasks(status);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, tasks, this.responseDataParameters);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a task", description = "Creates a task.\n" + "\n" + "Mandatory Fields: \n"
            + "title, description, dueDate\n" + "\n" + "Optional Fields: \n" + "")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = TasksApiResourceSwagger.PostTaskRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = TasksApiResourceSwagger.CreateTaskResponse.class))) })
    public String create(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createTask().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{taskId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve a Task", description = "Returns the details of a Task.\n" + "\n" + "Example Requests:\n" + "\n"
            + "tasks/1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = TasksApiResourceSwagger.RetrieveOneResponse.class))) })
    public String retrieveOne(@PathParam("taskId") @Parameter(description = "taskId") final Long taskId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        TaskData task = this.readPlatformService.retrieveTask(taskId);
        if (settings.isTemplate()) {
            task = TaskData.templateData(task, null);
        }
        return this.toApiJsonSerializer.serialize(settings, task, this.responseDataParameters);
    }

    @PUT
    @Path("{taskId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Update a task", description = "Updates the details of a task.")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = TasksApiResourceSwagger.PutTaskRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = TasksApiResourceSwagger.UpdateTaskResponse.class))) })
    public String update(@PathParam("taskId") @Parameter(description = "taskId") final Long taskId,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateTask(taskId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}
