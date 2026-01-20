package cn.fuguang.order.biz;

import cn.fuguang.api.order.dto.req.AgreementSignCallBackReqDTO;
import cn.fuguang.api.order.dto.req.PayCompleteReqDTO;
import cn.fuguang.order.pojo.vo.req.ScanCreateOrderReq;
import cn.fuguang.order.pojo.vo.res.ScanCreateOrderRes;

public interface OrderBiz {
    /**
     * 用户扫码下单
     */
    ScanCreateOrderRes scanCreateOrder(ScanCreateOrderReq req);

    /**
     * 接收channel服务预授权回调处理
     */
    void agreementSignCallBack(AgreementSignCallBackReqDTO reqDTO);
    
    /**
     * 处理支付完成
     */
    void handlePayComplete(PayCompleteReqDTO reqDTO);
}
