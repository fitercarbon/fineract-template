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

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecision;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecisionRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecisionState;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanDecisionStateUtilService {

    private final ConfigurationReadPlatformService configurationReadPlatformService;
    private final LoanDecisionRepository loanDecisionRepository;

    public void validateLoanAccountWithExtraLoanDecisionStagesConfiguredGlobally(Loan loan, final JsonCommand command) {
        final Boolean isExtendLoanLifeCycleConfig = isExtendLoanLifeCycleConfig();

        LocalDate approveOnDate = command.localDateValueOfParameterNamed(LoanApiConstants.approvedOnDateParameterName);
        final LoanDecision loanDecision = this.loanDecisionRepository.findLoanDecisionByLoanId(loan.getId());
        if (isExtendLoanLifeCycleConfig) {
            if (loanDecision == null) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.account.should.extend.decision.engine.to.review.Loan.application",
                        "Loan Account is not permitted for Approval since new workflow [Add-More-Stages-To-A-Loan-Life-Cycle] is activated and next status is [Review Application]");
            }
            if (loanDecision != null && !loanDecision.getReviewApplicationSigned()) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.account.should.extend.decision.engine.required.state.is.review.application",
                        "Loan Account is not permitted for Approval . [Review Application ] to proceed ");
            }

            if (loanDecision.getPrepareAndSignContractOn() != null && approveOnDate.isBefore(loanDecision.getPrepareAndSignContractOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Approval.date.should.be.after.loan.prepare.and.sign.contract.date",
                        "Approval on  date" + approveOnDate + " should be after Prepare And Sign Contract date "
                                + loanDecision.getPrepareAndSignContractOn());
            }

            if (loanDecision.getIcReviewDecisionLevelFiveOn() != null
                    && approveOnDate.isBefore(loanDecision.getIcReviewDecisionLevelFiveOn())) {
                throw new GeneralPlatformDomainRuleException("error.msg.Approval.date.should.be.after.Ic.Review.decision.level.Five.date",
                        "Approval on  date" + approveOnDate + " should be after IC ReviewDecision Level Five date "
                                + loanDecision.getIcReviewDecisionLevelFiveOn());
            }

            if (loanDecision.getIcReviewDecisionLevelFourOn() != null
                    && approveOnDate.isBefore(loanDecision.getIcReviewDecisionLevelFourOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Approval.date.should.be.after.Ic.Review.decision.level.Four.date",
                        "Approval on  date" + approveOnDate + " should be after IC ReviewDecision Level Four date "
                                + loanDecision.getIcReviewDecisionLevelFourOn());
            }
            if (loanDecision.getIcReviewDecisionLevelThreeOn() != null
                    && approveOnDate.isBefore(loanDecision.getIcReviewDecisionLevelThreeOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Approval.date.should.be.after.Ic.Review.decision.level.Three.date",
                        "Approval on  date" + approveOnDate + " should be after IC ReviewDecision Level Three date "
                                + loanDecision.getIcReviewDecisionLevelThreeOn());
            }

            if (loanDecision.getIcReviewDecisionLevelTwoOn() != null
                    && approveOnDate.isBefore(loanDecision.getIcReviewDecisionLevelTwoOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Approval.date.should.be.after.Ic.Review.decision.level.Two.date",
                        "Approval on  date" + approveOnDate + " should be after IC ReviewDecision Level two date "
                                + loanDecision.getIcReviewDecisionLevelTwoOn());
            }
            if (approveOnDate.isBefore(loanDecision.getIcReviewDecisionLevelOneOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Approval.date.should.be.after.Ic.Review.decision.level.one.date",
                        "Approval on  date" + approveOnDate + " should be after IC ReviewDecision Level One date "
                                + loanDecision.getIcReviewDecisionLevelOneOn());
            }
            if (approveOnDate.isBefore(loanDecision.getCollateralReviewOn())) {
                throw new GeneralPlatformDomainRuleException("error.msg.loan.Approval.date.should.be.after.collateral.review.date",
                        "Approval on  date" + approveOnDate + " should be after Collateral Review Approved date "
                                + loanDecision.getCollateralReviewOn());
            }
            if (approveOnDate.isBefore(loanDecision.getDueDiligenceOn())) {
                throw new GeneralPlatformDomainRuleException("error.msg.loan.Approval.date.should.be.after.Due.Diligence.date",
                        "Approval on date" + approveOnDate + " should be after Loan Due Diligence Approved date "
                                + loanDecision.getDueDiligenceOn());
            }
            if (approveOnDate.isBefore(loanDecision.getReviewApplicationOn())) {
                throw new GeneralPlatformDomainRuleException("error.msg.loan.Approval.date.should.be.after.review.application.date",
                        "Approval on date" + approveOnDate + " should be after Loan Review Application Approved date "
                                + loanDecision.getReviewApplicationOn());
            }
            if (!loan.getLoanDecisionState().equals(loanDecision.getLoanDecisionState())) {
                throw new GeneralPlatformDomainRuleException("error.msg.loan.decision.state.does.not.reconcile",
                        "Loan Account Decision state Does not reconcile . Operation is terminated");
            }
            if (!LoanDecisionState.fromInt(loan.getLoanDecisionState()).isPrepareAndSignContract()) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.approval.is.terminated.expected.state.is.prepare.and.sign.contract.but.found.different",
                        "Loan Account can't be approved because it's Decision state is invalid. Expected "
                                + LoanDecisionState.PREPARE_AND_SIGN_CONTRACT.getValue() + " but found " + loan.getLoanDecisionState());
            }
        }
    }

    public void validateLoanReviewApplicationStateIsFiredBeforeDisbursal(Loan loan, final JsonCommand command) {
        final Boolean isExtendLoanLifeCycleConfig = isExtendLoanLifeCycleConfig();

        final LoanDecision loanDecision = this.loanDecisionRepository.findLoanDecisionByLoanId(loan.getId());

        LocalDate actualDisbursementDate = command.localDateValueOfParameterNamed("actualDisbursementDate");

        if (isExtendLoanLifeCycleConfig) {

            if (loanDecision == null) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.account.should.extend.decision.engine.to.review.Loan.application",
                        "Loan Account is not permitted for Disbursement since new workflow [Add-More-Stages-To-A-Loan-Life-Cycle] is activated and next stage is [Review Application]");
            }
            if (loanDecision != null && !loanDecision.getReviewApplicationSigned()) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.account.should.extend.decision.engine.required.state.is.review.application",
                        "Loan Account is not permitted for Disbursement . [Review Application ] to proceed ");
            }

            if (loanDecision.getPrepareAndSignContractOn() != null
                    && actualDisbursementDate.isBefore(loanDecision.getPrepareAndSignContractOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Disbursement.date.should.be.after.loan.prepare.and.sign.contract.date",
                        "Disbursement on  date" + actualDisbursementDate + " should be after Prepare And Sign Contract date "
                                + loanDecision.getPrepareAndSignContractOn());
            }

            if (loanDecision.getIcReviewDecisionLevelFiveOn() != null
                    && actualDisbursementDate.isBefore(loanDecision.getIcReviewDecisionLevelFiveOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.Disbursement.date.should.be.after.Ic.Review.decision.level.Five.date",
                        "Disbursement on  date" + actualDisbursementDate + " should be after IC ReviewDecision Level Five date "
                                + loanDecision.getIcReviewDecisionLevelFiveOn());
            }

            if (loanDecision.getIcReviewDecisionLevelFourOn() != null
                    && actualDisbursementDate.isBefore(loanDecision.getIcReviewDecisionLevelFourOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Disbursement.date.should.be.after.Ic.Review.decision.level.Four.date",
                        "Disbursement on  date" + actualDisbursementDate + " should be after IC ReviewDecision Level Four date "
                                + loanDecision.getIcReviewDecisionLevelFourOn());
            }
            if (loanDecision.getIcReviewDecisionLevelThreeOn() != null
                    && actualDisbursementDate.isBefore(loanDecision.getIcReviewDecisionLevelThreeOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Disbursement.date.should.be.after.Ic.Review.decision.level.Three.date",
                        "Disbursement on  date" + actualDisbursementDate + " should be after IC ReviewDecision Level Three date "
                                + loanDecision.getIcReviewDecisionLevelThreeOn());
            }

            if (loanDecision.getIcReviewDecisionLevelTwoOn() != null
                    && actualDisbursementDate.isBefore(loanDecision.getIcReviewDecisionLevelTwoOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Disbursement.date.should.be.after.Ic.Review.decision.level.Two.date",
                        "Disbursement on  date" + actualDisbursementDate + " should be after IC ReviewDecision Level two date "
                                + loanDecision.getIcReviewDecisionLevelTwoOn());
            }
            if (actualDisbursementDate.isBefore(loanDecision.getIcReviewDecisionLevelOneOn())) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Disbursement.date.should.be.after.Ic.Review.decision.level.one.date",
                        "Disbursement on  date" + actualDisbursementDate + " should be after IC ReviewDecision Level One date "
                                + loanDecision.getIcReviewDecisionLevelOneOn());
            }
            if (actualDisbursementDate.isBefore(loanDecision.getCollateralReviewOn())) {
                throw new GeneralPlatformDomainRuleException("error.msg.loan.Disbursement.date.should.be.after.collateral.review.date",
                        "Disbursement on  date" + actualDisbursementDate + " should be after Collateral Review Approved date "
                                + loanDecision.getCollateralReviewOn());
            }
            if (actualDisbursementDate.isBefore(loanDecision.getDueDiligenceOn())) {
                throw new GeneralPlatformDomainRuleException("error.msg.loan.Disbursement.date.should.be.after.Due.Diligence.date",
                        "Disbursement on date" + actualDisbursementDate + " should be after Loan Due Diligence Approved date "
                                + loanDecision.getDueDiligenceOn());
            }
            if (actualDisbursementDate.isBefore(loanDecision.getReviewApplicationOn())) {
                throw new GeneralPlatformDomainRuleException("error.msg.loan.Disbursement.date.should.be.after.review.application.date",
                        "Disbursement on date" + actualDisbursementDate + " should be after Loan Review Application Approved date "
                                + loanDecision.getReviewApplicationOn());
            }
            if (!loan.getLoanDecisionState().equals(loanDecision.getLoanDecisionState())) {
                throw new GeneralPlatformDomainRuleException("error.msg.loan.decision.state.does.not.reconcile",
                        "Loan Account Decision state Does not reconcile . Operation is terminated");
            }
            if (!LoanDecisionState.fromInt(loan.getLoanDecisionState()).isPrepareAndSignContract()) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.Disbursement.is.terminated.expected.state.is.prepare.and.sign.contract.but.found.different",
                        "Loan Account can't be Disbursement because it's Decision state is invalid. Expected "
                                + LoanDecisionState.PREPARE_AND_SIGN_CONTRACT.getValue() + " but found " + loan.getLoanDecisionState());
            }
        }
    }

    @NotNull
    private Boolean isExtendLoanLifeCycleConfig() {
        final GlobalConfigurationPropertyData extendLoanLifeCycleConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Add-More-Stages-To-A-Loan-Life-Cycle");
        final Boolean isExtendLoanLifeCycleConfig = extendLoanLifeCycleConfig.isEnabled();
        return isExtendLoanLifeCycleConfig;
    }

}
