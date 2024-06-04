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
package org.apache.fineract.infrastructure.core.filters;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class FilterConstraint {

    private String filterSelection;
    private FilterElement filterElement;
    private String value;
    private String secondValue;

    private FilterType filterType;
    private List<String> values;

    public FilterConstraint() {}

    public String getFilterSelection() {
        return filterSelection;
    }

    public void setFilterSelection(String filterSelection) {
        this.filterSelection = filterSelection;
    }

    public FilterElement getFilterElement() {
        return filterElement;
    }

    public void setFilterElement(FilterElement filterElement) {
        this.filterElement = filterElement;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(String secondValue) {
        this.secondValue = secondValue;
    }

    public List<String> getValues() {
        if (values == null) {
            List<String> newValues = new ArrayList<>();
            if (StringUtils.isNotBlank(value)) {
                newValues.add(value);
            }
            if (StringUtils.isNotBlank(secondValue)) {
                newValues.add(secondValue);
            }
            return newValues;

        }
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }
}
