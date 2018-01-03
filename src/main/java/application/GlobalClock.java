package application;

/**
 * Class implements global clock
 */
public class GlobalClock {

	public static boolean isDeliverable (Event m){
		return m.getTtl() > Global.TTL;
	}

	public static long getClock(){
		return System.nanoTime();
	}
}
