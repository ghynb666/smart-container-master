package cn.fuguang.order.controller;

import cn.fuguang.order.biz.OrderBiz;
import cn.fuguang.order.pojo.vo.req.AliAuthReq;
import cn.fuguang.order.pojo.vo.req.ScanCreateOrderReq;
import cn.fuguang.order.pojo.vo.res.ScanCreateOrderRes;
import cn.fuguang.web.BaseResult;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {

    @Resource
    private OrderBiz orderBiz;

    /**
     * 用户扫码下单
     */
    @PostMapping("/scanResultWithConfirm")
    public BaseResult<ScanCreateOrderRes> scanCreateOrder(@RequestBody @Valid ScanCreateOrderReq req){
        log.info("用户扫码下单 req:{}", JSONObject.toJSONString(req));
        ScanCreateOrderRes res;
        try {
            res = orderBiz.scanCreateOrder(req);
        } catch (Exception e) {
            log.error("用户扫码下单 系统异常" + e);
            return BaseResult.fail("系统异常, 请稍后重试");
        }
        log.info("用户扫码下单结果 res:{}", JSONObject.toJSONString(res));
        return BaseResult.success(res);
    }


}
