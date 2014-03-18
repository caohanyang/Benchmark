package com.cao.io;

import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;
import java.util.Date;
import java.util.Random;

abstract public class Client<S extends NetworkChannel> {
	public static int CLIENT_NUMBER = Integer.getInteger("clientNumber", 100);
    public static int PORT_NUMBER = Integer.getInteger("portNumber", 10000);
    public static int BUFFER_SIZE = Integer.getInteger("buffSize", 100*1024);
    
    protected S socket;
    protected String sendString;
    protected String receiveString;
    public static int i=0;
    abstract public S connect();
    
    abstract protected void sendText(ByteBuffer writebuff);
    abstract protected ByteBuffer receiveText(S socket,ByteBuffer readbuff);

    public String sendMessage() {
        ByteBuffer writebuff = ByteBuffer.allocate(BUFFER_SIZE);

        sendString = generateString(BUFFER_SIZE);
        writebuff = ByteBuffer.wrap(sendString.getBytes());
        
        sendText(writebuff);
        return sendString;
    }
    
    public String receiveMessage(S socket){
    	ByteBuffer readbuff = ByteBuffer.allocate(BUFFER_SIZE);
    	readbuff=receiveText(socket,readbuff);
    	
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
            sendString=sendMessage();
            receiveString=receiveMessage(socket);
            testNow(sendString,receiveString);
        	
        }
    }
	public void testNow(String s1, String s2) {
//		if(s1.equals(s2)){
//			System.out.println("Task:success");
//		}else{
//			System.out.println("Task:failure");
//		}
		if(++i==CLIENT_NUMBER){
			Date endTime=new Date();
			long time=endTime.getTime()-Runner.startTime.getTime();
			System.out.println("End Time: "+endTime.getTime());
			System.out.println("time: "+time+" milliseconds");
		}	
	}
}
