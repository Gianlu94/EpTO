import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by gianluke on 24/08/17.
 *
 * This is the class containing the different kinds of messages
 * exchange among nodes
 */
public class Messages {

	//informs a node about PSS, schedule time event and churn
	public static class StartingNode implements Serializable{
		ActorRef pss;
		int st;
		double churn;

		public StartingNode (ActorRef pss, int st, double churn){
			this.pss = pss;
			this.st = st;
			this.churn = churn;
		}
	}
}
