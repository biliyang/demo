package com.example.demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerationException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@ServerEndpoint(value="/websocket/fileup")
@Component
public class FileServer {
	/**
     * 记录所有上传文件属性
     */
    private static final Map<String,FileTargetInfo> fileTarge = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(FileServer.class);

    /**
     * 当websocket连接成功的时候就是准备上传文件的时候
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        //扩大一次性上传的最大数值
        session.setMaxBinaryMessageBufferSize(BreakpointUploadConfig.blobSize+8);
    }

    /**
     * 当链接关闭的时候判断是否是上传成功状态，并进行删除未上传完文件或者保存文件操作
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        System.out.println("链接关闭");
        String id = session.getId();
        FileTargetInfo fileTargetInfo = fileTarge.get(id);
        if(fileTargetInfo!=null){
            fileTargetInfo.fileUploadComplete();
            fileTarge.remove(id);
        }
    }

    /**
     * 接收要上传文件的属性，文件名称，大小，key值
     * @param message
     * @param session
     */
    @OnMessage
    public void onTextMessage(String message, Session session) {
        try {
        	JSONObject obj = new JSONObject(message);
			String fileName = obj.getString("fileName");
			long fileId = obj.getLong("fileId");
			long fileSize = obj.getLong("fileSize");
			String filetype = obj.getString("fileInfo");
			FileInfo fileInfo = new FileInfo(fileName, fileId, fileSize, filetype);
            FileTargetInfo fileTargetInfo = new FileTargetInfo(fileInfo,BreakpointUploadConfig.savePath);
            //替换一下文件存放位置，不然将使用BreakpointUploadConfig.savePath来存放
            fileTarge.put(session.getId(),fileTargetInfo);
            UploadCommand uc=new UploadCommand(fileTargetInfo);
            response(session,uc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始上传二进制文件
     * @param session
     * @param bb
     */
    @OnMessage
    public void onBinaryMessage(Session session,ByteBuffer bb) {
        String id = session.getId();
        logger.info("收到文件:" + id);
        FileTargetInfo fileTargetInfo = fileTarge.get(id);
        fileTargetInfo.saveByteBuffer(bb);
        UploadCommand uc=new UploadCommand(fileTargetInfo);
        if(uc.getCompletePercent()==1){
        	logger.info("完成上传:" + id);
            FileTargetInfo fileTarget = fileTarge.get(id);
            fileTarget.fileUploadComplete();
            fileTarge.remove(id);
        }
        response(session,uc);
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }


    private void response(Session session,Object obj) {
        try {
        	session.getBasicRemote().sendText(new JSONObject(obj).toString());
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}