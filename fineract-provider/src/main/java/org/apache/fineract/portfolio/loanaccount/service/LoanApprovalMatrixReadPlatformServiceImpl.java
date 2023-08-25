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

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformServiceImpl;
import org.apache.fineract.portfolio.loanaccount.data.LoanApprovalMatrixData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanApprovalMatrix;
import org.apache.fineract.portfolio.loanaccount.domain.LoanApprovalMatrixRepository;
import org.apache.fineract.portfolio.loanaccount.mapper.LoanApprovalMatrixMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApprovalMatrixReadPlatformServiceImpl implements LoanApprovalMatrixReadPlatformService {

    private final LoanApprovalMatrixRepository loanApprovalMatrixRepository;
    private final LoanApprovalMatrixMapper mapper;
    private final CurrencyReadPlatformServiceImpl currencyReadPlatformService;

    @Override
    public List<LoanApprovalMatrixData> findAll() {
        List<LoanApprovalMatrix> loanApprovalMatrices = loanApprovalMatrixRepository.findAll();
        return mapper.map(loanApprovalMatrices);
    }

    @Override
    public LoanApprovalMatrixData getApprovalMatrixDetails(Long approvalMatrixId) {
        LoanApprovalMatrixData loanApprovalMatrixData = null;
        Optional<LoanApprovalMatrix> loanApproval = loanApprovalMatrixRepository.findById(approvalMatrixId);
        if (loanApproval.isPresent()) {
            CurrencyData currencyData = currencyReadPlatformService.retrieveCurrency(loanApproval.get().getCurrency());

            loanApprovalMatrixData = mapper.map(loanApproval.get());
            loanApprovalMatrixData.setCurrencyData(currencyData);
        }
        return loanApprovalMatrixData;
    }

}
