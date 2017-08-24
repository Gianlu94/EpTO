import java.io.File;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by gianluke on 24/08/17.
 */
public class Application {

	static int N, SV, K, TTL, RD; //paramters: nodes, view size, fanout, nb rounds delayed, round duration
	static double C, D; //parameters: churn, drift

	public static void main (String [] args){

		//Load paramenters from Parameters configuration file
		File parameterFile = new File("./src/main/resources/parameters.conf");
		Config parameters = ConfigFactory.parseFile(parameterFile);


		try{
			N = parameters.getInt("N.value");
			SV = parameters.getInt("SV.value");
			K = parameters.getInt("K.value");
			TTL = parameters.getInt("TTL.value");
			RD = parameters.getInt("RD.value");
			C = parameters.getDouble("C.value");
			D = parameters.getDouble("D.value");
		}catch (Exception e){
			System.err.println("ERROR: Loading parameters failed");
		}

	}
}
