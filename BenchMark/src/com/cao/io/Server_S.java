package com.cao.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server_S extends Server<ServerSocketChannel,SocketChannel> {
	@Override
	public void listen() {
		ExecutorService taskExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());  
		//open and bind
		InetSocketAddress address = new InetSocketAddress("localhost",PORT_NUMBER);
		try {			
			
			try {
				server=ServerSocketChannel.open();
			} catch (Exception e) {
				System.out.println("Cannot open the server");
				System.exit(0);
			}
			//set some options
			//server.configureBlocking(true); 			//set the blocking mode
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
			//System.out.println("SynchronousServer:waiting for connection..."); 
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
				SocketChannel socket=server.accept();
				//System.out.println("SynchronousServer:connect one client");				
				if(socket==null){
					break;
				}else{
					ServerWorker listener=new ServerWorker(socket);
					taskExecutor.execute(listener);
				}
			}
			  
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}


	@Override
	public void sendText(ByteBuffer writebuff, SocketChannel socket,int messageNow) {

		try {
			socket.write(writebuff);
			//System.out.println("SynchronousServer:already send: "+i);
			if(messageNow==MESSAGE_NUMBER){				
				socket.close();           //close the channel 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	

	@Override
	public ByteBuffer receiveText(ByteBuffer readbuff,SocketChannel socket,int messageNow) {
		try {

			socket.read(readbuff);
			//System.out.println("SynchronousServer:already receive: "+i);
			
			while(readbuff.hasRemaining()){
				//if readbuff has remaining, then read it again
				socket.read(readbuff);
				//System.out.println("SynchronousServer:receive rest of the string:"+j);
			}
		} catch (IOException e) {		
			e.printStackTrace();
		}	
		return readbuff;
	}

}
