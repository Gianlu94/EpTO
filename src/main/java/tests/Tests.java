package tests;

import java.io.IOException;
import java.util.ArrayList;

import application.Global;
import application.Utils;
import com.opencsv.CSVWriter;
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

	public static void TestPercentageMsgLost (int totMessages, int typeOfRun){
		ArrayList<ArrayList<String>> logs = new ArrayList<ArrayList<String>>();
		logs = Utils.uploadLogs();

		int pLost = 0;

		for (ArrayList<String> log : logs){
			pLost = pLost + totMessages - log.size();
		}

		System.out.println("Percentage of msgs lost is " + ((double)pLost)/(totMessages* Global.N));


		CSVWriter writerCsv = null;
		if (typeOfRun == 0){ //single Run
			writerCsv = Utils.openCsv(Global.pathToCsvRun);
		}
		else { //groupRun
			writerCsv = Utils.openCsv(Global.pathToCsvGroupRun);
		}

		//Collecting parameters of the run
		String [] parameters = new String[4];
		parameters[0] = String.valueOf(Global.N);
		parameters[1] = String.valueOf(Global.TTL);
		parameters[2] = String.valueOf(totMessages * Global.N);
		parameters[3] = String.valueOf(pLost);

		//Write parameters on the csv
		writerCsv.writeNext(parameters);

		try {
			writerCsv.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
