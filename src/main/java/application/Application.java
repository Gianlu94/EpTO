package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Util;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import scala.Int;
import tests.LineChart;
import tests.Tests;

import javax.swing.*;

/**
 * Created by gianluke on 24/08/17.
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
		}catch (Exception e){
			System.err.println("ERROR: Loading parameters failed");
		}

		system = ActorSystem.create("mysystem");
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
		String [] tokensInput;
		Integer tokensNumber;
		String firstCommand;

		input = new Scanner(System.in);
		int eventsRate = 0;
		int duration = 0;

		Utils.cleanEnvironment();


		while (true){
			System.out.println("\n\n1) How many events to spawn ");
			System.out.println("2) Test_1: Verify Total Order ");
			System.out.println("3) Test_2: Number of messages lost");
			System.out.println("    3a) Percentage of msgs lost chart");
			System.out.println("    3b) Plot on the variation of N ");
			//System.out.println("    a) Percentage of messages lost ")

			inputCommand = input.nextLine();

			switch (inputCommand){
				case "1":
					System.out.print(" Option 1 ---- Insert number of events to spawn per second: ");
					eventsRate= Integer.parseInt(input.nextLine());
					System.out.print(" Option 1 ---- Insert duration of the spawn: ");
					duration = Integer.parseInt(input.nextLine());
					Global.pss.tell(new Messages.StartingSpawnEvents(eventsRate,duration), null);
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
					/*if(Global.deliveredEvents.size() != 0){
						for (Event event : Global.deliveredEvents){

						}
					}
					*/
					//Global.deliveredEvents.clear();
				case "2":
					if (Tests.TestTotalOrder()){
						System.out.println (" ----- TOTAL ORDER SATISFIED :) ");
					}
					else{
						System.out.println (" ----- TOTAL ORDER IS NOT SATISFIED :( ");
					}

					break;
				case "3":
					int totMessages = eventsRate * duration * Global.N;
					Tests.TestPercentageMsgLost(totMessages, 0);
					break;
				case "3a":
					String [] legendToDisplay = Utils.createLegend("N = number of Nodes", "TTL = " + Global.TTL,
							"K = "+ Global.K);
					LineChart lineChart = new LineChart("Percentage of msgs lost", "N", "%lost",legendToDisplay,Global.pathToCsvRun);
					lineChart.pack();
					RefineryUtilities.centerFrameOnScreen(lineChart);
					lineChart.setVisible(true);
					break;
				case "3b":
					int n;  //nodes to create
					int s;  //creation step
					int ttl;
					int k;

					System.out.print("      Option 3b ---- Number of nodes ");
					n = Integer.parseInt(input.nextLine());
					System.out.print("      Option 3b ---- Incremental step ");
					s = Integer.parseInt(input.nextLine());
					System.out.print("      Option 3b ---- TTL: ");
					ttl = Integer.parseInt(input.nextLine());
					Global.TTL = ttl; //update
					System.out.print("      Option 3b ---- K : ");
					k = Integer.parseInt(input.nextLine());
					Global.K = k;   //update
					System.out.print("      Option 3b ---- Insert number of events to spawn per second: ");
					eventsRate= Integer.parseInt(input.nextLine());
					System.out.print("      Option 3b ---- Insert duration of the spawn: ");
					duration = Integer.parseInt(input.nextLine());

					for (int i = 20; i <= n; i = i + s){

						//start a new run
						Global.pss.tell(new Messages.ShutDownNodes(), null);
						//update
						Global.N = i;

						//wait
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						//create nodes for the current run
						for (int j = 0;  j < i; j++){
							ActorRef node = system.actorOf(Props.create(Node.class), "Node" + j);
							node.tell(new Messages.StartingNode(k, Global.C), null);
						}

						//wait
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						Global.pss.tell(new Messages.StartingSpawnEvents(eventsRate,duration), null);

						//wait
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

					LineChart lineChart2 = new LineChart("Percentage of msgs lost", "N", "%lost",legendToDisplay2,Global.pathToCsvGroupRun);
					lineChart2.pack();
					RefineryUtilities.centerFrameOnScreen(lineChart2);
					lineChart2.setVisible(true);

				default:
					break;
			}
		}
	}
}
