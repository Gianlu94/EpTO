import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.HashMap;

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
	private HashMap<Integer, ActorRef> myView = new HashMap<Integer, ActorRef>();

	public void onReceive(Object message) throws  Exception{

		if (message instanceof  Messages.StartingNode){
			Messages.StartingNode msg = (Messages.StartingNode)message;
			pss = msg.pss;
			st = msg.st;
			k = msg.k;
			churn = msg.churn;

			//ask Pss its id
			pss.tell(new Messages.IdRequest(), getSelf());
			//ask Pss starting view
			pss.tell(new Messages.RequestView(myId), getSelf());
		}
		else if (message instanceof Messages.IdResponse){
			myId = ((Messages.IdResponse) message).id;

		}
		else if (message instanceof  Messages.ResponseView){
			myView = ((Messages.ResponseView)message).view;

			//System.out.println("***** "+ myView.toString());
		}
		else if (message instanceof Messages.EventsRateCommunication){
			System.out.println ("Node "+ myId + " received spawn order");
		}
	}
}
