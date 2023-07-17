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

public enum MonthEnum {

    JANUARY(1, "monthEnum.january"),

    FEBRUARY(2, "monthEnum.february"),

    MARCH(3, "monthEnum.march"),

    APRIL(4, "monthEnum.april"),

    MAY(5, "monthEnum.may"),

    JUNE(6, "monthEnum.june"),

    JULY(7, "monthEnum.july"),

    AUGUST(8, "monthEnum.august"),

    SEPTEMBER(9, "monthEnum.september"),

    OCTOBER(10, "monthEnum.october"),

    NOVEMBER(11, "monthEnum.november"),

    DECEMBER(12, "monthEnum.december");

    private final Integer value;
    private final String code;

    MonthEnum(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static MonthEnum fromInt(final Integer type) {

        MonthEnum monthEnum = null;
        switch (type) {
            case 1:
                monthEnum = MonthEnum.JANUARY;
            break;
            case 2:
                monthEnum = MonthEnum.FEBRUARY;
            break;
            case 3:
                monthEnum = MonthEnum.MARCH;
            break;
            case 4:
                monthEnum = MonthEnum.APRIL;
            break;
            case 5:
                monthEnum = MonthEnum.MAY;
            break;
            case 6:
                monthEnum = MonthEnum.JUNE;
            break;
            case 7:
                monthEnum = MonthEnum.JULY;
            break;
            case 8:
                monthEnum = MonthEnum.AUGUST;
            break;
            case 9:
                monthEnum = MonthEnum.SEPTEMBER;
            break;
            case 10:
                monthEnum = MonthEnum.OCTOBER;
            break;
            case 11:
                monthEnum = MonthEnum.NOVEMBER;
            break;
            case 12:
                monthEnum = MonthEnum.DECEMBER;
            break;
        }
        return monthEnum;
    }
}
