package tests;

import java.util.ArrayList;

import application.Global;
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

	public static void TestPercentageMsgLost (int totMessages){
		ArrayList<ArrayList<String>> logs = new ArrayList<ArrayList<String>>();
		logs = Utils.uploadLogs();

		int pLost = 0;

		for (ArrayList<String> log : logs){
			pLost = pLost + totMessages - log.size();
		}

		System.out.println("Percentage of msgs lost is " + ((double)pLost)/(totMessages* Global.N));

	}
}
