package application;

/**
 * Class implements logical clock
 */
public class LogicalClock {
	long logicalClock = 0;

	public boolean isDeliverable (Event m){
		return m.getTtl() > Global.TTL;
	}

	public long getClock(){
		logicalClock++;
		return logicalClock;
	}

	public void updateClock(long ts){
		if (ts > logicalClock){
			logicalClock = ts;
		}
	}
}
