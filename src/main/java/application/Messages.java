package application;

import akka.actor.ActorRef;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * This is the class containing the different kinds of messages
 * exchanged among nodes
 *
 */

public class Messages {

	//start pss process
	public static class StartingPss implements  Serializable{}


	//informs a node about PSS, schedule time event and churn
	public static class StartingNode implements Serializable{
		int k;
		double churn;

		public StartingNode (int k, double churn){
			this.k = k;
			this.churn = churn;
		}
	}

	//request id sent by a node to pss
	public static class IdRequest implements Serializable{};

	//response id sent by pss to the requesting node
	public static class IdResponse implements  Serializable{
		int id;

		public IdResponse (int id){
			this.id = id;
		}
	}

	//request view sent by a node to pss
	public static class RequestView implements Serializable{
		int sender;

		public RequestView (int sender){
			this.sender = sender;
		}
	}

	//response view sent by pss to the requesting node
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

	//message sent by pss to a node in order to create a new event
	public static class CreateEvent implements Serializable{}


	//message used to send a Ball of msgs to another node
	public  static class Ball implements Serializable{
		HashMap<String, Event> nextball;

		public Ball (HashMap<String, Event> nextBall){
			nextball = new HashMap <String, Event>();
			for (String key : nextBall.keySet()){
				this.nextball.put(key, new Event(nextBall.get(key)));
			}
		}
	}


	//message used to simulate each round
	public static class Round implements Serializable{};

	//message sent to pss to shutdown nodes
	public static class ShutDownNodes implements Serializable{}
}
