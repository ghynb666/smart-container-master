package cn.fuguang.channel.pojo.dto;

import lombok.Data;

@Data
public class MwSendSmsResDTO {

    private Integer result;

    private String desc;

    private Long msgId;

    private String custId;
}
