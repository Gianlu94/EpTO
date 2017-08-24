import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by gianluke on 24/08/17.
 *
 * This is the class containing the different kinds of messages
 * exchange among nodes
 */
public class Messages {

	//informs a node about PSS and schedule time event
	public static class StartingNode implements Serializable{
		ActorRef pss;
		int st;

		public StartingNode (ActorRef pss, int st){
			this.pss = pss;
			this.st = st;
		}
	}
}
