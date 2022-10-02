package com.macro.mall.common.enums;

public enum DefaultStatus {

    DEFAULT(1),
    NO_DEFAULT(1);

    private final int status;

    DefaultStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
