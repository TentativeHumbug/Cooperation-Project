package JCC3YP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

/*
 * Author:	Josh Chacksfield
 * Date:	1-2-15
 * 
 * Reworked Project to include a Network object
 * Contains graph and related methods
 * */

public class Network {

	protected CooperationModel model;
	// Seed from System Time
	protected int seed;
	// Graph object
	protected SparseGraph<MyVertex, MyEdge> graph;
	protected int generation = 1;

	protected static int edgeCount;
	protected static int vertexCount;

	// Defines seed and generates graph
	public Network(int num,CooperationModel model){
		this.model = model;
		edgeCount = 0;
		vertexCount = 0;
		seed = (int) System.currentTimeMillis();
		graph = generateGraph(seed, num);
	}
	
	public Network(int num,int startingVertices,int edgesToAttach,CooperationModel model){
		this.model = model;
		edgeCount = 0;
		vertexCount = 0;
		seed = (int) System.currentTimeMillis();
		graph = generateGraph(seed, num,startingVertices,edgesToAttach);
	}

	public int getEdgeCount() {
		return edgeCount;
	}

	public void setEdgeCount(int edgeCount) {
		Network.edgeCount = edgeCount;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void setVertexCount(int vertexCount) {
		Network.vertexCount = vertexCount;
	}

	public static int incVertexCnt() {
		return vertexCount++;
	}

	public static int incEdgeCnt() {
		return edgeCount++;
	}

	//Factories for BarabasiAlbert Generator
	private Factory<Graph<MyVertex, MyEdge>> graphFactory = new Factory<Graph<MyVertex, MyEdge>>() {
		public Graph<MyVertex, MyEdge> create() {
			return new SparseGraph<MyVertex, MyEdge>();
		}
	};

	private Factory<MyVertex> vertexFactory = new Factory<MyVertex>() {
		public MyVertex create() {
			return new MyVertex(model.optMean,model.optVar);
		}
	};

	private Factory<MyEdge> edgeFactory = new Factory<MyEdge>() {
		public MyEdge create() {
			
			return new MyEdge();
		}
	};
	
	// Method constructs starting graph using BarabasiAlbert Method
	private SparseGraph<MyVertex,MyEdge> generateGraph(int seed,int n) {
		Set<MyVertex> seedMap = new HashSet<MyVertex>();

		BarabasiAlbertGenerator<MyVertex,MyEdge> generator =
				new BarabasiAlbertGenerator<MyVertex,MyEdge>(graphFactory, vertexFactory, edgeFactory,2, 2,seed, seedMap);

		System.out.println("Evolving Graph Started. Seed: "+seed);
		generator.evolveGraph(n);
		System.out.println("Evolving Graph Finished. " + generator.numIterations());
		SparseGraph<MyVertex,MyEdge> graph =  (SparseGraph<MyVertex, MyEdge>) generator.create();
		return graph;
	}
	
	private SparseGraph<MyVertex,MyEdge> generateGraph(int seed,int n,int startingVertices,int edgesToAttach) {
		Set<MyVertex> seedMap = new HashSet<MyVertex>();

		BarabasiAlbertGenerator<MyVertex,MyEdge> generator =
				new BarabasiAlbertGenerator<MyVertex,MyEdge>(graphFactory, vertexFactory, edgeFactory,startingVertices, edgesToAttach,seed, seedMap);

		System.out.println("Evolving Graph Started. Seed: "+seed);
		generator.evolveGraph(n);
		System.out.println("Evolving Graph Finished. " + generator.numIterations());
		SparseGraph<MyVertex,MyEdge> graph =  (SparseGraph<MyVertex, MyEdge>) generator.create();
		return graph;
	}

	// Graph editing functions
	// Add random edges back to max edge number defined during generation
	public void addRandomEdgesFromIds(MyVertex v, Stack<Integer> ids) {
		while (0 < ids.size()) {
			List<MyVertex> list = new ArrayList<MyVertex>();
			list.addAll(graph.getVertices());
			list.remove(v);

			MyVertex v2 = Network.getRandomVertex(list);
			while (graph.containsEdge(graph.findEdge(v, v2)) == true) {
				v2 = Network.getRandomVertex(list);
			}
			MyEdge e = new MyEdge(ids.pop());
			graph.addEdge(e, v, v2);
		}
	}
	
	public Stack<Integer> removeEdges(MyVertex v) {
		Stack<Integer> edgeIdStack = new Stack<Integer>();
		for (Iterator<MyEdge> iterator = graph.getIncidentEdges(v).iterator(); iterator
				.hasNext();) {
			MyEdge e = (MyEdge) iterator.next();
			edgeIdStack.push(e.getId());
			graph.removeEdge(e);
		}
		return edgeIdStack;
	}

	// Returns a random vertex from a collection
	public static MyVertex getRandomVertex(Collection<MyVertex> c) {
		if(c.size()==0){
			return null;
		}
		Random randomGenerator = new Random();
		MyVertex[] vertexArray = c.toArray(new MyVertex[c.size()]);
		int rand = randomGenerator.nextInt(vertexArray.length);
		return vertexArray[rand];
	}

	// Stats methods for the layout of the graph
	public double degreeVariance() {
		int n = getVertexCount();
		double kbar = 0;
		double sigk2 = 0;
		for (Iterator<MyVertex> iterator = graph.getVertices().iterator(); iterator.hasNext();) {
			MyVertex v = (MyVertex) iterator.next();
			int degree = graph.degree(v);
			kbar = kbar + degree;
			sigk2 = sigk2 + (double) Math.pow(degree, 2);
		}
		kbar = kbar / n;
		sigk2 = sigk2 / n;
		double degVar = (sigk2 - Math.pow(kbar, 2)) / kbar;
		return degVar;
	}

	//Returns the degree distribution
	public HashMap<Integer, Integer> getDegreeDist() {
		HashMap<Integer, Integer> degDist = new HashMap<Integer, Integer>();
		for (Iterator<MyVertex> iterator = graph.getVertices().iterator(); iterator
				.hasNext();) {
			MyVertex v = (MyVertex) iterator.next();
			int n = graph.degree(v);
			if (degDist.containsKey(n)) {
				degDist.put(n, degDist.get(n) + 1);
			} else {
				degDist.put(n, 1);
			}
		}
		return degDist;
	}
	
	public SparseGraph<MyVertex,MyEdge> getGraph() {
		return graph;
	}

	public int getSeed() {
		return seed;
	}
}
