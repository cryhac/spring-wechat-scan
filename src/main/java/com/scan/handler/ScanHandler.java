package com.scan.handler;

import com.scan.VO.WebSocketMessageVO;
import com.scan.builder.TextBuilder;
import com.scan.config.ProjectUrlConfig;
import com.scan.constant.RedisConstant;
import com.scan.service.WebSocket;
import com.scan.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
@Slf4j
public class ScanHandler extends AbstractHandler {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    WebSocket webSocket;

    @Autowired
    ProjectUrlConfig projectUrlConfig;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        String content;
        try {
            //获取用户的信息
            String openID = wxMessage.getFromUser();
            Integer expire = RedisConstant.EXPIRE;
            String key = String.format(RedisConstant.SCAN_PREFIX, openID);
            String count = redisTemplate.opsForValue().get(key);

            if (StringUtils.isEmpty(count)) {
                redisTemplate.opsForValue().set(key, "1", expire, TimeUnit.SECONDS);
                content = openID + "登陆成功。这是你第1次登陆,玩的开心";
            } else {
                count = String.valueOf(Integer.parseInt(count) + 1);
                content = openID + "登陆成功。这是你第" + count + "次登陆,玩的开心";
                redisTemplate.opsForValue().set(key, count, expire, TimeUnit.SECONDS);
            }
            //登陆成功。发送webSocket。然后跳转重新渲染页面
            WebSocketMessageVO webSocketMessageVO = new WebSocketMessageVO();
            webSocketMessageVO.setMessageType("scan_success");
            String site = projectUrlConfig.getScan();
            webSocketMessageVO.setUrl(site + "/wechat.jpg");
            webSocketMessageVO.setFd(wxMessage.getTicket());
            webSocket.sendScanSuccessMessage(JsonUtil.toJson(webSocketMessageVO), Integer.parseInt(wxMessage.getEventKey()) - 100);
            log.debug("返回数据result:{}", webSocketMessageVO);
        } catch (RuntimeException e) {
            content = "消息异常";
            log.error(e.getMessage());
        }
        return new TextBuilder().build(content, wxMessage, weixinService);
    }
}
