package application;

import akka.actor.ActorRef;
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
		try{
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);

			out.println(content);
			out.flush();
		}
		catch (IOException e){
			System.err.println("ERROR : An error occured while writing into file " + file);
		}
	}

	//Method used to load all logs
	public static ArrayList<ArrayList<String>> uploadLogs (){

		File file = new File (Global.pathOutPut);
		String [] folders = file.list();
		ArrayList<ArrayList<String>> logs = new ArrayList<ArrayList<String>>();

		for (String folder : folders) {

			String pathLog = Global.pathOutPut + folder + "/Log.txt";
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
}
