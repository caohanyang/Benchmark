package com.cao.io;

import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;

abstract public class Server<S extends NetworkChannel,C extends NetworkChannel> {
	public static int PORT_NUMBER=Integer.getInteger("portNumber", 10000);
	public static int BUFFER_SIZE=Integer.getInteger("buffSize", 100*1024);
	protected S server;
	abstract public void listen();
	abstract public void sendText(ByteBuffer writebuff,C socket);
	abstract public ByteBuffer receiveText(ByteBuffer readbuff,C socket);
	
	public void sendMessage(ByteBuffer readbuff,C socket) {
        ByteBuffer writebuff = ByteBuffer.allocate(BUFFER_SIZE);

        writebuff=ByteBuffer.wrap(readbuff.array(),0,readbuff.position());
        
        sendText(writebuff,socket);

    }
    
    public ByteBuffer receiveMessage(C socket){
    	ByteBuffer readbuff = ByteBuffer.allocate(BUFFER_SIZE);

    	readbuff=receiveText(readbuff,socket);

        return readbuff;
    }
    
	public class ServerWorker implements Runnable{
		public C socket=null;
		public ServerWorker(C socket){
			this.socket=socket;
		}
		@Override
		public void run() {
			ByteBuffer readbuff=receiveMessage(socket);
			sendMessage(readbuff,socket);			
		}
		
	}
}
