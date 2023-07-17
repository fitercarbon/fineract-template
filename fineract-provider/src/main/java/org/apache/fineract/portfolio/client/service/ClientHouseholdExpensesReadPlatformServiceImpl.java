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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.data.ClientHouseholdExpensesData;
import org.apache.fineract.portfolio.client.data.OtherExpensesData;
import org.apache.fineract.portfolio.client.domain.OtherClientHouseholdExpensesRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientHouseholdExpensesReadPlatformServiceImpl implements ClientHouseholdExpensesReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    private final OtherClientHouseholdExpensesRepository otherClientHouseholdExpensesRepository;

    @Override
    public ClientHouseholdExpensesData getClientHouseholdExpenses(long clientId) {
        this.context.authenticatedUser();
        final var rm = new ClientHouseholdExpensesMapper();
        final String sql = "select " + rm.schema() + " where hex.client_id=?";
        var clientHouseholdExpenses = this.jdbcTemplate.query(sql, rm, clientId);

        if (!clientHouseholdExpenses.isEmpty() && clientHouseholdExpenses.get(0).getId() != null) {
            return ClientHouseholdExpensesData.withOtherExpensesData(clientHouseholdExpenses.get(0),
                    getOtherExpensesData(clientHouseholdExpenses.get(0).getId()));
        }

        return ClientHouseholdExpensesData.empty();

    }

    private Collection<OtherExpensesData> getOtherExpensesData(long clientExpensesId) {
        this.context.authenticatedUser();
        final var rm = new OtherClientHouseholdExpensesMapper();
        final String sql = "select " + rm.schema() + " where oex.client_expenses_id=?";

        return this.jdbcTemplate.query(sql, rm, clientExpensesId);
    }

    private static final class ClientHouseholdExpensesMapper implements RowMapper<ClientHouseholdExpensesData> {

        public String schema() {
            return "hex.id AS id, hex.client_id AS clientId, hex.food_expenses_amount AS foodExpensesAmount, hex.school_fees_amount AS schoolFeesAmount,"
                    + " hex.rent_amount AS rentAmount, hex.utilities_amount AS utilitiesAmount " + " FROM m_client_household_expenses hex";
        }

        @Override
        public ClientHouseholdExpensesData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final long id = rs.getLong("id");
            final long clientId = rs.getLong("clientId");
            final BigDecimal foodExpensesAmount = rs.getBigDecimal("foodExpensesAmount");
            final BigDecimal schoolFeesAmount = rs.getBigDecimal("schoolFeesAmount");
            final BigDecimal rentAmount = rs.getBigDecimal("rentAmount");
            final BigDecimal utilitiesAmount = rs.getBigDecimal("utilitiesAmount");

            return ClientHouseholdExpensesData.instance(id, clientId, foodExpensesAmount, schoolFeesAmount, rentAmount, utilitiesAmount);

        }
    }

    private static final class OtherClientHouseholdExpensesMapper implements RowMapper<OtherExpensesData> {

        public String schema() {
            return " oex.client_expenses_id AS clientExpensesId,otherExpensesType.code_value AS otherExpensesValue, oex.other_expense_id AS otherExpensesId, oex.amount AS otherExpensesAmount "
                    + " FROM m_other_client_expenses oex left join m_code_value otherExpensesType on otherExpensesType.id = oex.other_expense_id";
        }

        @Override
        public OtherExpensesData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final long otherExpensesId = rs.getLong("otherExpensesId");
            final BigDecimal otherExpensesAmount = rs.getBigDecimal("otherExpensesAmount");
            final String otherExpensesValue = rs.getString("otherExpensesValue");
            final CodeValueData otherExpenses = CodeValueData.instance(otherExpensesId, otherExpensesValue);
            return OtherExpensesData.instance(otherExpenses, otherExpensesAmount);

        }
    }

}
