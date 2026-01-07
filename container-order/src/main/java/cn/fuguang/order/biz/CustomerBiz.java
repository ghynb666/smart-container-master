package cn.fuguang.order.biz;

import cn.fuguang.order.pojo.vo.req.AliAuthReq;
import cn.fuguang.order.pojo.vo.res.AliAuthRes;

public interface CustomerBiz {
    AliAuthRes aliAuth(AliAuthReq req);
}
