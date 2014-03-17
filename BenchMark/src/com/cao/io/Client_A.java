package com.cao.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client_A extends Client<AsynchronousSocketChannel>{

	@Override
	public AsynchronousSocketChannel connect() {
		try {
			//open and bind
			InetSocketAddress address = new InetSocketAddress("localhost",PORT_NUMBER);
			socket = AsynchronousSocketChannel.open();
			//set some options
			socket.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
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
				//receiveMsg(attachment);
				receiveString=receiveMessage(attachment);
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
	
	@Override
	public void test() {
		
	}
	
	public class clientConnect implements CompletionHandler<Void, AsynchronousSocketChannel>{

		@Override
		public void completed(Void result, AsynchronousSocketChannel attachment) {
			System.out.println("AsynchronousClient:"+this+" success to connect");
			sendString=sendMessage();
			//receiveMsg(attachment);
		}

		@Override
		public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
			System.out.println("Client: fail to connect the server");
			
		}
		
	}
	
}
