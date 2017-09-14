package application;

/**
 * Created by gianluke on 25/08/17.
 */
public class GlobalClock {

	public static boolean isDeliverable (Event m){
		return m.getTtl() > Global.TTL;
	}

	public static long getClock(){
		return System.currentTimeMillis();
	}
}
