package application;

/**
 * Created by gianluke on 25/08/17.
 */
public class Event  implements Comparable<Event>{
	private String id;
	private long ts;
	private int ttl;
	private int sourceId;


	public Event (){
	}

	public Event(int ts, int ttl, int sourceId){
		setTs(ts);
		setTtl(ttl);
		setSourceId(sourceId);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
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

	@Override
	public boolean equals (Object o){
		return this.getId() == ((Event)o).getId();
	}


	@Override
	public int compareTo(Event o) {

		if (this.getTs() < o.getTs()){
			return -1;
		}
		else if (this.getTs() > o.getTs()){
			return 1;
		}
		else if (this.getTs() == o.getTs()){
			if (this.getSourceId() < o.getSourceId()){
				return -1;
			}
			else {
				return 1;
			}
		}

		return 0;
	}
}
