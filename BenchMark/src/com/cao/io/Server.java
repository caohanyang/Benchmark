package com.cao.io;

import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;

abstract public class Server<S extends NetworkChannel,C extends NetworkChannel> {
	public static int PORT_NUMBER=Integer.getInteger("portNumber", 10000);
	public static int BUFFER_SIZE = Integer.parseInt(System.getProperty("buffSize").trim());
	public static int MESSAGE_NUMBER = Integer.parseInt(System.getProperty("messageNumber").trim());
	protected S server;
	abstract public void listen();
	abstract public void sendText(ByteBuffer writebuff,C socket,int messageNow);
	abstract public ByteBuffer receiveText(ByteBuffer readbuff,C socket);
	
	public void sendMessage(ByteBuffer readbuff,C socket,int messageNow) {
        ByteBuffer writebuff = ByteBuffer.allocate(BUFFER_SIZE);

        writebuff=ByteBuffer.wrap(readbuff.array(),0,readbuff.position());
        
        sendText(writebuff,socket,messageNow);

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
			
			for(int i=1;i<=MESSAGE_NUMBER;i++){
				ByteBuffer readbuff=receiveMessage(socket);
				if(readbuff==null){				
					 break;
				}else{
					sendMessage(readbuff,socket,i);	
				}
			}
		}	
	}
}
