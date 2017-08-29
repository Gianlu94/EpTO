import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by gianluke on 24/08/17.
 * This is the class that represents a node of the network
 */
public class Node extends UntypedActor {

	private ActorRef pss;
	private int myId;
	private int st;
	private int k;
	private double churn;
	private int idMessages; //counter used to assign id to messages
	private HashMap<Integer, ActorRef> myView = new HashMap<Integer, ActorRef>();
	private HashMap<String, Event> nextBall = new HashMap<String, Event>();

	public void onReceive(Object message) throws  Exception{

		if (message instanceof  Messages.StartingNode){
			Messages.StartingNode msg = (Messages.StartingNode)message;

			st = msg.st;
			k = msg.k;
			churn = msg.churn;

			//ask Pss its id
			Global.pss.tell(new Messages.IdRequest(), getSelf());
			//ask Pss starting view
			Global.pss.tell(new Messages.RequestView(myId), getSelf());
		}
		else if (message instanceof Messages.IdResponse){
			myId = ((Messages.IdResponse) message).id;

		}
		else if (message instanceof  Messages.ResponseView){
			myView = ((Messages.ResponseView)message).view;
			startingRounds();
			//System.out.println("***** "+ myView.toString());
		}
		else if (message instanceof Messages.EventsRateCommunication){
			Messages.EventsRateCommunication msg = (Messages.EventsRateCommunication)message;
			//System.out.println ("Node "+ myId + " received spawn order");
			scheduleEvents(msg.eventsRate, msg.duration);

		}
		else if (message instanceof  Messages.CreateEvent){
			Event event = new Event();
			event.setId(myId+"-"+idMessages); //set message id (node id + message id)
			idMessages++; //for the next messages
			event.setTs(GlobalClock.getClock());
			event.setTtl(0);
			event.setSourceId(myId);
			//System.out.println("Node "+ myId+ " message" + event.getId());
			if (nextBall == null) { //TODO: why?
				nextBall = new HashMap<String, Event>();
			}
			nextBall.put(event.getId(), event);
			//System.out.println("Node "+ myId+ " nextballsize " + nextBall.size());
			//nextBall.put(event.getId(), event);



		}
		else if (message instanceof Messages.Round) {
			//1st part


			//increment Ttl
			for (String keyEvent : nextBall.keySet()) {
				Event event = nextBall.get(keyEvent);
				event.setTtl(event.getTtl() + 1);
			}




			if (nextBall.size() != 0){
				//get k nodes from my view
				HashMap<Integer,ActorRef> peers;
				peers = Utils.getRandomNodes(myView,Global.K);
				System.out.println("Node "+myId + ":  k = "+ Global.K+ " peersSize = "+ peers.size());
				for (int q : peers.keySet()){
					peers.get(q).tell(new Messages.Ball(nextBall),null);
				}
			}


			//ORDEREVENTS(nextBall);
			nextBall.clear();
		}

	}

	private void scheduleEvents (int eventsRate, int duration){
		for (int i = 0; i < eventsRate * duration; i++){
			double random = ThreadLocalRandom.current().nextDouble(0, duration);
			getContext().system().scheduler().scheduleOnce(
					Duration.create((long)(random*1000000), TimeUnit.NANOSECONDS), getSelf(),
					new Messages.CreateEvent(), getContext().system().dispatcher(), null
			);
		}
	}

	private void startingRounds() {
		getContext().system().scheduler().schedule(
				Duration.create(0, TimeUnit.SECONDS), Duration.create(Global.RD, TimeUnit.SECONDS), getSelf(),
				new Messages.Round(), getContext().system().dispatcher(), null
		);
	}


}
