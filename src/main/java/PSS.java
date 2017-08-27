import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by gianluke on 24/08/17.
 *
 */

public class PSS extends UntypedActor {


	private int ids = 0;
	private HashMap<Integer,ActorRef> nodes;

	public void onReceive (Object message) throws Exception{

		if (message instanceof Messages.StartingPss){

			nodes = new HashMap<Integer, ActorRef>();
		}
		else if (message instanceof  Messages.IdRequest){
			getSender().tell(new Messages.IdResponse(ids), null);
			nodes.put(ids, getSender());
			ids++;

		}
		else if (message instanceof Messages.RequestView) {
			if ((nodes.size() - 1) >= Global.SV){
				Messages.RequestView msg = (Messages.RequestView) message;
				HashMap<Integer,ActorRef> sendingview = createView(msg.sender);
				//System.out.println("*******ID sendert")
				//System.out.println("***** "+ myView.toString());
				getSender().tell(new Messages.ResponseView(sendingview), null);
			}
			else{
				getContext().system().scheduler().scheduleOnce(
						Duration.create(1, TimeUnit.SECONDS), getSelf(),
						message, getContext().system().dispatcher(), getSender()
						);
			}
		}
		else if (message instanceof Messages.StartingSpawnEvents){
			Messages.StartingSpawnEvents msg = (Messages.StartingSpawnEvents)message;
			spawnEvents(msg.eventsRate, msg.duration);
		}
		else if (message instanceof Messages.RandomViewNodes){
			Messages.RandomViewNodes msg = (Messages.RandomViewNodes)message;
			HashMap<Integer,ActorRef> randomNodes = new HashMap<Integer, ActorRef>();

			//select k nodes
			randomNodes = getRandomNodes(msg.view, msg.k);

			//response back to node
			getSender().tell(new Messages.ResponseRandomNodes(randomNodes), null);
		}

	}

	//This method is responsible to select k node from a given node map
	//TODO: to implement checks on parameters (k <= nodeView)
	private HashMap<Integer, ActorRef> getRandomNodes(HashMap<Integer,ActorRef> nodeView, int k){
		ArrayList<Integer> keyNodes = new ArrayList<Integer>(nodeView.keySet());
		Collections.shuffle(keyNodes);

		HashMap<Integer,ActorRef> selectedRandomNodes = new HashMap<Integer, ActorRef>();
		for (int i = 0; i < k; i++){
			int key = keyNodes.get(i);
			selectedRandomNodes.put(key, nodeView.get(key));
		}

		return selectedRandomNodes;
	}

	private HashMap<Integer, ActorRef> createView (int sender){
		HashMap<Integer,ActorRef> tmpNodes = new HashMap<Integer, ActorRef>(nodes);
		tmpNodes.remove(sender);

		HashMap<Integer,ActorRef> sendingView = new HashMap<Integer, ActorRef>();
		sendingView = getRandomNodes(tmpNodes, Global.SV);

		return sendingView;


	}

	private void spawnEvents (int eventsRate, int duration){
		for (int key: nodes.keySet()){
			nodes.get(key).tell(new Messages.EventsRateCommunication(eventsRate,duration), null);
		}
	}

}
