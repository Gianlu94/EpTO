import akka.actor.UntypedActor;

/**
 * Created by gianluke on 24/08/17.
 *
 */

public class PSS extends UntypedActor {

	private int sizeOfNodeView;

	public void onReceive (Object message) throws Exception{

		if (message instanceof Messages.StartingPss){
			Messages.StartingPss msg = (Messages.StartingPss) message;

			sizeOfNodeView = msg.sv;
		}
	}
}
