package tests;

import java.io.IOException;
import java.util.ArrayList;

import application.Global;
import application.Utils;
import com.opencsv.CSVWriter;
import org.jgrapht.DirectedGraph;

/**
 * Class contains the following test:
 *  - total order
 *  - percentage of msg lost for run
 */
public class Tests {

	public static boolean TestTotalOrder (){
		ArrayList<ArrayList<String>> logs = new ArrayList<ArrayList<String>>();
		logs = Utils.uploadLogs();

		DirectedGraph graph = Utils.createLogsGraph(logs);
		boolean result = Utils.checkTotalOrder(graph);
		return result;
	}

	public static void TestPercentageMsgLost (int totMessages, int typeOfRun){
		ArrayList<ArrayList<String>> logs = new ArrayList<ArrayList<String>>();
		logs = Utils.uploadLogs();

		int pLost = 0;

		for (ArrayList<String> log : logs){
			pLost = pLost + totMessages - log.size();
		}


		CSVWriter writerCsv = null;
		if (typeOfRun == 0){ //single Run
			writerCsv = Utils.openCsv(Global.pathToCsvRun);
			double percentageLostMsg = (double)pLost/(double)(totMessages* Global.N);
			System.out.print("Percentage of lost messages: " + percentageLostMsg);
		}
		else if (typeOfRun == 1) { //groupRun on variation of N
			writerCsv = Utils.openCsv(Global.pathToCsvGroupRunNodes);
		}
		else{ //groupRun on variation of TTL
			writerCsv = Utils.openCsv(Global.pathToCsvGroupRunTTL);
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
