import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by gianluke on 24/08/17.
 *
 * This is the class containing the different kinds of messages
 * exchange among nodes
 */
public class Messages {

	//informs pss about size of nodes view
	public static class StartingPss implements  Serializable{
		int sv;
		public StartingPss (int sv){
			this.sv = sv;
		}
	}


	//informs a node about PSS, schedule time event and churn
	public static class StartingNode implements Serializable{
		ActorRef pss;
		int st;
		int k;
		double churn;

		public StartingNode (ActorRef pss, int st, int k, double churn){
			this.pss = pss;
			this.st = st;
			this.k = k;
			this.churn = churn;
		}
	}

	//request id to pss from node
	public static class IdRequest implements Serializable{};

	//response id to node from pss
	public static class IdResponse implements  Serializable{
		int id;

		public IdResponse (int id){
			this.id = id;
		}
	};
}