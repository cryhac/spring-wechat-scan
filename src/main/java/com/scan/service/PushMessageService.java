package com.scan.service;

import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * 推送消息
 */
public interface PushMessageService {
    String getQrCode(int scene, Integer expire_seconds);

    WxMpQrCodeTicket getWxMpQrCodeTicket();
}
