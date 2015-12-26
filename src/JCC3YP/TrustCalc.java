package JCC3YP;

import java.util.ArrayList;
import java.util.Iterator;
/*
 * Small class to calculate trust values given input of strategies
 * e.g. "c c c c c c c c c d d d d d d c" as command line arguments
 * would calculate trust values for that series of interactions
 * newest first.
 */
public class TrustCalc {
	static ArrayList<String> memory = new ArrayList<String>();
	//Enter lower case strategies 'c' or 'd' to calculate trust value assigned to that permutation
	public static void main(String[] args) {
		for(int i = 0; i < args.length;i++){
			memory.add(String.valueOf(args[i]));
		}
		System.out.println(args.length);
		System.out.println(trustValueCalc());
	}

	public static double trustValueCalc() {
		double rating = 0;
		double ratingNoValues = 0;
		// WeightsValues stores weights multiplied by values 1 for cooperator -1 for defector
		ArrayList<Double> weightsValues = new ArrayList<Double>();
		// WeightsNoValues stores just weights.
		ArrayList<Double> weightsNoValues = new ArrayList<Double>();
		//Possible to change how effective memory is
		double lamdaNumerator = 10;
		double lamda = -(lamdaNumerator/(Math.log(0.5)));


		// Set min number of interactions for rating to take effect
		for(Iterator<String> iterator = memory.iterator(); iterator.hasNext();) {
			String interaction = iterator.next();
			int timeDiff = memory.indexOf(interaction);
			double exp = -((timeDiff)/(lamda));
			double weight = Math.exp(exp);
			if(interaction.equals("c")) {
				weightsValues.add(weight*1);
			} else {
				weightsValues.add(weight*-1);
			}
			weightsNoValues.add(weight);
		}
		for(double d : weightsValues) {
			rating = rating + d;
		}
		for(double d : weightsNoValues) {
			ratingNoValues = ratingNoValues + d;
		}
		rating = rating/ratingNoValues;
		rating = (rating+1)/2;
		return rating;
	}
}
