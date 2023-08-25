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
package org.apache.fineract;

import com.intuit.karate.junit5.Karate;
import java.util.Optional;

class KarateTestApplicationTest implements BaseKarate {

    @Karate.Test
    Karate runAll() {

        Optional<String> port = Optional.ofNullable(System.getenv("FINERACT_SERVER_PORT"));
        Optional<String> testuser = Optional.ofNullable(System.getenv("FINERACT_TEST_USER"));
        Optional<String> testpass = Optional.ofNullable(System.getenv("FINERACT_TEST_PASS"));
        Optional<String> tenantId = Optional.ofNullable(System.getenv("FINERACT_TEST_TENANT"));

        System.setProperty("server.port", port.orElse("8443"));
        System.setProperty("testuser", testuser.orElse("mifos"));
        System.setProperty("testpass", testpass.orElse("password"));
        System.setProperty("tenantId", tenantId.orElse("default"));

        Karate karate = new Karate().path("classpath:features");

        karate.outputHtmlReport(true);
        return karate;
    }

}
