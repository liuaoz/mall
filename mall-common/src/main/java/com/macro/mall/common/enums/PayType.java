package com.macro.mall.common.enums;

public enum PayType {

    ZHI_FU_BAO(1),
    WEI_XIN(2),
    ;
    private final int type;

    PayType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
