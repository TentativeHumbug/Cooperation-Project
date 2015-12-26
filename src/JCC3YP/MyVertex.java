package JCC3YP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
/*
 * Originally required for an edge class for the constructor in Barabasi-Albert method to utilise.
 * Modified to facilitate adding nodes with dispositions.
 * Also contains method for calculating a recencyRating used in trust values.
 */
class MyVertex {
	private int id;
	private char strat;
	private double payoff;
	private double optimism;
	
	private ArrayList<Interaction> lastInteractions = null;
	
	public MyVertex(int id,double optMean,double optVar) {
		this.id = id;
		lastInteractions = new ArrayList<Interaction>();
		payoff = 0;
		optimism = getOptimism(optMean,optVar);
	}
	
	public MyVertex(double optMean,double optVar) {
		this.id = Network.incVertexCnt();
		lastInteractions = new ArrayList<Interaction>();
		payoff = 0;
		optimism = getOptimism(optMean,optVar);
	}
	
	public int getId() {
		return id;
	}
	
	//Used to assign strategies in Ichinose method after being passed a probability
    public void assignStrat(double prob) {
    	char cooperator = CooperationModel.COOPERATOR;
    	char defector = CooperationModel.DEFECTOR;
		double rand = Math.random();		
		if(rand > prob) {
			setStrat(defector);
		} else {
			setStrat(cooperator);
		}
	}
	
	public void setStrat(char c) {
		strat = c;	
	}
	
	public char getStrat() {
		return strat;
	}
	
	public void setPayoff(double payoff) {
		this.payoff = payoff;
	}
	
	public double getPayoff() {
		return payoff;
	}
	
	public void clearPayoff() {
		payoff = 0;
	}
	
	//Returns a list of all the last interactions and their respecctive objects
	public ArrayList<Interaction> getLastInteractions() {
		return lastInteractions;
	}

	public void setLastInteractions(ArrayList<Interaction> lastInteractions) {
		this.lastInteractions = lastInteractions;
	}
	
	public void addInteraction(MyVertex vertex) {
		lastInteractions.add(new Interaction(vertex,vertex.getStrat()));
	}
	
	public int getNumInteractionsWith(MyVertex vertex) {
		int n = 0;
		for(Iterator<Interaction> iterator = lastInteractions.iterator(); iterator.hasNext();){
			Interaction interaction = iterator.next();
			if(interaction.vertex == vertex) {
				n++;
			}
		}
		return n;
	}
	
	public int getNumCoopInteractionsWith(MyVertex vertex) {
		int n = 0;
		for(Iterator<Interaction> iterator = lastInteractions.iterator(); iterator.hasNext();) {
			Interaction interaction = iterator.next();
			if(interaction.vertex == vertex) {
				char c = (char)interaction.strat;
				if(c=='c') {
					n++;
				}
			}
		}
		return n;
	}
	
	/*
	 * Rating recency function/method rate interactions based on how old they are,
	 * returns a value between 0 and 1 
	 */
 
	public double ratingRecency(MyVertex vertex,int numberOfIteractions) {
		double rating = 0;
		double ratingNoValues = 0;
		// WeightsValues stores weights multiplied by values 1 for cooperator -1 for defector
		ArrayList<Double> weightsValues = new ArrayList<Double>();
		// WeightsNoValues stores just weights.
		ArrayList<Double> weightsNoValues = new ArrayList<Double>();
		//Possible to change how effective memory is
		double lamdaNumerator = 10;
		double lamda = -(lamdaNumerator/(Math.log(0.5)));
		
		// Only considers interactions with v and iterate through iterations back to front i.e newest to oldest
		List<Interaction> lastInteractionsWithV = new ArrayList<Interaction>();
		// Only use last x interactions
		int useXInteractions = numberOfIteractions;
		for(int j = lastInteractions.size() - 1; j >= 0; j--) {
			if(useXInteractions<=0) { break; }
			Interaction interaction = lastInteractions.get(j);
			if(interaction.vertex == vertex) {
				lastInteractionsWithV.add(interaction);
				useXInteractions--;
			}
		}
		
		// Set min number of interactions for rating to take effect
		int minNumberIteractions = 20;
		if(lastInteractionsWithV.size()>minNumberIteractions) {
			for(Iterator<Interaction> iterator = lastInteractionsWithV.iterator(); iterator.hasNext();) {
				Interaction interaction = iterator.next();
				int timeDiff = lastInteractionsWithV.indexOf(interaction);
				double exp = -((timeDiff)/(lamda));
				double weight = Math.exp(exp);
				if(interaction.strat == 'c') {
					weightsValues.add(weight*1);
				} else {
					weightsValues.add(weight*-1);
				}
				weightsNoValues.add(weight);
				//System.out.println("Weight "+ weight+" timeDiff "+timeDiff+" strat "+interaction.strat);
			}
			for(double d : weightsValues) {
				rating = rating + d;
			}
			for(double d : weightsNoValues) {
				ratingNoValues = ratingNoValues + d;
			}
			rating = rating/ratingNoValues;
			// Normalise to [0,1] instead of [-1,1] 
			rating = (rating+1)/2;
		} else {
			// What to do when they first meet (or for the minNumberIteractions)
			rating = optimism;
		}
		return rating;
	}
	
	public boolean interactedBefore(MyVertex vertex) {
		if(getNumInteractionsWith(vertex)>0) {
			return true;
		} else {
			return false;
		}
	}
	
	public double getOptimism(double mean,double variance) {
		Random random = new Random();
		double opt = mean +random.nextGaussian() * variance;
		// Check to see if outside [0,1] range
		if(opt<0){
			// Limit to 0
			opt = 0;
		} else if(opt>1) {
			// Limit to 1 
			opt = 1;
		}
		optimism = opt;
		return opt;
	}
	
	public void setOptimism(double opt) {
		optimism = opt;
	}
	
	public double returnOptimism() {
		return optimism;
	}

	public String toString() {
		return "V"+id+" "+strat;
	}
}