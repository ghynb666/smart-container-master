package cn.fuguang.device.controller;

import cn.fuguang.device.biz.DeviceInfoBiz;
import cn.fuguang.web.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("device")
@Slf4j
public class DeviceController {


    @Resource
    private DeviceInfoBiz deviceInfoBiz;

    /**
     * 检查设备在线状态
     */
    @PostMapping("/checkDeviceHeart")
    public BaseResult<Void> checkDeviceHeart(){
        log.info("开始检查设备心跳状态");
        try {
            deviceInfoBiz.checkDeviceHeart();
        } catch (Exception e) {
            log.error("开始检查设备心跳状态 系统异常" + e);
            return BaseResult.fail();
        }
        log.info("检查设备心跳状态完成");
        return BaseResult.success();
    }
}
