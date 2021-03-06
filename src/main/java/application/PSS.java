package application;

import akka.actor.ActorRef;
import akka.actor.Kill;
import akka.actor.PoisonPill;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Pss class
 *
 */

public class PSS extends UntypedActor {


	//incrementing nodes id
	private int ids = 0;
	//keeps tracking of nodes
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
				getSender().tell(new Messages.ResponseView(sendingview), null);
			}
			else{
				//if not having SV nodes then check later again
				getContext().system().scheduler().scheduleOnce(
						Duration.create(400, TimeUnit.MILLISECONDS), getSelf(),
						message, getContext().system().dispatcher(), getSender()
						);
			}
		}
		else if (message instanceof Messages.StartingSpawnEvents){
			Messages.StartingSpawnEvents msg = (Messages.StartingSpawnEvents)message;
			spawnEvents(msg.eventsRate, msg.duration);
		}
		else if (message instanceof  Messages.ShutDownNodes){
			Messages.ShutDownNodes msg = (Messages.ShutDownNodes)message;
			shutdownNodes();
		}


	}


	private HashMap<Integer, ActorRef> createView (int sender){
		HashMap<Integer,ActorRef> tmpNodes = new HashMap<Integer, ActorRef>(nodes);
		tmpNodes.remove(sender);

		HashMap<Integer,ActorRef> sendingView;
		sendingView = Utils.getRandomNodes(tmpNodes, Global.SV);

		return sendingView;


	}

	private void spawnEvents (int eventsRate, int duration){
		for (int key: nodes.keySet()){
			nodes.get(key).tell(new Messages.EventsRateCommunication(eventsRate,duration), null);
		}
	}

	private void shutdownNodes (){
		if (nodes != null){
			for (int key : nodes.keySet()){
				nodes.get(key).tell(PoisonPill.getInstance(), null);
			}
			nodes.clear();
		}
	}

}
