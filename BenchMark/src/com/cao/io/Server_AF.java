package com.cao.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
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
			System.out.println("AsynchronousServer:waiting for connection...");  
			while(true){
				
				try {
					Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture=server.accept();
					//AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get();
					final AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get(); //TODO
					System.out.println("AsynchronousServer:connect one client");
					ServerWorker listener=new ServerWorker(asynchronousSocketChannel);
					taskExecutor.execute(listener);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				} 
				
			}
			  
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void sendText(ByteBuffer writebuff, AsynchronousSocketChannel socket) {
		try {
			int i = socket.write(writebuff).get();
			System.out.println("AsynchronousServer:already send: "+i);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}finally{
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	@Override
	public ByteBuffer receiveText(ByteBuffer readbuff,AsynchronousSocketChannel socket) {
		try {
			int i = socket.read(readbuff).get();
			System.out.println("AsynchronousServer:already receive: "+i);
			while(readbuff.hasRemaining()){
				//if readbuff has remaining, then read it again
				int j= socket.read(readbuff).get();
				System.out.println("AsynchronousServer:receive rest of the string:"+j);
			}
		} catch (InterruptedException | ExecutionException e) {		
			e.printStackTrace();
		}	
		return readbuff;
	}
	
	@Override
	public void test() {
		
	}
}
