package application;

import java.io.File;
import java.util.Scanner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jfree.ui.RefineryUtilities;
import tests.LineChart;
import tests.Tests;

/**
 * This is the class managing user-system interaction as well as system initialization
 */
public class Application {

	private static ActorSystem system;

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
			Global.CLOCKTYPE = parameters.getInt("CLOCKTYPE.value");
		}catch (Exception e){
			System.err.println("ERROR: Loading parameters failed");
		}
		File logConfigurationFile = new File("./src/main/resources/application.conf");
	    Config logConfig = ConfigFactory.parseFile(logConfigurationFile);
	    system = ActorSystem.create("mysystem", logConfig);
		//create PSS process
		Global.pss = system.actorOf(Props.create(PSS.class), "PSS");
		Global.pss.tell(new Messages.StartingPss(), null);

		//create initial nodes
		for (int i = 0; i < Global.N; i++){
			ActorRef node = system.actorOf(Props.create(Node.class), "Node" + i);
			node.tell(new Messages.StartingNode(Global.K, Global.C), null);
		}

		terminal();

	}

	private static void terminal(){
		Scanner input;
		String inputCommand;

		input = new Scanner(System.in);

		//how many event to spawn
		int eventsRate = 0;
		//duration of the spawning
		int duration = 0;

		Utils.cleanEnvironment();


		while (true){
			System.out.println("\n\n1) Simulate a single run of the algorithm ");
			System.out.println("2) Change algorithm's parameters ");
			System.out.println("3) Test_1: Verify Total Order of the last run ");
			System.out.println("4) Test_2: Compute percentage of messages lost in the last run ");
			System.out.println("    4a) Display msgs lost chart of the single runs on the variation of N" );
			System.out.println("    4b) Simulate a series of runs varying N and plot msg lost chart ");
			System.out.println("    4c) Simulate a series of runs varying TTL and plot msg lost chart ");

			inputCommand = input.nextLine();

			switch (inputCommand){
				case "1":
					System.out.print(" Option 1 ---- Insert number of events to spawn per second: ");
					eventsRate= Integer.parseInt(input.nextLine());
					System.out.print(" Option 1 ---- Insert duration of the spawn: ");
					duration = Integer.parseInt(input.nextLine());
					Global.pss.tell(new Messages.StartingSpawnEvents(eventsRate,duration), null);
					//wait for the run to finish
					try {
						for (int i = 0; i < (duration + 1); i++ ){
							System.out.print(". ");
							Thread.sleep(1000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Global.runCounter++; //increment the run
					break;
				case "2":
					while (!inputCommand.equals("b")){
						System.out.println("    2a) Number of nodes  ");
						System.out.println("    2b) K ");
						System.out.println("    2c) TTL  ");
						System.out.println("    2d) Churn ");
						System.out.println("    2e) Process drift ");
						System.out.println("    2f) Clock type");
						System.out.println("    b = go back  ");

						inputCommand = input.nextLine();

						switch (inputCommand) {
							case "2a":
								System.out.print("Insert new n: " );
								try{
									String n = input.nextLine();
									createNodes(Integer.parseInt(n));
								}
								catch (Exception e) {
									System.err.println("ERROR : Try again\n");
								}
								break;
							case "2b":
								System.out.print("Insert new K: " );
								try{
									String k = input.nextLine();
									Global.K = Integer.parseInt(k);
								}
								catch (Exception e) {
									System.err.println("ERROR : Try again\n");
								}
								break;
							case "2c":
								System.out.print("Insert new TTL: " );
								try{
									String ttl = input.nextLine();
									Global.TTL = Integer.parseInt(ttl);
								}
								catch (Exception e) {
									System.err.println("ERROR : Try again\n");
								}
								break;
							case "2d":
								System.out.print("Insert new churn: " );
								try{
									String c = input.nextLine();
									Global.C = Double.parseDouble(c);
								}
								catch (Exception e) {
									System.err.println("ERROR : Try again\n");
								}
								break;
							case "2e":
								System.out.print("Insert new process drift: " );
								try{
									String pd = input.nextLine();
									Global.D = Double.parseDouble(pd);
								}
								catch (Exception e) {
									System.err.println("ERROR : Try again\n");
								}
								break;
							case "2f":
								System.out.print("Insert new clock type (0 = logical; 1 = global): " );
								try{
									String cT = input.nextLine();
									Global.CLOCKTYPE = Integer.parseInt(cT);
								}
								catch (Exception e) {
									System.err.println("ERROR : Try again\n");
								}
								break;

							case "b":
								break;
							default:
								break;
						}

					}
					break;
				case "3":
					if (Tests.TestTotalOrder()){
						System.out.println (" ----- TOTAL ORDER SATISFIED :) ");
					}
					else{
						System.out.println (" ----- TOTAL ORDER IS NOT SATISFIED :( ");
					}

					break;
				case "4":
					int totMessages = eventsRate * duration * Global.N;
					Tests.TestPercentageMsgLost(totMessages, 0);
					break;
				case "4a":
					String [] legendToDisplay = Utils.createLegend("N = number of Nodes", "TTL = " + Global.TTL,
							"K = "+ Global.K);
					LineChart lineChart = new LineChart("Percentage of msgs lost", "N", "%lost",legendToDisplay,Global.pathToCsvRun);
					lineChart.pack();
					RefineryUtilities.centerFrameOnScreen(lineChart);
					lineChart.setVisible(true);
					break;
				case "4b":
					//nodes to create
					int n;
					//creation step
					int s;
					int ttl;
					int k;

					//delete csv
					Utils.deleteFile(Global.pathToCsvGroupRunNodes);

					System.out.print("      Option 4b ---- Number of nodes ");
					n = Integer.parseInt(input.nextLine());
					System.out.print("      Option 4b ---- Incremental step ");
					s = Integer.parseInt(input.nextLine());
					System.out.print("      Option 4b ---- TTL: ");
					ttl = Integer.parseInt(input.nextLine());
					Global.TTL = ttl; //update
					System.out.print("      Option 4b ---- K : ");
					k = Integer.parseInt(input.nextLine());
					Global.K = k;   //update
					System.out.print("      Option 4b ---- Insert number of events to spawn per second: ");
					eventsRate= Integer.parseInt(input.nextLine());
					System.out.print("      Option 4b ---- Insert duration of the spawn: ");
					duration = Integer.parseInt(input.nextLine());

					for (int i = 20; i <= n; i = i + s){

						createNodes(i);

						Global.pss.tell(new Messages.StartingSpawnEvents(eventsRate,duration), null);

						//wait for the run to finish
						try {
							Thread.sleep((duration + 1)*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						Global.runCounter++;
						int totMessagesRun = eventsRate * duration * i;
						Tests.TestPercentageMsgLost(totMessagesRun,1);

					}

					String [] legendToDisplay2 = Utils.createLegend("N = number of Nodes", "TTL = " + Global.TTL,
							"K = "+ Global.K);

					LineChart lineChart2 = new LineChart("Percentage of msgs lost", "N", "%lost",legendToDisplay2,Global.pathToCsvGroupRunNodes);
					lineChart2.pack();
					RefineryUtilities.centerFrameOnScreen(lineChart2);
					lineChart2.setVisible(true);
					break;
				case "4c":
					int n2;  //nodes to create
					int s2;  //creation step
					int ttl2;
					int k2;

					Utils.deleteFile(Global.pathToCsvGroupRunTTL);

					System.out.print("      Option 4c ---- Number of nodes ");
					n2 = Integer.parseInt(input.nextLine());
					System.out.print("      Option 4c ---- Final TTL: ");
					ttl2 = Integer.parseInt(input.nextLine());
					System.out.print("      Option 4c ---- Incremental step ");
					s2 = Integer.parseInt(input.nextLine());
					System.out.print("      Option 4c ---- K : ");
					k2 = Integer.parseInt(input.nextLine());
					Global.K = k2;   //update
					System.out.print("      Option 4c ---- Insert number of events to spawn per second: ");
					eventsRate= Integer.parseInt(input.nextLine());
					System.out.print("      Option 4c ---- Insert duration of the spawn: ");
					duration = Integer.parseInt(input.nextLine());


					createNodes(n2);


					for (int i = 4; i <= ttl2; i = i + s2){


						//update
						Global.TTL = i;


						Global.pss.tell(new Messages.StartingSpawnEvents(eventsRate,duration), null);

						//wait for the run to finish
						try {
							Thread.sleep((duration + 1)*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						Global.runCounter++;
						int totMessagesRun = eventsRate * duration * n2;
						Tests.TestPercentageMsgLost(totMessagesRun,2);

					}

					String [] legendToDisplay3 = Utils.createLegend("N = "+Global.N,
							"K = "+ Global.K, "Theoretical TTL "+ Math.ceil(2*Utils.log2(Global.N)));

					LineChart lineChart3 = new LineChart("Percentage of msgs lost", "TTL", "%lost",legendToDisplay3,Global.pathToCsvGroupRunTTL);
					lineChart3.pack();
					RefineryUtilities.centerFrameOnScreen(lineChart3);
					lineChart3.setVisible(true);

					break;
				default:
					break;
			}

		}
	}

	private static void createNodes (int N){

		//update
		Global.N = N;

		Global.pss.tell(new Messages.ShutDownNodes(), null);

		//wait nodes deletion
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//create nodes for the current run
		for (int i = 0;  i < N; i++){
			ActorRef node = system.actorOf(Props.create(Node.class), "Node" + i);
			node.tell(new Messages.StartingNode(i, Global.C), null);
		}

		//wait for nodes creation
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}
}
