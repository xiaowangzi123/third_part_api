package com.unisound.iot;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import javax.websocket.*;
import java.io.File;
import java.io.IOException;

@ClientEndpoint
public class TtsClientEndpoint {
    private String fileName;
    private String text;
    private long start;
    public TtsClientEndpoint(String fileName,String text){
        this.fileName = fileName;
        this.text = text;
    }
    @OnOpen
    public void onOpen(Session session) {
        start = System.currentTimeMillis();
        System.out.println("->创建连接成功");

        final StringBuilder builder = new StringBuilder();
        //发送start请求
        JSONObject frame = new JSONObject();
        frame.put("format", "mp3");
        frame.put("sample", "16000");
        frame.put("vcn", "xiaofeng-base");
        frame.put("speed", 50);
        frame.put("volume", 50);
        frame.put("pitch", 50);
        frame.put("bright", 50);
        frame.put("text", text);
        frame.put("user_id", "unisound-home");

        try {
            session.getBasicRemote().sendText(frame.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnMessage
    public void onMessgae(Session session, byte[] data) throws IOException {

        System.out.println("====binary=====响应时间："+(System.currentTimeMillis() - start)+";返回字节大小=" +data.length);
        start = System.currentTimeMillis();
        File file = new File(fileName);
        FileUtils.writeByteArrayToFile(file,data,true);
    }
    @OnMessage
    public void processMessage(Session session, String data) {
        System.out.println("返回信息-->" + data);
    }

    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }
    @OnClose
    public void onClose(Session session) {
        System.out.println("->连接关闭");
    }
}

