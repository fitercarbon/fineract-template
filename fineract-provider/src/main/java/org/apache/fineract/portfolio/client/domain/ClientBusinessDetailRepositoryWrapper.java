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
package org.apache.fineract.portfolio.client.domain;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ClientBusinessDetailRepositoryWrapper {

    private final ClientBusinessDetailRepository clientBusinessDetailRepository;
    private final ClientRepositoryWrapper clientRepositoryWrapper;

    public ClientBusinessDetailRepositoryWrapper(ClientBusinessDetailRepository clientBusinessDetailRepository,
            ClientRepositoryWrapper clientRepositoryWrapper) {
        this.clientBusinessDetailRepository = clientBusinessDetailRepository;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
    }

    public List<ClientBusinessDetail> findByClientId(final Long clientId) {
        final Client client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
        return this.clientBusinessDetailRepository.findByClientId(client);
    }
}
