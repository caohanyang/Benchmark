package com.cao.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class Client_AF extends Client<AsynchronousSocketChannel> {

    @Override
    public AsynchronousSocketChannel connect() {
        try {
            // open and bind
            InetAddress addr = InetAddress.getByName("localhost");
            socket = AsynchronousSocketChannel.open();
            // set some options
            socket.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
            socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            socket.connect(new InetSocketAddress(addr, PORT_NUMBER)).get();
            startTime = System.currentTimeMillis();   //startTime
            //System.out.println("AsynchronousClient:"+this+" success to connect");
            return socket;
        } catch (IOException | InterruptedException | ExecutionException e) {
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
	protected ByteBuffer receiveText(AsynchronousSocketChannel socket,ByteBuffer readbuff) {
		
		 try {	      
	            socket.read(readbuff).get();
	           // System.out.println("AsynchronousClient:"+this+" already receive string lenth:" + i);
	            while (readbuff.hasRemaining()) {
	                // if readbuff has remaining, then read it again
	                socket.read(readbuff).get();
	                //System.out.println("AsynchronousClient:"+this+" receive rest of the string:" +j);
	            }
	            if(messageNow.incrementAndGet()==MESSAGE_NUMBER){
					System.out.println(System.currentTimeMillis()-startTime);
					System.exit(0);	
				}
	            //socket.close();
	        } catch (InterruptedException | ExecutionException e) {
	            e.printStackTrace();
	        }
		return readbuff;
	}
	
}
