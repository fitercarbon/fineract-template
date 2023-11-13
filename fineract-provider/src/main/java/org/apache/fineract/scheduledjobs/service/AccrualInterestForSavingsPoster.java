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
package org.apache.fineract.scheduledjobs.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.fineract.infrastructure.core.domain.FineractContext;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.portfolio.loanaccount.service.ApplyChargeToOverdueLoansPoster;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class AccrualInterestForSavingsPoster implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplyChargeToOverdueLoansPoster.class);
    private static final SecureRandom random = new SecureRandom();
    private List<Long> savingsAccountIds;
    private SavingsAccountWritePlatformService savingsAccountWritePlatformService;

    private FineractContext context;

    public void setContext(FineractContext context) {
        this.context = context;
    }

    public void setSavingsAccountIds(List<Long> savingsAccountIds) {
        this.savingsAccountIds = savingsAccountIds;
    }

    public void setSavingsAccountWritePlatformService(SavingsAccountWritePlatformService savingsAccountWritePlatformService) {
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
    }

    @Override
    @SuppressFBWarnings(value = {
            "DMI_RANDOM_USED_ONLY_ONCE" }, justification = "False positive for random object created and used only once")
    public Void call() throws JobExecutionException {
        ThreadLocalContextUtil.init(this.context);
        Integer maxNumberOfRetries = this.context.getTenantContext().getConnection().getMaxRetriesOnDeadlock();
        Integer maxIntervalBetweenRetries = this.context.getTenantContext().getConnection().getMaxIntervalBetweenRetries();
        if (!savingsAccountIds.isEmpty()) {
            List<Throwable> errors = new ArrayList<>();
            for (Long savingAccountId : savingsAccountIds) {
                if (savingAccountId == 0) {
                    continue;
                }
                LOG.info("Processing Accruals Saving ID " + savingAccountId);
                Integer numberOfRetries = 0;
                while (numberOfRetries <= maxNumberOfRetries) {
                    try {
                        this.savingsAccountWritePlatformService.postAccrualInterest(savingAccountId, DateUtils.getLocalDateOfTenant(),
                                false);
                        numberOfRetries = maxNumberOfRetries + 1;
                    } catch (CannotAcquireLockException | ObjectOptimisticLockingFailureException exception) {
                        LOG.info("Accrual Interest For Savings job has been retried {} time(s)", numberOfRetries);
                        // Fail if the transaction has been retired for
                        // maxNumberOfRetries
                        if (numberOfRetries >= maxNumberOfRetries) {
                            LOG.error(
                                    "Accrual Interest For Savings job has been retried for the max allowed attempts of {} and will be rolled back",
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
                            LOG.error("Accrual Interest For Savings retry failed due to InterruptedException", e);
                            errors.add(e);
                            break;
                        }
                    } catch (Exception e) {
                        LOG.error("Accrual Interest For Savings failed for account {}", savingAccountId, e);
                        numberOfRetries = maxNumberOfRetries + 1;
                        errors.add(e);
                    }
                }
            }
            if (!errors.isEmpty()) {
                throw new JobExecutionException(errors);
            }
        }
        return null;
    }
}
