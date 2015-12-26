package JCC3YP;
/*
 * Easy way to facilitate interactions in trust models.
 * Can store the vertex interacted with and the strategy selected.
 * 
 */
public class Interaction {
	public Interaction(MyVertex vertex, char strat) {
		this.vertex=vertex;
		this.strat=strat;
	}
	public MyVertex vertex;
	public char strat;
}
