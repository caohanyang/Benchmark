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
		try {			
			InetAddress addr = InetAddress.getByName("localhost");
			server=AsynchronousServerSocketChannel.open();
			//set some options
			server.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
			server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			server.bind(new InetSocketAddress(addr,PORT_NUMBER));
			//System.out.println("AsynchronousServer:waiting for connection...");  
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
		} catch (IOException e) {
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
