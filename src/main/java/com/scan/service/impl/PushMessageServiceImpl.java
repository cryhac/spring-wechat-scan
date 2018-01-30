package com.scan.service.impl;

import com.scan.config.WechatAccountConfig;
import com.scan.service.PushMessageService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PushMessageServiceImpl implements PushMessageService {

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WechatAccountConfig accountConfig;

    private WxMpQrCodeTicket wxMpQrCodeTicket;


    @Override
    public String getQrCode(int scene, Integer expire_seconds) {

        try {
            WxMpQrCodeTicket ticket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(scene, expire_seconds);
            setWxMpQrCodeTicket(ticket);
            String qrCodePictureUrl = wxMpService.getQrcodeService().qrCodePictureUrl(ticket.getTicket());
            return qrCodePictureUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setWxMpQrCodeTicket(WxMpQrCodeTicket ticket) {
        this.wxMpQrCodeTicket = ticket;
    }

    public WxMpQrCodeTicket getWxMpQrCodeTicket() {
        return this.wxMpQrCodeTicket;
    }
}
