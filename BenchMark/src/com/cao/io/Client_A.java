package com.cao.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicInteger;

import com.cao.io.Client.endMode;
import com.cao.io.Client.startMode;

public class Client_A extends Client<AsynchronousSocketChannel>{
	public AtomicInteger messageNow=new AtomicInteger(0);
	@Override
	public AsynchronousSocketChannel connect() {
		try {
			//open and bind
			InetSocketAddress address = new InetSocketAddress("localhost",PORT_NUMBER);
			socket = AsynchronousSocketChannel.open();
			//set some options
			
			socket.setOption(StandardSocketOptions.SO_RCVBUF, 10 * 1024);
			socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			socket.connect(address, socket, new clientConnect()); 
			
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
				//each client start to send message
				receiveMessage(attachment,messageNow.incrementAndGet());
			}

			@Override
			public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
				System.out.println("client fail to send text to the server");
				
			}
		});	
		
	}
	
	@Override
	protected ByteBuffer receiveText(final AsynchronousSocketChannel socket,ByteBuffer readbuff,final int messageNow) {
		
		socket.read(readbuff, readbuff, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				//System.out.println("AsynchronousClient:" + this + " already receive string lenth:" + result);
				if(attachment.hasRemaining()){
					//if readbuff has remaining, then read it again
					socket.read(attachment, attachment, this);
					//System.out.println("AsynchronousServer:receive rest of the string:");
				} else {
					//don't have remaining
					receiveString=byteBufferToString(attachment);

					//if it is the last message of the channel
					if(messageNow==MESSAGE_NUMBER){
						//with assertions
						if(System.getProperty("endMode").equals(String.valueOf(endMode.WITH_ASSERTIONS))){
							//with the assertions
		                	testNow(sendString,receiveString,messageNow);  //check the value  
		                }else{
		                	//record the time
		                	System.out.println(System.currentTimeMillis()-startTime);							                	
		                }
						//last message ,close the channel
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						} 
						//test if it is the last client
						exit();
						
					}else{
						//if it is not the last message of the channel
						//with assertions
						if(System.getProperty("endMode").equals(String.valueOf(endMode.WITH_ASSERTIONS))){
							//with the assertions, need to test
		                	testNow(sendString,receiveString,messageNow);  //check the value  
		                }		
						//continue to send message
						sendString=sendMessage();
					}
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
			if(System.getProperty("startMode").equals(String.valueOf(startMode.AFTER_CONNECT))){
            	startTime = System.currentTimeMillis();   //startTime    
            }   
			sendString=sendMessage();
			//System.out.println("aynchronousclient:connect one server");
		}

		@Override
		public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
			System.out.println("Client: fail to connect the server");
			
		}
		
	}
	
}
