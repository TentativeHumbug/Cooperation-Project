package JCC3YP;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class TrustModel extends CooperationModel {

	private double startT = 1;
	private int numberOfIteractions;
	private int graphEdgesToAttach;
	private boolean neutralThreshold = false;

	public TrustModel(int iterations,double optMean,int graphEdgesToAttach,int numberOfIteractions,int alterType) {
		// Eventually altering will be added to see stability
		this.iterations = iterations;
		this.optMean = optMean;
		this.graphEdgesToAttach = graphEdgesToAttach;
		this.numberOfIteractions = numberOfIteractions;
		this.alterType = alterType;

		// Starting payoffs for PD Model
		r = 1.0; // Mutual Cooperation
		p = 0.0; // Mutual Defection
		s = 0.0; // Sucker
		t = 1.5;// Temptation
	}

	public TrustModel(double startT,int alterType) {
		// Eventually altering will be added to see stability
		this.startT = startT;
		this.graphEdgesToAttach = 2;
		this.numberOfIteractions = 20;
		this.alterType = alterType;

		// Starting payoffs for PD Model
		r = 1.0; // Mutual Cooperation
		p = 0.0; // Mutual Defection
		s = 0.0; // Sucker
		t = 1.5;// Temptation

		// Generate starting graph

	}

	public void runCoopAnalysis() {
		timeStarted = new SimpleDateFormat("yyyy-MM-dd/HH-mm-ss").format(new Date());
		network = new Network(numberOfNodes,graphEdgesToAttach,graphEdgesToAttach,this);
		assignStrategies();
		for(int i = 1; i<=iterations;i++) {
			advanceGeneration();
		}
	}

	public void runTopologyAnalysis() {
		timeStarted = new SimpleDateFormat("yyyy-MM-dd/HH-mm-ss").format(new Date());
		double x = 0.1;
		// Change t over 10 steps from 1 to 3 in 0.2 increments
		for(double j = startT; j<=2.0;j=j+x) {
			int avgOver = 5;
			for(int k = 0; k<avgOver;k++) {
				t = j;
				network = new Network(4998,2,2,this);
				assignStrategies();
				startingGenerations();
				run1000Generations();
				String coopAvg = Double.toString(pctCoopAvg);
				try {
					Util.addToCsv(coopAvg,"TrustResults/AvgPctCoopData",String.valueOf(alterType)+"-"+String.valueOf(t)+"-"+optMean+"-"+numberOfIteractions);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void runAllTopologyAnalysis() {
		for(int i = alterType; i<=4;i++) {
			alterType = i%5;
			runTopologyAnalysis();
			startT = 1.0;
		}
	}

	// Method for settling the graph over 10000 generations
	public void startingGenerations() {
		for(int i = 0; i<10000;i++) {
			degVars.add(network.degreeVariance());
			advanceGeneration();
		}
	}

	// Define What to collect over 1000 generations after the initial 10,000
	public void run1000Generations() {
		pctCoopAvg = 0;
		// Collect data over so many generations
		for(int i = 0; i< 1000; i++) {
			pctCoop.add(pctCooperators());
			pctCoopAvg = pctCoopAvg + pctCooperators();
			advanceGeneration();
		}
		pctCoopAvg = pctCoopAvg/1000;
	}

	private void advanceGeneration() {
		System.out.println("Generation: "+network.generation);
		interactions();
		//Maybe include clustering analysis
		//getClusters();
		//analyseClusters();

		try {
			// Add to pctCooperators
			Util.addToCsv(Double.toString(pctCooperators()),"TrustResults/"+timeStarted+"/PctCoopData",String.valueOf(alterType)+"-"+String.valueOf(t)+"-"+optMean+"-"+numberOfIteractions);
		} catch (IOException e) {
			e.printStackTrace();
		}

		updateDispositions();
		try {
			switch (alterType) {
			case 0 : // Do nothing.
				break;
			case 1 : editGraphRR();
			break;
			case 2 : editGraphRP();
			break;
			case 3 : editGraphTR();
			break;
			case 4 : editGraphTP();
			break;
			default: throw new IOException();
			}
		} catch (IOException e) {
			System.out.println("Wrong Alter Type Error:"+ e);
		}
		network.generation++;
	}

	public void interactions() {
		// Should this be related to optimism
		double thresholdV1;
		double thresholdV2;
		for (Iterator<MyVertex> iterator = network.graph.getVertices().iterator(); iterator.hasNext();) {
			MyVertex v1 = (MyVertex) iterator.next();
			if(network.graph.getNeighborCount(v1) > 0) {
				// Interact with all neighbours or with a random one?
				//Select random  neighbour to interact with
				MyVertex v2 = Network.getRandomVertex(network.graph.getNeighbors(v1));

				// Returns v1's rating of interacting with v2
				double ratingV1  =	v1.ratingRecency(v2,numberOfIteractions);
				// Returns v2's rating of interacting with v1
				double ratingV2 =	v2.ratingRecency(v1,numberOfIteractions);

				if(neutralThreshold==true) {
					thresholdV1 = 0.5;
					thresholdV2 = 0.5;
				} else {
					thresholdV1 = 1.0 - v1.returnOptimism();
					thresholdV2 = 1.0 - v2.returnOptimism();
				}

				if(ratingV1>thresholdV1) {
					v1.setStrat('C');
				} else {
					v1.setStrat('D');
				}
				if(ratingV2>thresholdV2) {
					v2.setStrat('C');
				} else {
					v2.setStrat('D');
				}
				v1.addInteraction(v2);
				v2.addInteraction(v1);
				v1.setPayoff(v1.getPayoff() + getPayoffValue(v1,v2));
				v2.setPayoff(v2.getPayoff() + getPayoffValue(v2,v1));
			}
		}
	}

	public void updateDispositions() {
		double maxPayOff = 0;
		MyVertex maxPayOffVertex = null;
		for (Iterator<MyVertex> iterator = network.graph.getVertices().iterator(); iterator.hasNext();) {
			MyVertex v = (MyVertex) iterator.next();
			for (Iterator<MyVertex> iterator2 = network.graph.getNeighbors(v).iterator(); iterator2.hasNext();) {
				MyVertex v2 = (MyVertex) iterator2.next();
				if(v2.getPayoff()>maxPayOff) {
					maxPayOff = v2.getPayoff();
					maxPayOffVertex = v2;
				}
			}
			// Random kick
			if(Math.random()>0.9999) {
				System.out.println("Random Kick");
				MyVertex v2 = Network.getRandomVertex(network.graph.getNeighbors(v));
				if(v2==null) {
					break;
				}
				v.setOptimism(v.returnOptimism()+Math.copySign(0.01,v2.returnOptimism()-v.returnOptimism()));
			} else if(v.getPayoff()<maxPayOff) {
				v.setOptimism(v.returnOptimism()+Math.copySign(0.001,maxPayOffVertex.returnOptimism()-v.returnOptimism()));
			}
		}
	}
}