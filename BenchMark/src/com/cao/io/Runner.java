package com.cao.io;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Runner {
	//public static int CLIENT_NUMBER = Integer.getInteger("clientNumber", 100);
	public static int CLIENT_NUMBER = Integer.parseInt(System.getProperty("clientNumber").trim());
	//public static Date startTime=new Date();
    public static void main(String[] args) {   	
		//System.out.println("number："+CLIENT_NUMBER);
        new Thread(new Runnable() {
            @Override
            public void run() {
                newServer().listen();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initClientPool();
            }
        }).start();
    }

    static Server<?,?> newServer() {
        if (Boolean.parseBoolean(System.getProperty("serverasynchronous")))
        	if(Boolean.parseBoolean(System.getProperty("serverfuture"))){
        		return new Server_AF();
        	}else{
        		return new Server_A();
        	}
            
        else
            return new Server_S();
    }
    
    static Client<?> newClient() {
        if (Boolean.parseBoolean(System.getProperty("clientasynchronous")))
        	if(Boolean.parseBoolean(System.getProperty("clientfuture"))){
        		return new Client_AF();
        	}else{
        		return new Client_A();
        	}
        
        else
            return new Client_S();
    }

  
    static Runnable continuation() {
        if (Boolean.parseBoolean(System.getProperty("clientasynchronous")))
        	if(Boolean.parseBoolean(System.getProperty("clientfuture"))){
        		 return newClient().new ClientWorker_U();
        	}else{
        		 return newClient().new ClientWorker_C();
        	}
        else
            return newClient().new ClientWorker_U();
    }
    public static void initClientPool(){
   	 // create the clients pool
       ExecutorService clientPool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
       for (int i = 0; i < CLIENT_NUMBER; i++) {
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
