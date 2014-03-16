package com.cao.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
            System.out.println("AsynchronousClient:"+this+" success to connect");
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
	            int i = socket.read(readbuff).get();
	            System.out.println("AsynchronousClient:"+this+" already receive string lenth:" + i);
	            while (readbuff.hasRemaining()) {
	                // if readbuff has remaining, then read it again
	                int j = socket.read(readbuff).get();
	                System.out.println("AsynchronousClient:"+this+" receive rest of the string:" +j);
	            }
	        } catch (InterruptedException | ExecutionException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                socket.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		return readbuff;
	}
	
    @Override
    public void test() {
        
    }

}
