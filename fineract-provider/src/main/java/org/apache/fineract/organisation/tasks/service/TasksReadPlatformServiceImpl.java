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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.SQLBuilder;
import org.apache.fineract.organisation.tasks.data.TaskData;
import org.apache.fineract.organisation.tasks.domain.TaskStatusTypes;
import org.apache.fineract.organisation.tasks.exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class TasksReadPlatformServiceImpl implements TasksReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public TasksReadPlatformServiceImpl(final PlatformSecurityContext context, final JdbcTemplate jdbcTemplate) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final class TaskMapper implements RowMapper<TaskData> {

        public String schema() {
            return " T.id as id,T.user_id as userId, T.title, T.description, T.due_date as dueDate,"
                    + " T.created_date as createdDate, T.updated_date as updatedDate, T.assigned_to as assignedTo, "
                    + "T.status, U.username from m_tasks T  inner join m_appuser U on U.id = T.user_id ";
        }

        @Override
        public TaskData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long userId = rs.getLong("userId");
            final String title = rs.getString("title");
            final String description = rs.getString("description");
            final String username = rs.getString("username");
            final Integer status = rs.getInt("status");
            final Long assignedTo = rs.getLong("assignedTo");
            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
            final LocalDate updatedDate = JdbcSupport.getLocalDate(rs, "updatedDate");
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");

            String taskStatus = TaskStatusTypes.fromInt(status).getCode();
            return TaskData.instance(id, title, description, taskStatus, userId, createdDate, updatedDate, dueDate, username);
        }
    }

    @Override
    public TaskData retrieveTask(final Long taskId) {

        // adding the Authorization criteria so that a user cannot see an
        // employee who does not belong to his office or a sub office for his
        // office.
        final String hierarchy = this.context.authenticatedUser().getOffice().getHierarchy() + "%";

        try {
            final TaskMapper rm = new TaskMapper();
            final String sql = "select " + rm.schema() + " where T.id = ? order by T.id desc ";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { taskId }); // NOSONAR
        } catch (final EmptyResultDataAccessException e) {
            throw new TaskNotFoundException(taskId, e);
        }
    }

    @Override
    public Collection<TaskData> retrieveAllTasks(final Long status) {
        final SQLBuilder extraCriteria = getAllTasksStatus(status);
        return retrieveAllTasks(extraCriteria);
    }

    private Collection<TaskData> retrieveAllTasks(final SQLBuilder extraCriteria) {

        final TaskMapper rm = new TaskMapper();
        String sql = "select " + rm.schema();

        final Long loggedInUser = this.context.authenticatedUser().getId();

        extraCriteria.addCriteria(" T.user_id = ", loggedInUser);

        sql += " " + extraCriteria.getSQLTemplate();
        sql = sql + " order by T.id desc ";

        return this.jdbcTemplate.query(sql, rm, extraCriteria.getArguments()); // NOSONAR
    }

    private SQLBuilder getAllTasksStatus(final Long status) {

        final SQLBuilder extraCriteria = new SQLBuilder();
        // Passing status parameter to get ACTIVE (By Default), INACTIVE or ALL
        // (Both active and Inactive) employees
        if (status != null) {
            extraCriteria.addCriteria(" T.status =", status);
        }

        return extraCriteria;
    }
}
