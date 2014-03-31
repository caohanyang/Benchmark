package com.cao.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.*;  
import static org.hamcrest.MatcherAssert.assertThat;

abstract public class Client<S extends NetworkChannel> {
	public static int CLIENT_NUMBER = Integer.parseInt(System.getProperty("clientNumber").trim());
    public static int PORT_NUMBER = Integer.getInteger("portNumber", 10000);
    public static int BUFFER_SIZE = Integer.parseInt(System.getProperty("buffSize").trim());
    public static int MESSAGE_NUMBER = Integer.parseInt(System.getProperty("messageNumber").trim());
    public long startTime;
    public static AtomicInteger clientNow=new AtomicInteger(0);
         
    protected S socket;
    protected String sendString;
    protected String receiveString;
    
    abstract public S connect();
    abstract protected void sendText(ByteBuffer writebuff);
    abstract protected ByteBuffer receiveText(S socket,ByteBuffer readbuff,int messageNow);

    public String sendMessage() {
        ByteBuffer writebuff = ByteBuffer.allocate(BUFFER_SIZE);
        
        sendString = generateString(BUFFER_SIZE);
        writebuff = ByteBuffer.wrap(sendString.getBytes());
        sendText(writebuff);

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
        	if(System.getProperty("startMode").equals(String.valueOf(startMode.BEFORE_CONNECT))){
            	startTime = System.currentTimeMillis();   //startTime    
            } 
            connect();	
        }
    }
    public class ClientWorker_U implements Runnable {
        @Override
        public void run() {
        	
        	if(System.getProperty("startMode").equals(String.valueOf(startMode.BEFORE_CONNECT))){
            	startTime = System.currentTimeMillis();   //startTime    
            } 
        	
            S socket = connect();

            for(int j=1;j<=MESSAGE_NUMBER;j++){
            	sendString=sendMessage();
                receiveString=receiveMessage(socket,j); 
                if(System.getProperty("endMode").equals(String.valueOf(endMode.WITH_ASSERTIONS))){
                	testNow(sendString,receiveString,j);  //check the value  
                }          
            }
            exit();
        }
    }
    @Test
	public void testNow(String s1, String s2,int messageNow) {
	   assertThat(s1, equalTo(s2));
	   if(messageNow==MESSAGE_NUMBER){
		   System.out.println(System.currentTimeMillis()-startTime);
	   }
	}
	
	public void exit(){
		if(clientNow.incrementAndGet()==CLIENT_NUMBER){
			//System.out.println("End");
			System.exit(0);	
		}	
	}
	public enum startMode{
		BEFORE_CONNECT,AFTER_CONNECT
	}
	public enum endMode{
		WITH_ASSERTIONS,WITHOUT_ASSERTIONS
	}
}
