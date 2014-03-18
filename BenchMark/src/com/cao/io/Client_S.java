package com.cao.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client_S extends Client<SocketChannel> {
	
    @Override
    public SocketChannel connect() {

        try {
            // open and bind
            InetAddress addr = InetAddress.getByName("localhost");
            socket = SocketChannel.open();
            // set some options
            socket.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
            socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            socket.connect(new InetSocketAddress(addr, PORT_NUMBER));
            //System.out.println("SynchronousClient:"+this+" success to connect");
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void sendText(ByteBuffer writebuff) {
        try {
            socket.write(writebuff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	protected ByteBuffer receiveText(SocketChannel socket,ByteBuffer readbuff) {
		
		try {
            socket.read(readbuff);
            //System.out.println("SynchronousClient:"+this+" already receive string lenth:" + i);
			while(readbuff.hasRemaining()){
				//if readbuff has remaining, then read it again
				socket.read(readbuff);
				//System.out.println("SynchronousClient:"+this+" receive rest of the string:" +j);
			}
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
		return readbuff;	
	}
	
}
