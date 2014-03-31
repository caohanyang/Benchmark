package com.cao.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvWriter;

public class HandleFile {
	public static List<String> slistTotal;
	public static List<String> alistTotal;
	public static List<String> flistTotal;
	public static int testTimes=10;
	public static int removeTimes=3;
	public static int columnNumber=3;
	public static int clientNumber=10;
	
	public static void main(String[] args) {
//		String sFile = "0328/S0328.txt";
//		String aFile = "0328/A0328.txt";
//		String fFile = "0328/F0328.txt";
//		
//		slistTotal=readFile(sFile);
//		alistTotal=readFile(aFile);
//		flistTotal=readFile(fFile);
		
//		String sbFile="0328/SB0328.csv";
//		String saFile="0328/SA0328.csv";
//		String snFile="0328/SN0328.csv";
//		
//		String abFile="0328/AB0328.csv";
//		String aaFile="0328/AA0328.csv";
//		String anFile="0328/AN0328.csv";
//		
//		String fbFile="0328/FB0328.csv";
//		String faFile="0328/FA0328.csv";
//		String fnFile="0328/FN0328.csv";
//		
//		compareStartTime(sbFile,saFile,snFile,slistTotal);
//		compareStartTime(abFile,aaFile,anFile,alistTotal);
//		compareStartTime(fbFile,faFile,fnFile,flistTotal);
		
		String sFile = "0330/S0330.txt";
		String aFile = "0330/A0330.txt";
		String fFile = "0330/F0330.txt";
		
		slistTotal=readFile(sFile);
		alistTotal=readFile(aFile);
		flistTotal=readFile(fFile);
		
		String sSizeFile="0330/S0330.csv";
		String aSizeFile="0330/A0330.csv";
		String fSizeFile="0330/F0330.csv";
		
		compareMessageSize(sSizeFile,slistTotal);
		compareMessageSize(aSizeFile,alistTotal);
		compareMessageSize(fSizeFile,flistTotal);
	}
	
	public static List<String> readFile(String fileName){
		List<String> data=new ArrayList<String>();
		File file=new File(fileName);
		String s = new String(); 
		if(file.exists()){
			try {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),"Unicode");
				BufferedReader reader=new BufferedReader(read);
				while((s=reader.readLine())!=null){
					//int value=Integer.parseInt(s.trim());
					//System.out.println(value);
					data.add(s.trim());
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
            data=null;
			System.out.println("Can't find the data");
		}
		return data;
	}
	
	public static void compareMessageSize(String fileName,List<String> listTotal){ 
	    try {  
	          
	         CsvWriter wb =new CsvWriter(fileName,',',Charset.forName("SJIS"));   
	         String[] contents = {"1","10","100"}; 
	         wb.writeRecord(contents); 
	                 
//	         int times=removeTimes-1;
	         for(int i=removeTimes;i<testTimes;i++){
	        	 int sum1=0;
	        	 int sum2=0;
	        	 int sum3=0;
	        	 //remove the fist few numbers of each test
//                 if(i<removeTimes) continue;
//                 if(i>=testTimes&i<(testTimes+removeTimes)) continue;
//                 if(i>=(testTimes*2)&i<(testTimes*2+removeTimes)) continue;
	        	 
//	        	 if(i<testTimes){
//	        		 String[] bbb={listTotal.get(i*columnNumber),listTotal.get(i*columnNumber+1),listTotal.get(i*columnNumber+2),listTotal.get(i*columnNumber+3)};
//		        	 wb.writeRecord(bbb);
//	        	 }else if(i<testTimes*2){
//	        		 String[] aaa={listTotal.get(i*columnNumber),listTotal.get(i*columnNumber+1),listTotal.get(i*columnNumber+2),listTotal.get(i*columnNumber+3)};
//		        	 wa.writeRecord(aaa);
//	        	 }else{
//	        		 String[] nnn={listTotal.get(i*columnNumber),listTotal.get(i*columnNumber+1),listTotal.get(i*columnNumber+2),listTotal.get(i*columnNumber+3)};
//		        	 wn.writeRecord(nnn);
//	        	 }   
	        	 int stage=clientNumber*columnNumber;
	        	 for(int j=0;j<stage;j++){
	        		 if(j<clientNumber){
		        		 sum1=sum1+Integer.parseInt(listTotal.get(i*stage+j));
		        	 }else if(j<clientNumber*2){
		        		 sum2=sum2+Integer.parseInt(listTotal.get(i*stage+j));
		        	 }else{
		        		 sum3=sum3+Integer.parseInt(listTotal.get(i*stage+j));
		        	 }
	        	 }
	        	 
	        	 int median1=sum1/clientNumber;
	        	 int median2=sum2/clientNumber;
	        	 int median3=sum3/clientNumber;
	        	 String [] vvv={""+median1,""+median2,""+median3};
	        	 wb.writeRecord(vvv);
	        	 
	         }   
	             	 
	         wb.close();  
	         
	     } catch (IOException e) {  
	        e.printStackTrace();  
	     }  
	}
	
	public static void compareStartTime(String bfileName,String afileName,String nfileName,List<String> listTotal){ 
	    try {  
	          
	         CsvWriter wb =new CsvWriter(bfileName,',',Charset.forName("SJIS"));  
	         CsvWriter wa =new CsvWriter(afileName,',',Charset.forName("SJIS"));  
	         CsvWriter wn =new CsvWriter(nfileName,',',Charset.forName("SJIS"));  
	         String[] contents = {"1","10","100","1000"}; 
	         wb.writeRecord(contents); 
	         wa.writeRecord(contents); 
	         wn.writeRecord(contents); 

	         for(int i=0;i<listTotal.size()/columnNumber;i++){
	        	 //remove the fist few numbers of each test
                 if(i<removeTimes) continue;
                 if(i>=testTimes&i<(testTimes+removeTimes)) continue;
                 if(i>=(testTimes*2)&i<(testTimes*2+removeTimes)) continue;
	        	 
	        	 if(i<testTimes){
	        		 String[] bbb={listTotal.get(i*columnNumber),listTotal.get(i*columnNumber+1),listTotal.get(i*columnNumber+2),listTotal.get(i*columnNumber+3)};
		        	 wb.writeRecord(bbb);
	        	 }else if(i<testTimes*2){
	        		 String[] aaa={listTotal.get(i*columnNumber),listTotal.get(i*columnNumber+1),listTotal.get(i*columnNumber+2),listTotal.get(i*columnNumber+3)};
		        	 wa.writeRecord(aaa);
	        	 }else{
	        		 String[] nnn={listTotal.get(i*columnNumber),listTotal.get(i*columnNumber+1),listTotal.get(i*columnNumber+2),listTotal.get(i*columnNumber+3)};
		        	 wn.writeRecord(nnn);
	        	 }
	        	 
	         }
	         
	         wb.close();  
	         wa.close();
	         wn.close();
	     } catch (IOException e) {  
	        e.printStackTrace();  
	     }  
	}
	
}
