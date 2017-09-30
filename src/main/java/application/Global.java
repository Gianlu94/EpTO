package application;

import akka.actor.ActorRef;

import java.util.Vector;

/**
 * Created by gianluke on 25/08/17.
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

	public static int runCounter = 0;

	public static ActorRef pss;     //pss

	public static String pathOutPut = "./output/";
	public static String pathToRun = pathOutPut + "run-";
	public static String pathNode = "/Node-";
	public static String pathToCsvRun = pathOutPut + "dataRun.csv";
	public static String pathToCsvGroupRun = pathOutPut + "dataGroupRun.csv";
}
