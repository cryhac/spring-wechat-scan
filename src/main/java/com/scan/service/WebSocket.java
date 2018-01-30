package com.scan.service;

import com.scan.VO.WebSocketMessageVO;
import com.scan.config.ApplicationContextHolder;
import com.scan.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;


@Component
@ServerEndpoint("/webSocket")
@Slf4j
public class WebSocket {
    private Session session;

    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        log.info("【websocket消息】有新的连接, 总数:{}", webSocketSet.size());
        PushMessageService pushMessageService = ApplicationContextHolder.getContext().getBean(PushMessageService.class);
        try {
            WebSocketMessageVO webSocketMessageVO = new WebSocketMessageVO();
            int fd = Integer.parseInt(session.getId()) + 100;
            webSocketMessageVO.setUrl(pushMessageService.getQrCode(fd, 120));
            webSocketMessageVO.setTicket(pushMessageService.getWxMpQrCodeTicket());
            webSocketMessageVO.setMessageType("qrcode_url");
            this.session.getBasicRemote().sendText(JsonUtil.toJson(webSocketMessageVO));
            log.debug("返回数据result:{}", webSocketMessageVO);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        log.info("【websocket消息】连接断开, 总数:{}", webSocketSet.size());
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】收到客户端发来的消息:{}", message);
    }

    public void sendMessage(String message) {
        for (WebSocket webSocket : webSocketSet) {
            log.info("【websocket消息】广播消息, message={}", message);
            try {
                webSocket.session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送扫描成功
     *
     * @param message
     * @param id
     */
    public void sendScanSuccessMessage(String message, Integer id) {
        for (WebSocket webSocket : webSocketSet) {
            log.info("【websocket消息】单播消息, message={}", message);
            try {
                if (Integer.parseInt(webSocket.session.getId()) == id) {
                    webSocket.session.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
