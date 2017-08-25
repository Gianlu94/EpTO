import akka.actor.ActorRef;

import java.io.Serializable;
import java.util.HashMap;

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
	}

	//request view to pss from node
	public static class RequestView implements Serializable{
		int sender;

		public RequestView (int sender){
			this.sender = sender;
		}
	}

	//response view to node from pss
	public static class ResponseView implements  Serializable{
		HashMap<Integer, ActorRef> view;

		public ResponseView (HashMap<Integer, ActorRef> view){
			this.view = view;
		}
	}

	//message sent to pss in order to start the spawn of new events
	public static class StartingSpawnEvents implements Serializable{
		int eventsRate;
		int duration;

		public StartingSpawnEvents (int eventsRate, int duration){
			this.eventsRate = eventsRate;
			this.duration = duration;
		}
	}

	//message sent to node to communicate the rate for the events creation
	public static class EventsRateCommunication implements Serializable{
		int eventsRate;
		int duration;

		public EventsRateCommunication (int eventsRate, int duration){
			this.eventsRate = eventsRate;
			this.duration = duration;
		}
	}

	//
	public static class CreateEvent implements Serializable{}



}
