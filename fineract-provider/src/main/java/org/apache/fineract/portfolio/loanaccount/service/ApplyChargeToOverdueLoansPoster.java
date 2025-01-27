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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.domain.FineractContext;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.OverdueLoanScheduleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ApplyChargeToOverdueLoansPoster implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplyChargeToOverdueLoansPoster.class);
    private static final SecureRandom random = new SecureRandom();
    private List<Long> loanIds;
    private LoanWritePlatformService loanWritePlatformService;
    private ConfigurationDomainService configurationDomainService;
    private LoanReadPlatformService loanReadPlatformService;
    private FineractContext context;

    public void setContext(FineractContext context) {
        this.context = context;
    }

    public void setLoanIds(final List<Long> loanIds) {
        this.loanIds = loanIds;
    }

    public void setLoanWritePlatformService(final LoanWritePlatformService loanWritePlatformService) {
        this.loanWritePlatformService = loanWritePlatformService;
    }

    public void setLoanReadPlatformService(final LoanReadPlatformService loanReadPlatformService) {
        this.loanReadPlatformService = loanReadPlatformService;
    }

    public void setConfigurationDomainService(ConfigurationDomainService configurationDomainService) {
        this.configurationDomainService = configurationDomainService;
    }

    @Override
    @SuppressFBWarnings(value = {
            "DMI_RANDOM_USED_ONLY_ONCE" }, justification = "False positive for random object created and used only once")
    public Void call() throws JobExecutionException {
        ThreadLocalContextUtil.init(this.context);
        Integer maxNumberOfRetries = this.context.getTenantContext().getConnection().getMaxRetriesOnDeadlock();
        Integer maxIntervalBetweenRetries = this.context.getTenantContext().getConnection().getMaxIntervalBetweenRetries();
        final Long penaltyWaitPeriodValue = configurationDomainService.retrievePenaltyWaitPeriod();
        final Boolean backdatePenalties = configurationDomainService.isBackdatePenaltiesEnabled();

        int i = 0;
        if (!loanIds.isEmpty()) {
            final Collection<OverdueLoanScheduleData> overdueLoanScheduledInstallments = loanReadPlatformService
                    .retrieveAllLoansWithOverdueInstallments(penaltyWaitPeriodValue, backdatePenalties, loanIds.get(0),
                            loanIds.get(loanIds.size() - 1));
            Map<Long, List<OverdueLoanScheduleData>> groupedOverdueData = overdueLoanScheduledInstallments.stream()
                    .collect(Collectors.groupingBy(OverdueLoanScheduleData::getLoanId));

            List<Throwable> errors = new ArrayList<>();
            for (Long loanId : loanIds) {
                LOG.info("Loan ID {}", loanId);
                Integer numberOfRetries = 0;
                while (numberOfRetries <= maxNumberOfRetries) {
                    try {
                        this.loanWritePlatformService.applyOverdueChargesForLoan(loanId, groupedOverdueData.get(loanId));
                        numberOfRetries = maxNumberOfRetries + 1;
                    } catch (CannotAcquireLockException | ObjectOptimisticLockingFailureException exception) {
                        LOG.info("Recalculate interest job has been retried {} time(s)", numberOfRetries);
                        // Fail if the transaction has been retired for
                        // maxNumberOfRetries
                        if (numberOfRetries >= maxNumberOfRetries) {
                            LOG.error(
                                    "Recalculate interest job has been retried for the max allowed attempts of {} and will be rolled back",
                                    numberOfRetries);
                            errors.add(exception);
                            break;
                        }
                        // Else sleep for a random time (between 1 to 10
                        // seconds) and continue
                        try {
                            int randomNum = random.nextInt(maxIntervalBetweenRetries + 1);
                            Thread.sleep(1000 + (randomNum * 1000));
                            numberOfRetries = numberOfRetries + 1;
                        } catch (InterruptedException e) {
                            LOG.error("Interest recalculation for loans retry failed due to InterruptedException", e);
                            errors.add(e);
                            break;
                        }
                    } catch (Exception e) {
                        LOG.error("Interest recalculation for loans failed for account {}", loanId, e);
                        numberOfRetries = maxNumberOfRetries + 1;
                        errors.add(e);
                    }
                    i++;
                }
                LOG.info("Loans count {}", i);
            }
            if (!errors.isEmpty()) {
                throw new JobExecutionException(errors);
            }
        }
        return null;
    }
}
