package com.cao.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicInteger;

public class Client_A extends Client<AsynchronousSocketChannel>{
	
	@Override
	public AsynchronousSocketChannel connect() {
		try {
			//open and bind
			InetSocketAddress address = new InetSocketAddress("localhost",PORT_NUMBER);
			socket = AsynchronousSocketChannel.open();
			//set some options
			
			socket.setOption(StandardSocketOptions.SO_RCVBUF, 10 * 1024);
			socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			socket.connect(address, socket, new clientConnect()); //TODO
			
			return socket;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  null;
	}


	@Override
	protected void sendText(ByteBuffer writebuff) {
		socket.write(writebuff, socket, new CompletionHandler<Integer, AsynchronousSocketChannel>() {

			@Override
			public void completed(Integer result, AsynchronousSocketChannel attachment) {
				receiveMessage(attachment);
			}

			@Override
			public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
				System.out.println("client fail to send text to the server");
				
			}
		});	
		
	}
	
	@Override
	protected ByteBuffer receiveText(final AsynchronousSocketChannel socket,ByteBuffer readbuff) {
		
		socket.read(readbuff, readbuff, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				//System.out.println("AsynchronousClient:" + this + " already receive string lenth:" + result);
				if(attachment.hasRemaining()){
					//if readbuff has remaining, then read it again
					socket.read(attachment, attachment, this);
					//System.out.println("AsynchronousServer:receive rest of the string:");
				} else {
					receiveString=byteBufferToString(attachment);
					testNow(sendString,receiveString);
					if(messageNow.incrementAndGet()==MESSAGE_NUMBER){
						System.out.println(System.currentTimeMillis()-startTime);
						System.exit(0);	
					}else{
						//continue to send message
						sendString=sendMessage();
					}
					
//					try {
//						socket.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
				}	
			}
		
			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				System.out.println("client fail to receive text from the server");				
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}			
			}
		});		
		return readbuff;
    }
	
	public class clientConnect implements CompletionHandler<Void, AsynchronousSocketChannel>{

		@Override
		public void completed(Void result, AsynchronousSocketChannel attachment) {
			//System.out.println("AsynchronousClient:"+this+" success to connect");
			startTime = System.currentTimeMillis();   //startTime
			sendString=sendMessage();
			
//            for(int i=0;i<MESSAGE_NUMBER;i++){
//            	
//            	System.out.println("Message :"+i);
////            	new Thread(new Runnable() {
////					
////					@Override
////					public void run() {
////						sendString=sendMessage();
////						
////					}
////				}).start();
//            	//sendString=sendMessage();
//                //receiveString=receiveMessage(socket);
//                //testNow(sendString,receiveString);
//            }
		}

		@Override
		public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
			System.out.println("Client: fail to connect the server");
			
		}
		
	}
	
}
