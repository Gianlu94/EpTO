package tests;

import java.util.ArrayList;
import application.Utils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

/**
 * Created by gianluke on 31/08/17.
 */
public class Tests {

	public static boolean TestTotalOrder (){
		ArrayList<ArrayList<String>> logs = new ArrayList<ArrayList<String>>();
		logs = Utils.uploadLogs();

		//System.out.println("***** " + logs.size() + "***** " + logs.get(0).size());
		DirectedGraph graph = Utils.createLogsGraph(logs);
		boolean result = Utils.checkTotalOrder(graph);
		System.out.println("\nRESULT : "+result);
		return result;
	}
}
