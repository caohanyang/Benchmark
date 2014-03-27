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
		try {			
			InetAddress addr = InetAddress.getByName("localhost");
			server=ServerSocketChannel.open();
			//set some options
			//server.configureBlocking(true); 			//set the blocking mode
			server.setOption(StandardSocketOptions.SO_RCVBUF, 10 * 1024);
			server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			server.bind(new InetSocketAddress(addr,PORT_NUMBER));
			//System.out.println("SynchronousServer:waiting for connection...");  
			//SocketChannel socket=server.accept();
			while(true){
				SocketChannel socket=server.accept();
				//System.out.println("SynchronousServer:connect one client");				
				if(socket==null){
					break;
				}else{
					ServerWorker listener=new ServerWorker(socket);
					taskExecutor.execute(listener);
				}
//				ServerWorker listener=new ServerWorker(socket);
//				taskExecutor.execute(listener);
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
				//System.out.println("time:"+(System.currentTimeMillis()-startTime));
				//testNow(sendString, receiveString);
				socket.close();           //close the channel
				//System.exit(0);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	

	@Override
	public ByteBuffer receiveText(ByteBuffer readbuff,SocketChannel socket) {
		try {

			socket.read(readbuff);
			//System.out.println("SynchronousServer:already receive: "+i);
			//if(i==0) return null;       //if this is the end of the message
			
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
