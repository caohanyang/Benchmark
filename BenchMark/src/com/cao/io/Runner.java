package com.cao.io;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.cao.io.Client.ClientWorker_C;
import com.cao.io.Client.ClientWorker_U;

public class Runner {
	public static int CLIENT_NUMBER = Integer.getInteger("clientNumber", 100);
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                newServer().listen();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //newClient().init();
                initClientPool();
            }
        }).start();
    }

    static Server<?,?> newServer() {
        if (Boolean.parseBoolean(System.getProperty("server.asynchronous")))
            return new Server_A();
        else
            return new Server_S();
    }
    
    static Client<?> newClient() {
        if (Boolean.parseBoolean(System.getProperty("client.asynchronous")))
            return new Client_A();
        else
            return new Client_S();
    }

  
    static Runnable continuation() {
        if (Boolean.parseBoolean(System.getProperty("client.asynchronous")))
            return newClient().new ClientWorker_C();
        else
            return newClient().new ClientWorker_U();
    }
    public static void initClientPool(){
   	 // create the clients pool
       ExecutorService clientPool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
       for (int i = 0; i < CLIENT_NUMBER; i++) {
       	Client<?> client=newClient();
       	Runnable connector=continuation();
	    clientPool.execute(connector);
       }

       try {
           clientPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
   }
}
