package com.cao.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server_A extends Server<AsynchronousServerSocketChannel,AsynchronousSocketChannel>{
	
	@Override
	public void listen() {
		    	
	 try {		
	     //use ChannelGroup
	     AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 10);
		 //open and bind
		 InetSocketAddress address = new InetSocketAddress("localhost",PORT_NUMBER);
		 server = AsynchronousServerSocketChannel.open(group).bind(address);  
		 server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
	 	 server.setOption(StandardSocketOptions.SO_RCVBUF, BUFFER_SIZE);
	 	 //System.out.println("AsynchronousServer:waiting for connection...");
		 
         //accept the socket
		 server.accept(null, new ServerAccept());
		  
		// Awaits termination of the group.
		group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
	  }	
	}

	@Override
	public void sendText(ByteBuffer writebuff, final AsynchronousSocketChannel socket) {
		//write
		socket.write(writebuff, writebuff, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				//System.out.println("AsynchronousServer:already send: "+result);
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				System.out.println("server fail to send text to the client");
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	
	@Override
	public ByteBuffer receiveText(final ByteBuffer readbuff,final AsynchronousSocketChannel socket) {
		
	    	socket.read(readbuff, readbuff, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				//System.out.println("AsynchronousServer:"+this+" already receive: "+result);
				if(attachment.hasRemaining()){			
					//if readbuff has remaining, then read it again
					socket.read(readbuff,readbuff,this);				
				}else{
					sendMessage(readbuff,socket);
				}	
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				System.out.println("server fail to receive text the client");	
				
			}
		});
	    
		return readbuff;
	}
	
	public class ServerAccept implements CompletionHandler<AsynchronousSocketChannel,Object>{

		@Override
		public void completed(AsynchronousSocketChannel result, Object attachment) {
			// get the reslut and invoke next thread.
			//System.out.println("AsynchronousServer:connect one client");
			server.accept(null, this);
			//do read and write	
			receiveMessage(result);
		}

		@Override
		public void failed(Throwable exc, Object attachment) {
			System.out.println("server fail to connect the client");		
		}	
	}
	
}
