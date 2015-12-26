package JCC3YP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import edu.uci.ics.jung.algorithms.cluster.VoltageClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/*
 * Author:	Josh Chacksfield
 * Date:	13-10-14
 * 
 * CooperationModel class was created so as to be able to 
 * reuse the code from it in any other models that are created.
 * Mainly contains methods that are used in conjunction with
 * the declared Graph<MyVertex, MyEdge> g.
 * */

public class CooperationModel {

	// Strategies
	final static char COOPERATOR = 'C';
	final static char DEFECTOR = 'D';
	final static double assignProb = 0.5;

	protected int numberOfNodes = 5000-2;

	// Values to establish optimisms
	double optMean = 0.5;
	double optVar = 0.1;

	protected Network network;
	protected int iterations;
	protected int alterType;
	protected String timeStarted;

	// Payoffs
	protected double r;// Mutual Cooperation
	protected double p;// Mutual Defection
	protected double s;// Sucker payoff
	protected double t;// Temptation payoff

	// Stats stores
	protected ArrayList<Double> degVars = new ArrayList<Double>();
	protected ArrayList<Double> pctCoop = new ArrayList<Double>();
	protected double pctCoopAvg = 0;
	protected Collection<Set<MyVertex>> clusters;

	public void changeR(double r) {
		this.r = r;
	}

	public void changeP(double p) {
		this.p = p;
	}

	public void changeS(double s) {
		this.s = s;
	}

	public void changeT(double t) {
		this.t = t;
	}

	// Assigns 'c' to nodes with assignProb otherwise 'd'
	public void assignStrategies(){
		// Assign strategies to vertices
		System.out.println("Assigning Strategies.");
		for (Iterator<MyVertex> iterator = network.graph.getVertices().iterator(); iterator.hasNext();) {
			MyVertex v = (MyVertex) iterator.next();
			v.assignStrat(assignProb);
		}
	}

	// Calculates case of interaction to return relavant pay-off
	public double getPayoffValue(MyVertex v1, MyVertex v2) {
		switch(v1.getStrat()) {
		case 'D': {
			switch(v2.getStrat()) {
			case 'D': return p;// p
			case 'C': return t;// t
			}
		}
		case 'C': {
			switch(v2.getStrat()) {
			case 'D': return s;// s
			case 'C': return r;// r
			}
		}
		}
		return 0;
	}

	// Statistical functions.
	/*public String countStrategies() {
		String s;
		int c = 0;
		int d = 0;
		for (Iterator<MyVertex> iterator = network.graph.getVertices().iterator(); iterator.hasNext();) {
			MyVertex v = (MyVertex) iterator.next();
			if(v.getStrat()==COOPERATOR) {
				c++;
			} else {
				d++;
			}
		}
		s = "C:" + c + " D:" + d;
		return s;
	}*/

	//Returns the pct of cooperators
	public double pctCooperators() {
		double n;
		double c = 0;
		double d = 0;
		for (Iterator<MyVertex> iterator = network.graph.getVertices().iterator(); iterator.hasNext();) {
			MyVertex v = (MyVertex) iterator.next();
			if(v.getStrat()=='C') {
				c++;
			} else {
				d++;
			}
		}
		n = c/(c+d);
		pctCoop.add(Double.valueOf(n));
		return n;
	}


	//Partially build clustering analysis methods
	public void getClusters() {
		System.out.println("Getting clusters...");
		Graph<MyVertex,MyEdge> graph = new SparseGraph<MyVertex, MyEdge>();
		for (MyVertex v : network.graph.getVertices())
			graph.addVertex(v);

		for (MyEdge e : network.graph.getEdges())
			graph.addEdge(e, network.graph.getIncidentVertices(e));

		int numClusters = 50;
		VoltageClusterer<MyVertex, MyEdge> clusterer = new VoltageClusterer<MyVertex, MyEdge>(graph,numClusters);
		clusters = clusterer.cluster(numClusters);
	}

	public void analyseClusters() {
		ArrayList<Double> avgCPerCluster = new ArrayList<Double>();
		Double avgOverAllClusters = 0.0;
		Double varianceOverAllClusters = 0.0;
		for(Iterator<Set<MyVertex>> iterator = clusters.iterator();iterator.hasNext();){
			Set<MyVertex> cluster = iterator.next();
			if(iterator.hasNext()==false) {
				break;
			}
			int c = 0;
			for(Iterator<MyVertex> iterator2 = cluster.iterator(); iterator2.hasNext();){
				MyVertex vertex = iterator2.next();
				if(vertex.getStrat()=='c') {
					c++;
				}
			}
			avgCPerCluster.add((double)c/(double)cluster.size());
			// Skip last cluster as it contains rest of nodes

		}
		avgOverAllClusters = Util.getMean(avgCPerCluster);
		varianceOverAllClusters = Util.getVariance(avgCPerCluster);
		try {
			Util.addToCsv(Double.toString(avgOverAllClusters),"TrustResults","AvgCoopClustersData");
			Util.addToCsv(Double.toString(varianceOverAllClusters),"TrustResults","VarianceClustersData");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void printClusters(Collection<Set<MyVertex>> clusters) {
		System.out.println("Printing clusters");
		System.out.println(clusters.size());
		for(Iterator<Set<MyVertex>> iterator = clusters.iterator(); iterator.hasNext();) {
			Set<MyVertex> cluster = iterator.next();
			System.out.println(cluster.size());
			System.out.println(cluster.toString());
		}
	}


	// ------ Graph editing methods ------

	// RR (Random removal and Random addition)
	public void editGraphRR() {
		VertexEdgeIdStack vEIS = editGraphRandomRemoval();
		int n = vEIS.getEdgeIdStack().size();

		// If n==0 then leave as a isolated node reassign a strategy.
		if(n!=0){
			editGraphRandomAdd(vEIS);
		}              
	}

	// RP (Random removal and Preferential addition)
	public void editGraphRP() {
		VertexEdgeIdStack vEIS = editGraphRandomRemoval();
		int n = vEIS.getEdgeIdStack().size();

		// If n==0 then leave as a isolated node reassign a strategy.
		if(n!=0){
			editGraphPrefAdd(vEIS);
		}
	}

	// TR (Targeted removal and Random addition)
	public void editGraphTR() {
		VertexEdgeIdStack vEIS = editGraphTargetRemoval();
		int n = vEIS.getEdgeIdStack().size();

		// If n==0 then leave as a isolated node reassign a strategy.
		if(n!=0){
			editGraphRandomAdd(vEIS);
		}
	}

	// TP (Targeted removal and Preferential addition)
	public void editGraphTP() {
		VertexEdgeIdStack vEIS = editGraphTargetRemoval();
		int n = vEIS.getEdgeIdStack().size();

		// If n==0 then leave as a isolated node reassign a strategy.
		if(n!=0){
			editGraphPrefAdd(vEIS);
		}
	}

	//Random Removal method
	public VertexEdgeIdStack editGraphRandomRemoval() {
		MyVertex v = Network.getRandomVertex(network.graph.getVertices());
		VertexEdgeIdStack vEIS = new VertexEdgeIdStack(v,network.removeEdges(v));
		v.assignStrat(assignProb);
		return vEIS;
	}

	//Targeted Removal method
	public VertexEdgeIdStack editGraphTargetRemoval() {
		int degree = 0;
		Vector<MyVertex> vertices = new Vector<MyVertex>();
		// Returns a vector of the vertices of greatest degree
		for(Iterator<MyVertex> iterator = network.graph.getVertices().iterator(); iterator.hasNext();) {
			MyVertex v = (MyVertex) iterator.next();
			int vDeg = network.graph.degree(v);
			if(vDeg > degree) {
				vertices.clear();
				vertices.add(v);
				degree = vDeg;
			} else if(vDeg == degree) {
				vertices.add(v);
			}
		}
		MyVertex v = Network.getRandomVertex(vertices);
		VertexEdgeIdStack vEIS = new VertexEdgeIdStack(v,network.removeEdges(v));
		v.assignStrat(assignProb);
		return vEIS;
	}

	//Random Addition method
	public void editGraphRandomAdd(VertexEdgeIdStack vEIS) {
		MyVertex v = vEIS.getVertex();
		Stack<Integer> edgeIdStack = vEIS.getEdgeIdStack();

		MyVertex u;
		Pair<MyVertex> vertexPair;
		MyEdge e;

		// See if degvar changes else delete
		for (int i =0; i<2;i++) {
			if (edgeIdStack.empty() == false) {
				List<MyVertex> vertices = new ArrayList<MyVertex>();
				vertices.addAll(network.graph.getVertices());      
				vertices.remove(v);
				// Check that no edge from v to u exists
				do {
					u = Network.getRandomVertex(vertices);
				} while (network.graph.findEdge(v,u)!=null);
				vertexPair = new Pair<MyVertex>(u,v);
				e = new MyEdge(edgeIdStack.pop());
				network.graph.addEdge(e,vertexPair,EdgeType.UNDIRECTED);
			}
		}
		/* "m < n, the rest of each n-m link is connected
		 * from a randomly selected node (referred to as
		 * source) to a randomly selected node (referred
		 * to as target)"*/
		// Add edges between two randomly selected nodes
		while(edgeIdStack.empty() == false) {
			List<MyVertex> vertices = new ArrayList<MyVertex>();
			vertices.addAll(network.graph.getVertices());      
			v = Network.getRandomVertex(vertices);
			vertices.remove(v);
			// Check that no edge from v to u exists
			do {
				u = Network.getRandomVertex(vertices);
			} while (network.graph.findEdge(v,u)!=null);
			vertexPair = new Pair<MyVertex>(u,v);
			e = new MyEdge(edgeIdStack.pop());
			network.graph.addEdge(e,vertexPair,EdgeType.UNDIRECTED);
		}
	}

	//Preferential Addition method
	public void editGraphPrefAdd(VertexEdgeIdStack vEIS) {
		MyVertex v = vEIS.getVertex();
		Stack<Integer> edgeIdStack = vEIS.getEdgeIdStack();
		Random randomGenerator = new Random();
		MyVertex u;
		Pair<MyVertex> vertexPair;
		MyEdge e;
		int m = 0;
		// Iterate through vertices finding pi and adding edges if necessary
		// remove if remove other
		Iterator<MyVertex> iterator =network.graph.getVertices().iterator();
		while(m<2){
			if(iterator.hasNext()){
				u = (MyVertex) iterator.next();
				if(network.graph.findEdge(v,u)==null) {
					// Add edge to all vertices based on probability pi=ki/Sumi ki
					double degree =network.graph.degree(u);
					double p = (double) (degree/(network.graph.getEdgeCount()*2));
					double rand = randomGenerator.nextDouble();
					if(edgeIdStack.empty() == false) {
						if(rand<=p) {
							vertexPair = new Pair<MyVertex>(u,v);
							e = new MyEdge(edgeIdStack.pop());
							network.graph.addEdge(e,vertexPair,EdgeType.UNDIRECTED);
							m++;
						}
					} else {
						break;
					}
				}
			} else {
				iterator =network.graph.getVertices().iterator();
			}
		}
		// If m<n then edgeIdStack wont be empty, continue till it is.
		while(edgeIdStack.empty() == false) {
			List<MyVertex> vertices = new ArrayList<MyVertex>();
			vertices.addAll(network.graph.getVertices());
			v = Network.getRandomVertex(vertices);
			vertices.remove(v);
			// Causes problems choosing a random vertex from an array of zero.
			// Until the edge doesn't exist.
			for(Iterator<MyVertex> iterator2 =network.graph.getVertices().iterator(); iterator2.hasNext();) {
				u = (MyVertex) iterator2.next();
				if (edgeIdStack.isEmpty()) {
					break;
				} else if (network.graph.findEdge(v,u)!=null) {
					continue;
				}
				vertexPair = new Pair<MyVertex>(u,v);
				double degree =network.graph.degree(u);
				double p = (double) (degree/(network.graph.getEdgeCount()*2));
				double rand = randomGenerator.nextDouble();
				if(rand<=p) {
					e = new MyEdge(edgeIdStack.pop());
					network.graph.addEdge(e,vertexPair,EdgeType.UNDIRECTED);
				}
			}
		}
	}

	// Generic graph utility functions used in conjunction with g


	/*
	//Used for early debugging
	//Displayed a graph in a circle to examine 
	public void displayCircleGraph() {
		Layout<MyVertex, MyEdge> layout = new CircleLayout<MyVertex, MyEdge>(g);
		layout.setSize(new Dimension(600, 600));
		BasicVisualizationServer<MyVertex, MyEdge> vv = new BasicVisualizationServer<MyVertex, MyEdge>(
				layout);
		vv.setPreferredSize(new Dimension(650, 650));
		JFrame frame = new JFrame("Circle Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	// Used for early debugging
	public void printIncidentEdges(MyVertex v) {
		for (Iterator<MyEdge> iterator = g.getIncidentEdges(v).iterator(); iterator
				.hasNext();) {
			MyEdge e = (MyEdge) iterator.next();
			System.out.println(e.toString());
		}
	}*/
}
