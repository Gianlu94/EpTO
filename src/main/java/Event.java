/**
 * Created by gianluke on 25/08/17.
 */
public class Event {
	private int ts;
	private int ttl;
	private int sourceId;


	public Event (){
	}

	public Event(int ts, int ttl, int sourceId){
		setTs(ts);
		setTtl(ttl);
		setSourceId(sourceId);
	}

	public int getTs() {
		return ts;
	}

	public void setTs(int ts) {
		this.ts = ts;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
}
