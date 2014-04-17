package com.cao.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server_AF extends Server<AsynchronousServerSocketChannel,AsynchronousSocketChannel>{
	@Override
	public void listen() {
		ExecutorService taskExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());  
		//open and bind
		InetSocketAddress address = new InetSocketAddress("localhost",PORT_NUMBER);
		try {			
			
			try {
				server=AsynchronousServerSocketChannel.open();
			} catch (Exception e) {
				System.out.println("Cannot open the server");
				System.exit(0);
			}
			//set some options
			try {
				server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			} catch (Exception e) {
				System.out.println("Cannot set the reuse address");
				System.exit(0);
			}
			try {
				server.setOption(StandardSocketOptions.SO_RCVBUF, BUFFER_SIZE*MESSAGE_NUMBER*CLIENT_NUMBER);
			} catch (Exception e) {
				System.out.println("Cannot set the reveive buff");
				System.exit(0);
			}	
			try {
				server.bind(address,1000); //backlog=1000
			} catch (Exception e) {
				System.out.println("Cannot bind the address");
				System.exit(0);
			}
			
			//System.out.println("AsynchronousServer:waiting for connection...");  
			if(server.isOpen()){
		        new Thread(new Runnable() {
		            @Override
		            public void run() {
		                Runner.initClientPool();
		            }
		        }).start();
		        //Runner.initClientPool();
			}
			while(true){			
				try {
					Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture=server.accept();
					final AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get();
					//System.out.println("AsynchronousServer:connect one client");
					if(asynchronousSocketChannel==null){
						break;
					}else{
						ServerWorker listener=new ServerWorker(asynchronousSocketChannel);
						taskExecutor.execute(listener);
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				} 				
			}		  
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void sendText(ByteBuffer writebuff, AsynchronousSocketChannel socket,int messageNow) {
		try {
			socket.write(writebuff).get();
			//System.out.println("AsynchronousServer:already send: "+i);
            if(messageNow==MESSAGE_NUMBER){
				socket.close();           //close the channel //TODO
			}
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
		}	
	}
	
	@Override
	public ByteBuffer receiveText(ByteBuffer readbuff,AsynchronousSocketChannel socket,int messageNow) {
		try {
			socket.read(readbuff).get();
			//System.out.println("AsynchronousServer:already receive: "+i);
			while(readbuff.hasRemaining()){
				//if readbuff has remaining, then read it again
				socket.read(readbuff).get();
				//System.out.println("AsynchronousServer:receive rest of the string:"+j);
			}
		} catch (InterruptedException | ExecutionException e) {		
			e.printStackTrace();
		}	
		return readbuff;
	}
	
}
