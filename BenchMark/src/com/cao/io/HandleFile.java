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
	public static String DATE_TIME = System.getProperty("dateTime").trim();
	public static List<String> slistTotal;
	public static List<String> alistTotal;
	public static List<String> flistTotal;
	public static int TEST_TIMES = Integer.parseInt(System.getProperty("testTimes").trim());
	public static int REMOVE_TIMES = Integer.parseInt(System.getProperty("removeTimes").trim());
	public static int CLIENT_NUMBER = Integer.parseInt(System.getProperty("clientNumber").trim());
	
	public static int modeNumber=4;
	//public static int clientNumber=1;
	
	public static void main(String[] args) {
		testStartTime();
		
		//testMessageSize();
		
		
	}
	
	private static void testStartTime() {
//		String s="C:/Users/CAO HANYANG/git/BenchMark/BenchMark/";
		String s="/home/cao/UB1/BenchMark/";
		String sFile = s+DATE_TIME+"/S"+DATE_TIME+".txt";
		String aFile = s+DATE_TIME+"/A"+DATE_TIME+".txt";
		String fFile = s+DATE_TIME+"/F"+DATE_TIME+".txt";
		
		slistTotal=readFile(sFile);
		alistTotal=readFile(aFile);
		flistTotal=readFile(fFile);
		//System.out.println(slistTotal);
		
		String sbwFile=s+DATE_TIME+"/SBW"+DATE_TIME+".csv";
		String sawFile=s+DATE_TIME+"/SAW"+DATE_TIME+".csv";
		String sbnFile=s+DATE_TIME+"/SBN"+DATE_TIME+".csv";
		String sanFile=s+DATE_TIME+"/SAN"+DATE_TIME+".csv";
	
		String abwFile=s+DATE_TIME+"/ABW"+DATE_TIME+".csv";
		String aawFile=s+DATE_TIME+"/AAW"+DATE_TIME+".csv";
		String abnFile=s+DATE_TIME+"/ABN"+DATE_TIME+".csv";
		String aanFile=s+DATE_TIME+"/AAN"+DATE_TIME+".csv";
		
		String fbwFile=s+DATE_TIME+"/FBW"+DATE_TIME+".csv";
		String fawFile=s+DATE_TIME+"/FAW"+DATE_TIME+".csv";
		String fbnFile=s+DATE_TIME+"/FBN"+DATE_TIME+".csv";
		String fanFile=s+DATE_TIME+"/FAN"+DATE_TIME+".csv";
		
		compareStartTime(sbwFile,sawFile,sbnFile,sanFile,slistTotal);
		compareStartTime(abwFile,aawFile,abnFile,aanFile,alistTotal);
		compareStartTime(fbwFile,fawFile,fbnFile,fanFile,flistTotal);
		
	}

	private static void testMessageSize() {
		String sFile = "0331/S0331.txt";
		String aFile = "0331/A0331.txt";
		String fFile = "0331/F0331.txt";
		
		slistTotal=readFile(sFile);
		alistTotal=readFile(aFile);
		flistTotal=readFile(fFile);
		
		String sSizeFile="0331/S0331.csv";
		String aSizeFile="0331/A0331.csv";
		String fSizeFile="0331/F0331.csv";
		
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
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf8");
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
	         String[] contents = {"1","10","100","1000"}; 
	         wb.writeRecord(contents); 
	                 
//	         int times=removeTimes-1;
	         for(int i=REMOVE_TIMES;i<TEST_TIMES;i++){
	        	 int sum1=0;
	        	 int sum2=0;
	        	 int sum3=0;
	        	 int sum4=0;
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
	        	 int stage=CLIENT_NUMBER*modeNumber;
	        	 for(int j=0;j<stage;j++){
	        		 if(j<CLIENT_NUMBER){
		        		 sum1=sum1+Integer.parseInt(listTotal.get(i*stage+j));
		        	 }else if(j<CLIENT_NUMBER*2){
		        		 sum2=sum2+Integer.parseInt(listTotal.get(i*stage+j));
		        	 }else if(j<CLIENT_NUMBER*3){
		        		 sum3=sum3+Integer.parseInt(listTotal.get(i*stage+j));
		        	 }else{
		        		 sum4=sum4+Integer.parseInt(listTotal.get(i*stage+j));
		        	 }
	        	 }
	        	 
	        	 int median1=sum1/CLIENT_NUMBER;
	        	 int median2=sum2/CLIENT_NUMBER;
	        	 int median3=sum3/CLIENT_NUMBER;
	        	 int median4=sum4/CLIENT_NUMBER;
	        	 String [] vvv={""+median1,""+median2,""+median3,""+median4};
	        	 wb.writeRecord(vvv);
	        	 
	         }   
	             	 
	         wb.close();  
	         
	     } catch (IOException e) {  
	        e.printStackTrace();  
	     }  
	}
	
	public static void compareStartTime(String bwfileName,String awfileName,String bnfileName,String anfileName,List<String> listTotal){ 
	    try {  
	    	 File file1=new File(bwfileName);
	    	 File file2=new File(awfileName);
	    	 File file3=new File(bnfileName);
	    	 File file4=new File(anfileName);
	    	 file1.createNewFile();
	    	 file2.createNewFile();
	    	 file3.createNewFile();
	    	 file4.createNewFile();
	    	 //System.out.println(file1.getAbsolutePath());
	    	 //System.out.println("111");
	         CsvWriter bw =new CsvWriter(bwfileName,',',Charset.forName("SJIS"));  
	         CsvWriter aw =new CsvWriter(awfileName,',',Charset.forName("SJIS"));  
	         CsvWriter bn =new CsvWriter(bnfileName,',',Charset.forName("SJIS")); 
	         CsvWriter an =new CsvWriter(anfileName,',',Charset.forName("SJIS")); 
	         String[] contents = {"1","10","100","1000"}; 
	         bw.writeRecord(contents); 
	         aw.writeRecord(contents); 
	         bn.writeRecord(contents); 
	         an.writeRecord(contents);
	         
	         for(int i=0;i<listTotal.size()/modeNumber;i++){
	        	 //remove the fist few numbers of each test
                 if(i<REMOVE_TIMES) continue;
                 if(i>=TEST_TIMES&i<(TEST_TIMES+REMOVE_TIMES)) continue;
                 if(i>=(TEST_TIMES*2)&i<(TEST_TIMES*2+REMOVE_TIMES)) continue;
                 if(i>=(TEST_TIMES*3)&i<(TEST_TIMES*3+REMOVE_TIMES)) continue;
	        	 if(i<TEST_TIMES){
	        		 String[] bbb={listTotal.get(i*modeNumber),listTotal.get(i*modeNumber+1),listTotal.get(i*modeNumber+2),listTotal.get(i*modeNumber+3)};
	        		 bw.writeRecord(bbb);
	        	 }else if(i<TEST_TIMES*2){
	        		 String[] aaa={listTotal.get(i*modeNumber),listTotal.get(i*modeNumber+1),listTotal.get(i*modeNumber+2),listTotal.get(i*modeNumber+3)};
	        		 System.out.println(aaa);
	        		 aw.writeRecord(aaa);
	        	 }else if(i<TEST_TIMES*3){
	        		 String[] nnn={listTotal.get(i*modeNumber),listTotal.get(i*modeNumber+1),listTotal.get(i*modeNumber+2),listTotal.get(i*modeNumber+3)};
	        		 System.out.println(nnn);
	        		 bn.writeRecord(nnn);
	        	 }else {
	        		 String[] kkk={listTotal.get(i*modeNumber),listTotal.get(i*modeNumber+1),listTotal.get(i*modeNumber+2),listTotal.get(i*modeNumber+3)};
	        		 System.out.println(kkk);
	        		 an.writeRecord(kkk);
	        	 }
	        	 
	         }
	         
	         bw.close();  
	         aw.close();
	         bn.close();
	         an.close();
	     } catch (IOException e) {  
	        e.printStackTrace();  
	     }  
	}
	
}
