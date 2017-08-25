import java.io.File;
import java.util.Scanner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.Int;

/**
 * Created by gianluke on 24/08/17.
 */
public class Application {

	public static void main (String [] args){

		//Load paramenters from Parameters configuration file
		File parameterFile = new File("./src/main/resources/parameters.conf");
		Config parameters = ConfigFactory.parseFile(parameterFile);


		try{
			Global.N = parameters.getInt("N.value");
			Global.SV = parameters.getInt("SV.value");
			Global.K = parameters.getInt("K.value");
			Global.TTL = parameters.getInt("TTL.value");
			Global.RD = parameters.getInt("RD.value");
			Global.ST = parameters.getInt("ST.value");
			Global.C = parameters.getDouble("C.value");
			Global.D = parameters.getDouble("D.value");
		}catch (Exception e){
			System.err.println("ERROR: Loading parameters failed");
		}

		final ActorSystem system = ActorSystem.create("mysystem");

		//create PSS process
		Global.pss = system.actorOf(Props.create(PSS.class), "PSS");
		Global.pss.tell(new Messages.StartingPss(), null);

		//create initial nodes
		for (int i = 0; i < Global.N; i++){
			ActorRef node = system.actorOf(Props.create(Node.class), "Node" + i);
			node.tell(new Messages.StartingNode(Global.ST,Global.K,Global.C), null);
		}

		terminal();

	}

	private static void terminal(){
		Scanner input;
		String inputCommand;
		String [] tokensInput;
		Integer tokensNumber;
		String firstCommand;

		input = new Scanner(System.in);

		while (true){
			System.out.println("\n\n    1) How many events to spawn ");
			System.out.println("    2) ....\n\n\n");
			inputCommand = input.nextLine();

			switch (inputCommand){
				case "1":
					System.out.print(" Option 1 ---- Insert number of events to spawn per second: ");
					int eventsRate= Integer.parseInt(input.nextLine());
					System.out.print(" Option 1 ---- Insert duration of the spawn: ");
					int duration = Integer.parseInt(input.nextLine());
					Global.pss.tell(new Messages.StartingSpawnEvents(eventsRate,duration), null);
					break;
				default:
					break;
			}
		}
	}
}
