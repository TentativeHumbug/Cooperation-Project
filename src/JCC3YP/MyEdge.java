package JCC3YP;
/*
 * Required an edge class for the constructor in Barabasi-Albert method to utilise.
 * Only contains id.
 */
class MyEdge {
	private int id;
	
	public MyEdge(int id) {
		this.id = id;
	}
	
	public MyEdge() {
		this.id = Network.incEdgeCnt();
	}
	
	public int getId() {
		return id;
	}
	
	public String toString() { 
		return "E"+id;
	}
}