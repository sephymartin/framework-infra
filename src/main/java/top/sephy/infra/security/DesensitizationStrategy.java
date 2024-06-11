/*
 * Copyright 2022-2024 sephy.top
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.sephy.infra.security;

import top.sephy.infra.utils.DesensitizationUtils;

public enum DesensitizationStrategy implements Desensitization {

    /**
     * 脱敏策略
     */
    CHINESE_NAME {
        @Override
        public String desensitize(String target) {
            return DesensitizationUtils.chineseName(target);
        }
    },
    ID_CARD {
        @Override
        public String desensitize(String target) {
            return DesensitizationUtils.idCardNum(target);
        }
    },
    FIXED_PHONE {
        @Override
        public String desensitize(String target) {
            return DesensitizationUtils.fixedPhone(target);
        }
    },
    MOBILE_PHONE {
        @Override
        public String desensitize(String target) {
            return DesensitizationUtils.mobilePhone(target);
        }
    },
    ADDRESS {
        @Override
        public String desensitize(String target) {
            return DesensitizationUtils.address(target, 4);
        }
    },
    EMAIL {
        @Override
        public String desensitize(String target) {
            return DesensitizationUtils.email(target);
        }
    },
    BANK_CARD {
        @Override
        public String desensitize(String target) {
            return DesensitizationUtils.bankCard(target);
        }
    },
    PASSWORD {
        @Override
        public String desensitize(String target) {
            return DesensitizationUtils.password(target);
        }
    },
    CNAPS_CODE {
        @Override
        public String desensitize(String target) {
            return DesensitizationUtils.cnapsCode(target);
        }
    },
    CREDIT_CODE {
        @Override
        public String desensitize(String target) {
            return DesensitizationUtils.creditCode(target);
        }
    },;

}
