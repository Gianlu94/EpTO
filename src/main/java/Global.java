import akka.actor.ActorRef;

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

	public static ActorRef pss;     //pss
}
