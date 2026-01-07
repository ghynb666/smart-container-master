package cn.fuguang.enums;

import lombok.Getter;

@Getter
public enum DeviceSupplierEnum {

    YINUO("益诺"),
    KENAI("可耐");
    private final String desc;

    DeviceSupplierEnum(String desc) {
        this.desc = desc;
    }

}
