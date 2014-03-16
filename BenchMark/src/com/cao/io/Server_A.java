package com.cao.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
	 	 System.out.println("AsynchronousServer:waiting for connection...");
		 
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
				System.out.println("AsynchronousServer:already send: "+result);
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
				System.out.println("AsynchronousServer:already receive: "+result);
				if(attachment.hasRemaining()){
					//if readbuff has remaining, then read it again
					while(attachment.hasRemaining()){
						//java.nio.channels.ReadPendingException
						socket.read(readbuff,readbuff,this);
						System.out.println("AsynchronousServer:receive rest of the string:");
						
						//The solution use get() is ok
//						try {
//							int j=socket.read(readbuff).get();
//							System.out.println("AsynchronousServer:receive rest of the string:"+j);
//						} catch (InterruptedException | ExecutionException e) {
//							e.printStackTrace();
//						}
					}
					
				}else{
					sendMessage(readbuff,socket);
				}	
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				System.out.println("server fail to receive text the client");	
				
			}
		});
	    
		//socket.read(readbuff, readbuff,new ServerReceive(socket));
		return readbuff;
	}
	

	@Override
	public void test() {

	}

	public class ServerAccept implements CompletionHandler<AsynchronousSocketChannel,Object>{

		@Override
		public void completed(AsynchronousSocketChannel result, Object attachment) {
			// get the reslut and invoke next thread.
			System.out.println("AsynchronousServer:connect one client");
			server.accept(null, this);
			//do read and write
			//ByteBuffer readbuff=receiveMsg(result);		
			ByteBuffer readbuff=receiveMessage(result);
		}

		@Override
		public void failed(Throwable exc, Object attachment) {
			System.out.println("server fail to connect the client");		
		}	
	}
	
//	public class ServerReceive implements CompletionHandler<Integer, ByteBuffer>{
//		public AsynchronousSocketChannel socket=null;
//		public ServerReceive(AsynchronousSocketChannel socket){
//			this.socket=socket;
//		}
//		@Override
//		public void completed(Integer result, ByteBuffer attachment) {
//			System.out.println("AsynchronousServer:already receive: "+result);
//			while(attachment.hasRemaining()){
//				//if readbuff has remaining, then read it again
//				socket.read(attachment,attachment,new ServerReceive(socket));
//				System.out.println("AsynchronousServer:receive rest of the string:");
//			}
//			sendMessage(attachment,socket);
//		}
//
//		@Override
//		public void failed(Throwable exc, ByteBuffer attachment) {
//			System.out.println("server fail to receive text the client");	
//			
//		}
//		
//	}
}
