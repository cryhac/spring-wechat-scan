package com.scan.VO;

import lombok.Data;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * Created by will on 2018/1/26.
 */
@Data
public class WebSocketMessageVO {
    private String messageType;
    private WxMpQrCodeTicket ticket;
    private String url;
    private String fd;
}
