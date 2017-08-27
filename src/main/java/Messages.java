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

	//start pss process
	public static class StartingPss implements  Serializable{}


	//informs a node about PSS, schedule time event and churn
	public static class StartingNode implements Serializable{
		int st;
		int k;
		double churn;

		public StartingNode (int st, int k, double churn){
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

	//message sent to Node in order to create a new event
	public static class CreateEvent implements Serializable{}

	//message used to send a Ball of msgs to another node
	public  static class Ball implements Serializable{
		HashMap<String, Event> nextball;

		public Ball (HashMap<String, Event> nextBall){
			this.nextball = nextBall;
		}
	}

	//message sent by a node to Pss in order to get k random nodes from its view
	public  static class  RandowViewNodes implements  Serializable{
		HashMap<Integer, ActorRef> view;
		int k;

		//TODO: use k from global?
		public RandowViewNodes(HashMap<Integer,ActorRef> view, int k){
			this.view = view;
			this.k = k;
		}
	}
}
