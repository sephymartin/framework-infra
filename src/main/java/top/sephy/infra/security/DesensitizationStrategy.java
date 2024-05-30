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
