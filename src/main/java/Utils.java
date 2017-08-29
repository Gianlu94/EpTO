import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by gianluke on 29/08/17.
 */
public class Utils {

	//This method is responsible to select k node from a given node map
	//TODO: to implement checks on parameters (k <= nodeView)
	public static HashMap<Integer, ActorRef> getRandomNodes(HashMap<Integer,ActorRef> nodeView, int k){
		ArrayList<Integer> keyNodes = new ArrayList<Integer>(nodeView.keySet());
		Collections.shuffle(keyNodes);

		HashMap<Integer,ActorRef> selectedRandomNodes = new HashMap<Integer, ActorRef>();
		if (keyNodes.size() >= k) {
			for (int i = 0; i < k; i++) {
				int key = keyNodes.get(i);
				selectedRandomNodes.put(key, nodeView.get(key));
			}
		}
		return selectedRandomNodes;
	}
}
