package application;

import akka.actor.ActorRef;

import java.util.Vector;

/**
 * Class containing variables with globa access
 */
public class Global {

	public static int N;            //nodes
	public static int SV;           //view size
	public static int K;            //fanout
	public static int TTL;          //nb rounds delayed,
	public static int RD;           //round duration
	public static int ST;           //schedule time event

	public static double C;         //churn
	public static double D;         //drift

	public static int CLOCKTYPE;

	public static int runCounter = 0;

	public static ActorRef pss;     //pss

	//path to the output folder containing runs
	public static String pathOutPut = "./output/";
	//path to the i-th run folder
	public static String pathToRun = pathOutPut + "run-";
	//path to the i-th node node
	public static String pathNode = "/Node-";

	public static String pathToCsvRun = pathOutPut + "dataRun.csv";
	//path to the csv containing data of test with N
	public static String pathToCsvGroupRunNodes = pathOutPut + "dataGroupRunNodes.csv";
	//path to the csv containing data of test with TTL
	public static String pathToCsvGroupRunTTL = pathOutPut + "dataGroupRunTTL.csv";
}
