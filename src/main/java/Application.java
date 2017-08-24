import java.io.File;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by gianluke on 24/08/17.
 */
public class Application {

	static int N, SV, K, TTL, RD, ST; //paramters: nodes, view size, fanout, nb rounds delayed, round duration, schedule
	//time event
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
			ST = parameters.getInt("ST.value");
			C = parameters.getDouble("C.value");
			D = parameters.getDouble("D.value");
		}catch (Exception e){
			System.err.println("ERROR: Loading parameters failed");
		}

		final ActorSystem system = ActorSystem.create("mysystem");

		//create PSS process
		ActorRef pss = system.actorOf(Props.create(PSS.class), "PSS");

		//create initial nodes
		for (int i = 0; i < N; i++){
			ActorRef node = system.actorOf(Props.create(Node.class), "Node" + i);
			node.tell(new Messages.StartingNode(pss,ST), null);
		}

	}
}
