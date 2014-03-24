package com.cao.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LineCharts extends ApplicationFrame {
	/**
* 
*/
	private static final long serialVersionUID = 1L;

	public LineCharts(String s) {
		super(s);
		setContentPane(createDemoLine());
	}

	public static void main(String[] args) {
		LineCharts fjc = new LineCharts("Benchmark");
		fjc.pack();
		RefineryUtilities.centerFrameOnScreen(fjc);
		fjc.setVisible(true);
	}

	// generate the pane
	public static JPanel createDemoLine() {
		JFreeChart jfreechart = createChart(createDataset());
		return new ChartPanel(jfreechart);
	}

	// createJFreeChart
	public static JFreeChart createChart(DefaultCategoryDataset linedataset) {

		JFreeChart chart = ChartFactory.createLineChart("Benchmark", // Name
				"ClientNumber", // X name
				"Milliseconds", // Y name
				linedataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setRangeGridlinesVisible(true); // set grid
		plot.setBackgroundAlpha(0.3f); // set backgroundalpha
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);
		rangeAxis.setUpperMargin(0.20);
		rangeAxis.setLabelAngle(Math.PI / 2.0);
		return chart;
	}

	// generate data
	public static DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset linedataset = new DefaultCategoryDataset();
		// line name
		String series1 = "Synchronous";
		String series2 = "Asynchronous";
		String series3 = "Asynchronous Future";
		// x client number
		String sFile = "S.txt";
		String aFile = "A.txt";
		String fFile = "F.txt";

		int[] a={1,2,4,10,20,30,40,50,60,70,80,90,100};
		//get all the date
		List<Integer> slistTotal=readFile(sFile);
		List<Integer> alistTotal=readFile(aFile);
		List<Integer> flistTotal=readFile(fFile);
		
		//get the average value of each clientNumber
		List<Integer> slist=averageValue(slistTotal);
		List<Integer> alist=averageValue(alistTotal);
		List<Integer> flist=averageValue(flistTotal);
		for(int i=0;i<a.length;i++){
			int j= a[i];
			System.out.println(j);
			linedataset.addValue(slist.get(i), series1, String.valueOf(j));
			linedataset.addValue(alist.get(i), series2, String.valueOf(j));
			linedataset.addValue(flist.get(i), series3, String.valueOf(j));
		}
		
		
		return linedataset;
	}
	
	public static List<Integer> readFile(String fileName){
		List<Integer> data=new ArrayList<Integer>();
		File file=new File(fileName);
		String s = new String(); 
		if(file.exists()){
			try {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),"Unicode");
				BufferedReader reader=new BufferedReader(read);
				while((s=reader.readLine())!=null){
					int value=Integer.parseInt(s.trim());
					data.add(value);
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
	
	public static List<Integer> averageValue(List<Integer> listTotal){
		int testNumber=50;   //how many times we test for each clientNumber
		List<Integer> list=new ArrayList<Integer>();

		System.out.println(listTotal.size());
		for(int i=0;i<13;i++){
			//int value=(listTotal.get(i*testNumber+0)+listTotal.get(i*testNumber+1)+listTotal.get(i*testNumber+2))/testNumber;
			int sum=0;
			for(int j=0;j<testNumber;j++){
				sum+=listTotal.get(i*testNumber+j);
			}
			list.add(sum/testNumber);
		}
		return list;
	}
}