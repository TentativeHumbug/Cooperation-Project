package JCC3YP;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/*
 * Author:      Josh Chacksfield
 * Date:        13-10-14
 *
 * Uses CooperationModel as a base enabling the editing of the
 * class without making it useless for other models that maybe created.
 * Creates a starting graph as per the Barabasi-Albert Method.
 *
 * */

class IchinoseModel extends CooperationModel {

	public IchinoseModel(int num, int alterType) {
		this.alterType = alterType;
		this.numberOfNodes=num;

		// Starting payoffs for StagHunt Model can be altered to PD
		r = 2; 		// Mutual Cooperation 1
		p = 1; 		// Mutual Defection 0
		s = 0; 		// Sucker 0 
		t = 1;	// Temptation 1.5
	}

	//Run method
	public void run() {
		timeStarted = new SimpleDateFormat("yyyy-MM-dd/HH-mm-ss").format(new Date());
		double x = 0.1;
		// Change t over 10 steps from 1 to 3 in 0.2 increments
		for(double j = 1; j<=3;j=j+x) {
			t = j;
			// Generate starting graph
			network = new Network(numberOfNodes,this);
			assignStrategies();
			startingGenerations();
			try {
				Util.outToCsv(degVars,"IchinoseResults/"+timeStarted+"/DegreeData","alterType"+alterType);
			} catch (IOException e) {
				e.printStackTrace();
			}

			run1000Generations();
			String coopAvg = Double.toString(pctCoopAvg);
			try {
				Util.addToCsv(coopAvg,"IchinoseResults/"+timeStarted+"/AvgPctCoopData","alterType"+alterType);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void runAll() {
		for(int i = 1; i<=4;i++) {
			alterType = i%5;
			run();
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

	//Advancing a generation method
	public void advanceGeneration() {
		System.out.println("Generation "+network.generation+" :"+alterType);
		updatePayoffValues();
		updateStrategies();
		try {
			// Add to pctCooperators output file
			Util.addToCsv(Double.toString(pctCooperators()),"IchinoseResults/"+timeStarted+"/PctCoopData",String.valueOf(alterType)+"-"+String.valueOf(t));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Try to utilise a type of topological change
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

	//Get total payoff for each node
	public void updatePayoffValues() {
		for (Iterator<MyVertex> iterator =network.graph.getVertices().iterator(); iterator.hasNext();) {
			MyVertex v = (MyVertex) iterator.next();
			v.clearPayoff();
			for (Iterator<MyVertex> iterator2 =network.graph.getNeighbors(v).iterator(); iterator2.hasNext();) {
				MyVertex v2 = (MyVertex) iterator2.next();
				v.setPayoff(v.getPayoff() + getPayoffValue(v,v2));
			}
		}
	}
	
	//Update the strategies of all nodes 
	public void updateStrategies() {
		// Char Array to store changes to strategies so they can all be updated at once
		HashMap<MyVertex, Character> changeStratMap = new HashMap<MyVertex, Character>();

		// Compares Payoffs to random neighbour's editing strategy accordingly
		for (Iterator<MyVertex> iterator = network.graph.getVertices().iterator(); iterator.hasNext();) {
			MyVertex v = (MyVertex) iterator.next();
			if(network.graph.getNeighborCount(v) > 0) {
				MyVertex v2 = Network.getRandomVertex(network.graph.getNeighbors(v));              
				if(v.getPayoff()<v2.getPayoff()) {
					double prob = (v2.getPayoff()-v.getPayoff())/((t-s)*(Math.max(network.graph.degree(v),network.graph.degree(v2))));
					double number = Math.random();
					if(number<=prob) {
						changeStratMap.put(v,v2.getStrat());
					}
				}
			} 
		}

		//Update Strategies from changeStratMap
		for (Iterator<MyVertex> iterator =network.graph.getVertices().iterator(); iterator.hasNext();) {
			MyVertex v = (MyVertex) iterator.next();
			if(changeStratMap.get(v)!=null) {
				v.setStrat(changeStratMap.get(v));
			}
		}
	}
}