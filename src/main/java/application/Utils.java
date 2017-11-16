package application;

import akka.actor.ActorRef;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import org.jfree.data.xy.XYSeries;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by gianluke on 29/08/17.
 */
public class Utils {

	//This method is responsible to select k node from a given node map
	//TODO: to implement checks on parameters (k <= nodeView)
	public static HashMap<Integer, ActorRef> getRandomNodes(HashMap<Integer,ActorRef> nodeView, int k){
		ArrayList<Integer> keyNodes = new ArrayList<Integer>(nodeView.keySet());
		Collections.shuffle(keyNodes);

		HashMap<Integer,ActorRef> selectedRandomNodes = new HashMap<Integer, ActorRef>();
		//System.out.println("size of view : "+nodeView.size()+ " -- K: "+k );

		for (int i = 0; i < k; i++) {
			int key = keyNodes.get(i);
			selectedRandomNodes.put(key, nodeView.get(key));
		}

		return selectedRandomNodes;
	}

	public static void createFile (String pathToFile){
		File f = new File(pathToFile);
		f.getParentFile().mkdirs();
		try {
			if (!f.exists()){
				f.createNewFile();
			}
			else{
				f.delete();
				f.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Wrapper to write into a file
	public static void writeOnAFile (String file, String content){
		PrintWriter out = null;
		try{
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			out = new PrintWriter(bw);

			out.println(content);
			out.flush();
		}
		catch (IOException e){
			//System.err.println("ERROR : An error occured while writing into file " + file);
			e.printStackTrace();
		}
		finally {
			out.close();
		}

	}

	//Method used to load all logs
	public static ArrayList<ArrayList<String>> uploadLogs (){

		String pathToRun = Global.pathToRun+(Global.runCounter - 1)+"/";
		File file = new File (pathToRun);
		String [] folders = file.list();
		ArrayList<ArrayList<String>> logs = new ArrayList<ArrayList<String>>();

		for (String folder : folders) {
			String pathLog = pathToRun + folder + "/Log.txt";
			ArrayList<String> log = new ArrayList<String>(); //one log

			try {
				BufferedReader br = new BufferedReader(new FileReader(pathLog));
				String line;
				while ((line = br.readLine())  != null){
					log.add(line);
				}

			} catch (IOException e){
				System.err.println("ERROR : An error occurred while reading file " + pathLog);
			}
			logs.add(log);
		}

		return logs;
	}

	//create graph from logs
	public static DefaultDirectedGraph createLogsGraph(ArrayList<ArrayList<String>> logs){
		DefaultDirectedGraph<String, DefaultEdge> logsGraph = new DefaultDirectedGraph<String,DefaultEdge>(DefaultEdge.class);

		for (ArrayList<String> log : logs){
			String pred = null;
			for (String item : log){
				if (!logsGraph.containsVertex(item)) {
					logsGraph.addVertex(item);
					System.out.println("\nADDING vertex "+ item);
					if (pred != null){
						logsGraph.addEdge(item, pred);
						System.out.println("\nCREATE edge from "+ item +" to "+ pred);
					}
				}
				else{
					//System.out.println("ITEM : "+ item + " PRED  " + pred);
					if ((pred != null) && (!logsGraph.containsEdge(item, pred))){
						logsGraph.addEdge(item, pred);
						System.out.println("\nCREATE edge from "+ item +" to "+ pred);
					}
				}
				pred = item;
			}
		}

		return logsGraph;
	}

	//check if the graph has not cycle (= satisfy total order)
	public static boolean checkTotalOrder(DirectedGraph logsGraph){
		TarjanSimpleCycles finderCycles = new TarjanSimpleCycles(logsGraph);
		System.out.println("***** CYCLE"+ finderCycles.findSimpleCycles().toString());
		return finderCycles.findSimpleCycles().isEmpty();
	}

	//get path to log for node
	public static String getPathToLog (int nodeId){
		return Global.pathToRun + Global.runCounter + Global.pathNode + nodeId + "/Log.txt";
	}

	//clean the environment
	public static void cleanEnvironment (){
		File output = new File(Global.pathOutPut);
		try {
			FileUtils.cleanDirectory(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//open a csv file if exists otherwise create it
	public static CSVWriter openCsv (String csvPath ){

		CSVWriter writerCsv = null;

		try {
			writerCsv = new CSVWriter(new FileWriter(csvPath,true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return writerCsv;
	}

	//fill series for the chart (by reading from csv file)
	public static XYSeries fillSeries (String pathToCsv){
		CSVReader readerCsv = null;
		XYSeries series = new XYSeries("First");

		try {
			readerCsv = new CSVReader(new FileReader(pathToCsv));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: An error occured while opening CSV " + pathToCsv);
		}

		String [] values;
		try {
			if (pathToCsv.equals(Global.pathToCsvGroupRunNodes) || pathToCsv.equals(Global.pathToCsvRun)){
				while ((values = readerCsv.readNext())!= null){
					series.add(Integer.parseInt(values[0]), (Double.parseDouble(values[3]))/
							Integer.parseInt(values[2]));
				}
			}
			else {
				while ((values = readerCsv.readNext())!= null){
					series.add(Integer.parseInt(values[1]), (Double.parseDouble(values[3]))/
							Integer.parseInt(values[2]));
				}

			}

		} catch (IOException e) {
			System.out.println("ERROR: An error occured while reading CSV " + pathToCsv);

		}
		finally {
			try {
				readerCsv.close();
			} catch (IOException e) {
				System.out.println("ERROR: An error occured while closing CSV " + pathToCsv);
			}
		}

		return series;

	}

	//method use to create a legend for a chart
	public static String [] createLegend (String ... items){
		String [] legendToDisplay = new String [items.length];

		for (int i = 0; i < items.length; i++){
			legendToDisplay[i] = items[i];
		}

		return legendToDisplay;

	}

	//method to calculate log base 2 of a given number
	public static double log2 (int n){
		return Math.log(n) / Math.log(2);
	}

	//method to delete a specific file (given a path)
	public static void deleteFile (String pathFile){
		File file = new File (pathFile);
		file.delete();
	}

	//function used by a process to retrieve its round duration (extension)
	public static int getRoundDuration (){
		Random random = new Random();

		int total = (int)(Global.RD * Global.D);
		int valueToAdd = random.nextInt(total+1);
		valueToAdd = valueToAdd - total/2;

		return (Global.RD + valueToAdd);

	}
}
