package com.cao.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.cao.io.Client.endMode;
import com.cao.io.Client.startMode;

public class Client_AF extends Client<AsynchronousSocketChannel> {

    @Override
    public AsynchronousSocketChannel connect() {
        try {
            // open and bind
        	InetSocketAddress address = new InetSocketAddress("localhost",PORT_NUMBER);
            try {
				socket = AsynchronousSocketChannel.open();
			} catch (Exception e) {
				System.out.println("Cannot open the socket");
				System.exit(0);
			}
            // set some options
            try {
            	socket.setOption(StandardSocketOptions.SO_RCVBUF, BUFFER_SIZE*MESSAGE_NUMBER);
			} catch (Exception e) {
				System.out.println("Cannot set the reveive buff");
				System.exit(0);
				e.printStackTrace();
			}
            try {
            	socket.setOption(StandardSocketOptions.SO_SNDBUF, BUFFER_SIZE*MESSAGE_NUMBER);
			} catch (Exception e) {
				System.out.println("Cannot set the send buff");
				System.exit(0);
				e.printStackTrace();
			}
            try {
				socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			} catch (Exception e) {
				System.out.println("Cannot set the reuse address");
				System.exit(0);
				e.printStackTrace();
			}
       
            try {
                socket.connect(address).get();
			} catch (Exception e) {
				System.out.println("Error in connect");
				System.exit(0);
				e.printStackTrace();
			}
           
            if(System.getProperty("startMode").equals(String.valueOf(startMode.AFTER_CONNECT))){
            	startTime = System.currentTimeMillis();   //startTime    
            }  
            //System.out.println("AsynchronousClient:"+this+" success to connect");
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	@Override
	protected void sendText(ByteBuffer writebuff) {
		try {
            socket.write(writebuff).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
		
	}
	
	@Override
	protected ByteBuffer receiveText(AsynchronousSocketChannel socket,ByteBuffer readbuff,int messageNow) {
		
		 try {	      
	            socket.read(readbuff).get();
	           // System.out.println("AsynchronousClient:"+this+" already receive string lenth:" + i);
	            while (readbuff.hasRemaining()) {
	                // if readbuff has remaining, then read it again
	                socket.read(readbuff).get();
	                //System.out.println("AsynchronousClient:"+this+" receive rest of the string:" +j);
	            }
	            
	            if(messageNow==MESSAGE_NUMBER){
					if(!System.getProperty("endMode").equals(String.valueOf(endMode.WITH_ASSERTIONS))){			
					    System.out.println(System.currentTimeMillis()-startTime);				 
		            }  

					socket.close();           //close the channel			
				}	
	        } catch (InterruptedException | ExecutionException | IOException  e) {
	            e.printStackTrace();
	        }
		return readbuff;
	}
	
}
