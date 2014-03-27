package com.cao.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

abstract public class Client<S extends NetworkChannel> {
	public static int CLIENT_NUMBER = Integer.parseInt(System.getProperty("clientNumber").trim());
    public static int PORT_NUMBER = Integer.getInteger("portNumber", 10000);
    public static int BUFFER_SIZE = Integer.getInteger("buffSize", 10*1024);
    public static int MESSAGE_NUMBER = Integer.parseInt(System.getProperty("messageNumber").trim());
    public long startTime;
    public static AtomicInteger i=new AtomicInteger(0);
    //ublic static AtomicInteger messageNow=new AtomicInteger(0);
       
    protected S socket;
    protected String sendString;
    protected String receiveString;
    
    abstract public S connect();
    abstract protected void sendText(ByteBuffer writebuff);
    abstract protected ByteBuffer receiveText(S socket,ByteBuffer readbuff,int messageNow);

    public String sendMessage() {
        ByteBuffer writebuff = ByteBuffer.allocate(BUFFER_SIZE);
        
        //startTime = System.currentTimeMillis();   //startTime
        sendString = generateString(BUFFER_SIZE);
        writebuff = ByteBuffer.wrap(sendString.getBytes());
        sendText(writebuff);
//        startTime = System.currentTimeMillis();   //startTime
        return sendString;
    }
    
    public String receiveMessage(S socket,int messageNow){
    	ByteBuffer readbuff = ByteBuffer.allocate(BUFFER_SIZE);
    	readbuff=receiveText(socket,readbuff,messageNow);
    	
		return byteBufferToString(readbuff);
    }
    
    public String generateString(int length) {
        // generate from this string
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        //System.out.println("generate string lenth:" + sb.length());
        return sb.toString();
    }
    
    public String byteBufferToString(ByteBuffer buffer){
    	String str=new String(buffer.array());
		return str;

    }
    public class ClientWorker_C implements Runnable {
        @Override
        public void run() {
            connect();
        	
        }
    }
    public class ClientWorker_U implements Runnable {
        @Override
        public void run() {
            S socket = connect();
           // AtomicInteger messageNow=new AtomicInteger(0);
            //startTime = System.currentTimeMillis();   //startTime
            //System.out.println("startTime:"+startTime);
            for(int i=1;i<=MESSAGE_NUMBER;i++){
            	//System.out.println("Message :"+i);
            	sendString=sendMessage();
                receiveString=receiveMessage(socket,i);
                //testNow(sendString,receiveString);
            }
        	
        }
    }
	public void testNow(String s1, String s2) {
		if(i.incrementAndGet()==CLIENT_NUMBER){
//			Date endTime=new Date();
			//long time=endTime.getTime()-Runner.startTime.getTime();
			System.out.println("End");
		    //exit
			System.exit(0);	
		}	
	}
}
