package application;

/**
 * Created by gianluke on 10/10/17.
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
