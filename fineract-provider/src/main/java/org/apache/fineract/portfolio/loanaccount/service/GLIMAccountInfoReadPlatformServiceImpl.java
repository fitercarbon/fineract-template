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

package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.GLIMContainer;
import org.apache.fineract.portfolio.loanaccount.data.GlimRepaymentTemplate;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringAccountData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GLIMAccountInfoReadPlatformServiceImpl implements GLIMAccountInfoReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final AccountDetailsReadPlatformService accountDetailsReadPlatforService;

    @Autowired
    public GLIMAccountInfoReadPlatformServiceImpl(final PlatformSecurityContext context, final JdbcTemplate jdbcTemplate,
            final AccountDetailsReadPlatformService accountDetailsReadPlatforService) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
        this.accountDetailsReadPlatforService = accountDetailsReadPlatforService;

    }

    private static final class GLIMFieldsMapper implements RowMapper<GroupLoanIndividualMonitoringAccountData> {

        public String schema() {
            return "glim.id as glimId,ln.group_id as groupId,glim.account_number as accountNumber, ln.account_no as childAccountNumber,ln.principal_amount as childPrincipalAmount,glim.principal_amount as parentPrincipalAmount,glim.child_accounts_count as childAccountsCount,"
                    + "glim.loan_status_id as loanStatus , glim.actual_principal_amount as actualPrincipalAmount from glim_accounts glim,m_loan ln where glim.id=ln.glim_id";
        }

        @Override
        public GroupLoanIndividualMonitoringAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {

            final BigDecimal glimId = rs.getBigDecimal("glimId");

            final BigDecimal groupId = rs.getBigDecimal("groupId");

            final String accountNumber = rs.getString("accountNumber");

            final String childAccountNumber = rs.getString("childAccountNumber");

            final Long childAccountsCount = rs.getLong("childAccountsCount");

            final BigDecimal parentPrincipalAmount = rs.getBigDecimal("parentPrincipalAmount");
            final BigDecimal actualPrincipalAmount = rs.getBigDecimal("actualPrincipalAmount");

            final BigDecimal childPrincipalAmount = rs.getBigDecimal("childPrincipalAmount");

            final String loanStatus = LoanStatus.fromInt((int) rs.getLong("loanStatus")).toString();

            return GroupLoanIndividualMonitoringAccountData.getInstance(glimId, groupId, accountNumber, childAccountNumber,
                    childPrincipalAmount, parentPrincipalAmount, childAccountsCount, loanStatus, actualPrincipalAmount);

        }
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringAccountData> findGlimAccountsByGLIMId(final Long glimId) {
        this.context.authenticatedUser();

        final GLIMFieldsMapper rm = new GLIMFieldsMapper();
        final String sql = "select " + rm.schema() + " and glim.id=?";

        return this.jdbcTemplate.query(sql, rm, glimId); // NOSONAR
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringAccountData> findGlimAccountsByGroupId(String groupId) {
        this.context.authenticatedUser();

        final GLIMFieldsMapper rm = new GLIMFieldsMapper();
        final String sql = "select " + rm.schema() + " and ln.group_id=?";

        return this.jdbcTemplate.query(sql, rm, Long.parseLong(groupId)); // NOSONAR
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringAccountData> findGlimAccountByGroupId(String groupId) {
        this.context.authenticatedUser();

        GLIMMapper rm = new GLIMMapper();

        final String sql = "select " + rm.schema() + " where glim.group_id=?";

        return this.jdbcTemplate.query(sql, rm, Long.parseLong(groupId)); // NOSONAR
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringAccountData> findGlimAccountByParentAccountId(String parentAccountIds) {
        this.context.authenticatedUser();

        final GLIMFieldsMapper rm = new GLIMFieldsMapper();
        final String sql = "select " + rm.schema() + " and glim.accountNumber=?";

        return this.jdbcTemplate.query(sql, rm, parentAccountIds); // NOSONAR
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringAccountData> findGlimAccountByGroupIdandAccountNo(Long groupId, String accountNo) {
        this.context.authenticatedUser();

        GLIMMapper rm = new GLIMMapper();

        final String sql = "select " + rm.schema() + " where glim.group_id=? and glim.account_number=?";

        return this.jdbcTemplate.query(sql, rm, groupId, accountNo); // NOSONAR
    }

    @Override
    public Collection<GLIMContainer> findGlimAccount(Long groupId) {
        this.context.authenticatedUser();
        Collection<GroupLoanIndividualMonitoringAccountData> glimInfo = findGlimAccountByGroupId(groupId + "");

        // List<LoanAccountSummaryData> glimAccounts =
        // retrieveLoanAccountDetails(loanWhereClauseForGroupAndLoanType, new
        // Object[] { groupId });

        List<GLIMContainer> glimAccounts = new ArrayList<GLIMContainer>();
        for (GroupLoanIndividualMonitoringAccountData glimAccount : glimInfo) {

            List<LoanAccountSummaryData> childLoans = accountDetailsReadPlatforService
                    .retrieveLoanAccountDetailsByGroupIdAndGlimAccountNumber(groupId, glimAccount.getAccountNumber());
            glimAccounts
                    .add(new GLIMContainer(glimAccount.getGlimId(), glimAccount.getGroupId(), glimAccount.getAccountNumber(), childLoans,
                            glimAccount.getParentPrincipalAmount(), glimAccount.getLoanStatus(), glimAccount.getActualPrincipalAmount()));
        }

        return glimAccounts;
    }

    @Override
    public Collection<GLIMContainer> findGlimAccountbyGroupAndAccount(Long groupId, String accountNo) {
        this.context.authenticatedUser();
        Collection<GroupLoanIndividualMonitoringAccountData> glimInfo = findGlimAccountByGroupIdandAccountNo(groupId, accountNo);

        List<GLIMContainer> glimAccounts = new ArrayList<GLIMContainer>();
        for (GroupLoanIndividualMonitoringAccountData glimAccount : glimInfo) {

            List<LoanAccountSummaryData> childLoans = accountDetailsReadPlatforService
                    .retrieveLoanAccountDetailsByGroupIdAndGlimAccountNumber(groupId, glimAccount.getAccountNumber());
            glimAccounts
                    .add(new GLIMContainer(glimAccount.getGlimId(), glimAccount.getGroupId(), glimAccount.getAccountNumber(), childLoans,
                            glimAccount.getParentPrincipalAmount(), glimAccount.getLoanStatus(), glimAccount.getActualPrincipalAmount()));
        }

        return glimAccounts;
    }

    @Override
    public Collection<GlimRepaymentTemplate> findglimRepaymentTemplate(final Long glimId, final Boolean isRepayment) {
        this.context.authenticatedUser();

        GLIMRepaymentMapper rm = new GLIMRepaymentMapper();
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select ");
        sqlBuilder.append(rm.schema());
        sqlBuilder.append(" where glim.id=? ");

        if (isRepayment) {
            sqlBuilder.append(" and loan.loan_status_id = 300 ");
        }

        return this.jdbcTemplate.query(sqlBuilder.toString(), rm, glimId); // NOSONAR

    }

    private static final class GLIMMapper implements RowMapper<GroupLoanIndividualMonitoringAccountData> {

        public String schema() {
            return "glim.id as glimId,glim.group_id as groupId,glim.account_number as accountNumber,glim.principal_amount as principalAmount,glim.child_accounts_count as childAccountsCount,"
                    + "glim.loan_status_id as loanStatus , glim.actual_principal_amount as actualPrincipalAmount from glim_accounts glim";
        }

        @Override
        public GroupLoanIndividualMonitoringAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {

            final BigDecimal glimId = rs.getBigDecimal("glimId");

            final BigDecimal groupId = rs.getBigDecimal("groupId");

            final String accountNumber = rs.getString("accountNumber");

            final BigDecimal principalAmount = rs.getBigDecimal("principalAmount");
            final BigDecimal actualPrincipalAmount = rs.getBigDecimal("actualPrincipalAmount");

            final String loanStatus = LoanStatus.fromInt((int) rs.getLong("loanStatus")).toString();

            return GroupLoanIndividualMonitoringAccountData.getInstance1(glimId, groupId, accountNumber, principalAmount, loanStatus,
                    actualPrincipalAmount);

        }
    }

    private static final class GLIMRepaymentMapper implements RowMapper<GlimRepaymentTemplate> {

        public String schema() {
            return " glim.id as glimId,loan.group_id as groupId,client.id as clientId,glim.account_number as parentLoanAccountNo,"
                    + "glim.principal_amount as parentPrincipalAmount,loan.id as childLoanId,loan.account_no as childLoanAccountNo,loan.approved_principal as childPrincipalAmount,"
                    + "client.display_name as clientName , glim.actual_principal_amount as actualPrincipalAmount, "
                    + " loan.total_outstanding_derived as outStandingAmount,loan.last_repayment_date as lastRepaymentDate,loan.last_repayment_amount as lastRepaymentAmount,  "
                    + " loan.loan_status_id as statusId,ds.loan_decision_state as loanDecisionState ,la.overdue_since_date_derived as overdueSinceDate "
                    + " from glim_accounts glim left join m_loan loan on loan.glim_id=glim.id "
                    + " left join m_client client on client.id=loan.client_id "
                    + " left join m_loan_decision as ds on loan.id = ds.loan_id "
                    + " left join m_loan_arrears_aging la on la.loan_id = loan.id ";
        }

        @Override
        public GlimRepaymentTemplate mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final BigDecimal glimId = rs.getBigDecimal("glimId");

            final BigDecimal groupId = rs.getBigDecimal("groupId");

            final BigDecimal clientId = rs.getBigDecimal("clientId");

            final String clientName = rs.getString("clientName");

            final BigDecimal childLoanId = rs.getBigDecimal("childLoanId");

            final String parentLoanAccountNo = rs.getString("parentLoanAccountNo");

            final BigDecimal parentPrincipalAmount = rs.getBigDecimal("parentPrincipalAmount");
            final BigDecimal actualPrincipalAmount = rs.getBigDecimal("actualPrincipalAmount");
            final BigDecimal lastRepaymentAmount = rs.getBigDecimal("lastRepaymentAmount");
            final BigDecimal outStandingAmount = rs.getBigDecimal("outStandingAmount");
            final LocalDate lastRepaymentDate = JdbcSupport.getLocalDate(rs, "lastRepaymentDate");

            final String childLoanAccountNo = rs.getString("childLoanAccountNo");

            final BigDecimal childPrincipalAmount = rs.getBigDecimal("childPrincipalAmount");
            final Integer loanStatusId = JdbcSupport.getInteger(rs, "statusId");
            final LoanStatusEnumData loanStatus = LoanEnumerations.status(loanStatusId);

            final Long loanDecisionStateId = JdbcSupport.getLong(rs, "loanDecisionState");
            EnumOptionData loanDecisionStateEnumData = null;
            if (loanDecisionStateId != null) {
                loanDecisionStateEnumData = LoanEnumerations.loanDecisionState(loanDecisionStateId.intValue());
            }
            final LocalDate overdueSinceDate = JdbcSupport.getLocalDate(rs, "overdueSinceDate");
            Boolean inArrears = true;
            if (overdueSinceDate == null) {
                inArrears = false;
            }

            GlimRepaymentTemplate glimRepaymentTemplate = GlimRepaymentTemplate.getInstance(glimId, groupId, clientId, clientName,
                    childLoanId, parentLoanAccountNo, parentPrincipalAmount, childLoanAccountNo, childPrincipalAmount,
                    actualPrincipalAmount, lastRepaymentAmount, outStandingAmount, lastRepaymentDate);
            glimRepaymentTemplate.setLoanStatus(loanStatus);
            glimRepaymentTemplate.setLoanDecisionStateEnumData(loanDecisionStateEnumData);
            glimRepaymentTemplate.setInArrears(inArrears);

            return glimRepaymentTemplate;
        }
    }

}
