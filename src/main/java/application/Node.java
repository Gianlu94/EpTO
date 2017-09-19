package application;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

import java.util.*;
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
	private long lastDeliveredTs = 0;

	private String pathLog;

	private HashMap<Integer, ActorRef> myView = new HashMap<Integer, ActorRef>();
	private HashMap<String, Event> nextBall = new HashMap<String, Event>();
	private HashMap<String, Event> received = new HashMap<String, Event>();
	private ArrayList<Event> delivered = new ArrayList<Event>();


	public void onReceive(Object message) throws  Exception{

		if (message instanceof  Messages.StartingNode){
			Messages.StartingNode msg = (Messages.StartingNode)message;

			st = msg.st;
			k = msg.k;
			churn = msg.churn;

			//ask Pss its id
			Global.pss.tell(new Messages.IdRequest(), getSelf());

		}
		else if (message instanceof Messages.IdResponse){
			myId = ((Messages.IdResponse) message).id;
			
			//ask Pss starting view
			Global.pss.tell(new Messages.RequestView(myId), getSelf());

		}
		else if (message instanceof  Messages.ResponseView){
			myView = ((Messages.ResponseView)message).view;
			startingRounds();
			//System.out.println("***** "+ myView.toString());
		}
		else if (message instanceof Messages.EventsRateCommunication){
			Messages.EventsRateCommunication msg = (Messages.EventsRateCommunication)message;
			//System.out.println ("Node "+ myId + " received spawn order");
			pathLog = Global.pathNode + myId+ "/Log.txt";
			Utils.createFile(pathLog);

			scheduleEvents(msg.eventsRate, msg.duration);

		}
		else if (message instanceof  Messages.CreateEvent){
			Event event = new Event();
			event.setId(myId+"-"+idMessages); //set message id (node id + message id)
			idMessages++; //for the next messages
			event.setTs(GlobalClock.getClock());
			event.setTtl(0);
			event.setSourceId(myId);

			nextBall.put(event.getId(), event);




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
				//System.out.println("Node "+myId + ":  k = "+ Global.K+ " peersSize = "+ peers.size());
				for (int q : peers.keySet()){
					peers.get(q).tell(new Messages.Ball(nextBall),null);
				}
			}


			orderEvents(nextBall);
			nextBall.clear();
		}
		else if (message instanceof Messages.Ball){

			HashMap<String, Event> ball = ((Messages.Ball) message).nextball;

			for (String key : ball.keySet()){
				Event event = ball.get(key);

				if (event.getTtl() < Global.TTL){
					if (nextBall.containsKey(event.getId())) {
						if (nextBall.get(event.getId()).getTtl() < event.getTtl()) {
							//System.out.println("EVENT BALL KEY = " + event.getId() + "-- EVENT NEXT BALL KEY "+nextBall.get(event.getId()));
							nextBall.get(event.getId()).setTtl(event.getTtl());
						}
					}
					else{
						nextBall.put(event.getId(), event);
					}
				}

				//UPDATECLOCK

			}

		}

	}

	private void scheduleEvents (int eventsRate, int duration){
		for (int i = 0; i < eventsRate * duration; i++){
			double random = ThreadLocalRandom.current().nextDouble(0, duration);
			getContext().system().scheduler().scheduleOnce(
					Duration.create((long)(random*1000000000), TimeUnit.NANOSECONDS), getSelf(),
					new Messages.CreateEvent(), getContext().system().dispatcher(), null
			);
		}
	}

	private void startingRounds() {
		getContext().system().scheduler().schedule(
				Duration.create(0, TimeUnit.SECONDS), Duration.create(Global.RD, TimeUnit.MICROSECONDS), getSelf(),
				new Messages.Round(), getContext().system().dispatcher(), null
		);
	}

	private void orderEvents(HashMap<String, Event> ball){

		for (String key : received.keySet()){
			Event event = received.get(key);
			event.setTtl(event.getTtl() + 1);
		}

		for (String key : ball.keySet()){
			Event event = ball.get(key);

			if ((!delivered.contains(event)) && (event.getTs() >= lastDeliveredTs)){
				if (received.containsKey(event.getId())){
					if (received.get(event.getId()).getTtl() < event.getTtl()){
						received.get(event.getId()).setTtl(event.getTtl());
					}
				}
				else{
					received.put(event.getId(), event);
				}
			}
		}

		Long minQueuedTs = Long.MAX_VALUE;
		int minSourceId = -1;
		HashSet<Event> deliverableEvents = new HashSet<Event>();

		for (String key : received.keySet()){
			Event event = received.get(key);

			if (GlobalClock.isDeliverable(event)){
				deliverableEvents.add(event);
			}
			else{
				if (minQueuedTs > event.getTs()){
					minQueuedTs = event.getTs();
					minSourceId = event.getSourceId();
				}
			}

		}

		Iterator it = deliverableEvents.iterator();
		while (it.hasNext()){
			Event event = (Event) it.next();
			if (event.getTs() > minQueuedTs){
				it.remove();
			}
			else if ((event.getTs() == minQueuedTs) && (event.getSourceId() > minSourceId)) {
				it.remove();
			}
			else {
				received.remove(event.getId());
			}
		}

		/*
		for (int i = 0; i < deliverableEvents.size(); i++){
			Event event = deliverableEvents.get(i);
			if (event.getTs() > minQueuedTs){
				deliverableEvents.remove(i);
			}
			else{
				received.remove(event.getId());
			}
		}
		*/

		ArrayList<Event> deliverableEventsSorted = new ArrayList<Event>(deliverableEvents);
		Collections.sort(deliverableEventsSorted);

		for (Event event : deliverableEventsSorted){
			delivered.add(event);
			lastDeliveredTs = event.getTs();
			deliver(event);
		}


	}

	private void deliver (Event event){
		Utils.writeOnAFile(pathLog,event.getId());
		//System.out.println("Node "+ myId + "delivered " + event.getId() );
	}


}
