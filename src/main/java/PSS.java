import akka.actor.ActorRef;
import akka.actor.UntypedActor;
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

	private int sizeOfNodeView;
	private int ids = 0;
	private HashMap<Integer,ActorRef> nodes;

	public void onReceive (Object message) throws Exception{

		if (message instanceof Messages.StartingPss){
			Messages.StartingPss msg = (Messages.StartingPss) message;

			sizeOfNodeView = msg.sv;
			nodes = new HashMap<Integer, ActorRef>();
		}
		else if (message instanceof  Messages.IdRequest){
			getSender().tell(new Messages.IdResponse(ids), null);
			nodes.put(ids, getSender());
			ids++;

		}
		else if (message instanceof Messages.RequestView) {
			if ((nodes.size() - 1) >= sizeOfNodeView){
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

	}

	private HashMap<Integer, ActorRef> createView (int sender){
		HashMap<Integer,ActorRef> tmpNodes = new HashMap<Integer, ActorRef>(nodes);
		tmpNodes.remove(sender);

		ArrayList<Integer>remainingNodeKeys = new ArrayList<Integer>(tmpNodes.keySet());
		Collections.shuffle(remainingNodeKeys);

		HashMap<Integer,ActorRef> sendingView = new HashMap<Integer, ActorRef>();
		for (int i = 0; i < sizeOfNodeView; i++){
			int key = remainingNodeKeys.get(i);
			sendingView.put(key, tmpNodes.get(key));
		}

		return sendingView;


	}

	private void spawnEvents (int eventsRate, int duration){
		for (int key: nodes.keySet()){
			nodes.get(key).tell(new Messages.EventsRateCommunication(eventsRate,duration), null);
		}
	}

}
