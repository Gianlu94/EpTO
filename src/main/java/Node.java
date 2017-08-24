import akka.actor.ActorRef;
import akka.actor.UntypedActor;

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

	public void onReceive(Object message) throws  Exception{

		if (message instanceof  Messages.StartingNode){
			Messages.StartingNode msg = (Messages.StartingNode)message;
			pss = msg.pss;
			st = msg.st;
			k = msg.k;
			churn = msg.churn;

			//ask Pss its id
			pss.tell(new Messages.IdRequest(), getSelf());
		}
		else if (message instanceof Messages.IdResponse){
			myId = ((Messages.IdResponse) message).id;
			System.out.println("******** MY ID is "+myId);
		}
	}
}
