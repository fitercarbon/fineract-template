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
package org.apache.fineract.infrastructure.hooks.processor;

import static org.apache.fineract.infrastructure.hooks.api.HookApiConstants.BasicAuthParamName;
import static org.apache.fineract.infrastructure.hooks.api.HookApiConstants.apiKeyName;
import static org.apache.fineract.infrastructure.hooks.api.HookApiConstants.contentTypeName;
import static org.apache.fineract.infrastructure.hooks.api.HookApiConstants.payloadURLName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.domain.FineractContext;
import org.apache.fineract.infrastructure.hooks.domain.Hook;
import org.apache.fineract.infrastructure.hooks.domain.HookConfiguration;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.template.domain.Template;
import org.springframework.stereotype.Service;
import retrofit2.Callback;

@Service
@RequiredArgsConstructor
public class WebHookProcessor implements HookProcessor {

    private final ClientRepositoryWrapper clientRepository;
    private final ProcessorHelper processorHelper;

    @Override
    public void process(final Hook hook, final String payload, final String entityName, final String actionName,
            final FineractContext context) throws IOException {
        final HashMap<String, Object> payLoadMap = new ObjectMapper().readValue(payload, HashMap.class);

        if (payLoadMap.get("response") instanceof Map<?, ?>) {
            Map<String, Object> responseMap = (Map<String, Object>) payLoadMap.get("response");
            if (responseMap.get("errors") instanceof List && ((List) responseMap.get("errors")).size() > 0) {
                return;
            }
        }

        if (payLoadMap.get("request") instanceof Map<?, ?>) {
            Map<String, Object> requestMap = (Map<String, Object>) payLoadMap.get("request");
            if (requestMap.get("clientId") != null) {
                Long clientId = Long.parseLong(String.valueOf(requestMap.get("clientId")));
                Client client = clientRepository.findOneWithNotFoundDetection(clientId);
                payLoadMap.put("client", client);
            }
        }
        final Set<HookConfiguration> config = hook.getHookConfig();

        String url = "";
        String contentType = "";

        String basicAuthCreds = "";

        String apiKey = "";

        String apiKeyValue = "";

        for (final HookConfiguration conf : config) {
            final String fieldName = conf.getFieldName();
            if (fieldName.equals(payloadURLName)) {
                url = conf.getFieldValue();
            }
            if (fieldName.equals(contentTypeName)) {
                contentType = conf.getFieldValue();
            }
            if (fieldName.equals(BasicAuthParamName) && !conf.getFieldValue().isEmpty()) {
                basicAuthCreds = "Basic " + conf.getFieldValue();
            }
            if (fieldName.equals(apiKeyName) && !conf.getFieldValue().isEmpty()) {
                String keyValuePair = conf.getFieldValue();
                apiKey = StringUtils.split(keyValuePair, ":")[0];
                apiKeyValue = StringUtils.split(keyValuePair, ":")[1];
            }
        }
        final String compilePayLoad = compilePayLoad(hook.getUgdTemplate(), payLoadMap);
        sendRequest(url, contentType, compilePayLoad, entityName, actionName, context, basicAuthCreds, apiKey, apiKeyValue);
    }

    @SuppressWarnings("unchecked")
    private void sendRequest(final String url, final String contentType, final String payload, final String entityName,
            final String actionName, final FineractContext context, String basicAuthCreds, String apiKey, String apiKeyValue) {

        final String fineractEndpointUrl = System.getProperty("baseUrl");
        final WebHookService service = processorHelper.createWebHookService(url);

        @SuppressWarnings("rawtypes")
        final Callback callback = processorHelper.createCallback(url);
        final String validPayload = payload.replace("&nbsp;", " ").replace("&quot;", "\"").replaceAll("\\\\&quot;", "\"");
        if (contentType.equalsIgnoreCase("json") || contentType.contains("json")) {

            final JsonObject json = JsonParser.parseString(validPayload).getAsJsonObject();

            if (!StringUtils.isBlank(basicAuthCreds)) {
                service.sendJsonRequestBasicAuth(entityName, actionName, context.getTenantContext().getTenantIdentifier(),
                        fineractEndpointUrl, basicAuthCreds, json).enqueue(callback);
            } else if (!StringUtils.isBlank(apiKey)) {
                service.sendJsonRequestApiKey(entityName, actionName, context.getTenantContext().getTenantIdentifier(), fineractEndpointUrl,
                        apiKeyValue, json).enqueue(callback);
            } else
                service.sendJsonRequest(entityName, actionName, context.getTenantContext().getTenantIdentifier(), fineractEndpointUrl, json)
                        .enqueue(callback);
        } else {
            Map<String, String> map = new HashMap<>();
            map = new Gson().fromJson(validPayload, map.getClass());
            service.sendFormRequest(entityName, actionName, context.getTenantContext().getTenantIdentifier(), fineractEndpointUrl, map)
                    .enqueue(callback);
        }
    }

    private String compilePayLoad(final Template template, final Map<String, Object> payLoadObj) throws IOException {
        final MustacheFactory mf = new DefaultMustacheFactory();
        final Mustache mustache = mf.compile(new StringReader(template.getText()), "");
        StringWriter writer = new StringWriter();
        mustache.execute(writer, payLoadObj).flush();
        return writer.toString().replaceAll("<[^>]*>", "");
    }
}
