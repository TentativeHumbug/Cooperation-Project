package JCC3YP;
/*
 * Class to run Trust models
 * take command line input for both topological change 
 * and investigating varying dispositions etc.
 * depends on number of command line inputs
 */
public class TrustResults {
	private static String timeStarted;
	// Option variables and defaults TODO explain what these do
	private static int alterType;
	private static double startT = 1;
	private static double optMeanMin;
	private static double optMeanMax;
	private static double optMeanStep;
	private static int iterations;
	private static int numberOfEdgesAdded;
	private static int numberOfEdgesAddedStep;
	private static int numberOfNodes;
	private static int minNumberInteractions;
	private static int maxNumberInteractions;
	private static int numberInteractionsStep;

	public static void main(String[] args) {
		// Example Runs:
		// 1 1.0
		// 0.45 0.45 0.01 2 2 80 120 20 false
		if(args.length==0) {
			System.out.println("Include options <optMeanMin> <optMeanMax> <optMeanStep> <numberOfEdgesAdded> <numberOfEdgesAddedStep> <minNumberInteractions> <maxNumberInteractions> <numberInteractionsStep> <neutralThreshold>");
			System.out.println("Running topology analysis!");
		}else if(args.length==1) {
			if(args[0].equals("_")) {
				alterType  = 1;
			} else {
				alterType	= Integer.valueOf(args[0]);
			}
		} else if(args.length==2) {
			if(args[0].equals("_")) {
				alterType  = 1;
			} else {
				alterType	= Integer.valueOf(args[0]);
			}

			if(args[1].equals("_")) {
				startT  = 1.0;
			} else {
				startT	= Double.valueOf(args[1]);
			}
		} else if(args.length==3) {
			if(args[0].equals("_")) {
				alterType  = 1;
			} else {
				alterType	= Integer.valueOf(args[0]);
			}

			if(args[1].equals("_")) {
				startT  = 1.0;
			} else {
				startT	= Double.valueOf(args[1]);
			}

			if(args[2].equals("_")) {
				optMeanMin  = 0.1;
			} else {
				optMeanMin	= Double.valueOf(args[2]);
			}
		} else {
			if(args[0].equals("default")){
				// Need to update
				optMeanMin  	= 0.1;
				optMeanMax  	= 0.9;
				optMeanStep 	= 0.05;
				iterations 		= 2000;
			} else {
				iterations 		= 2000;
				minNumberInteractions = 20;
				maxNumberInteractions = 20;
				numberInteractionsStep = 1;

				if(args[0].equals("_")) {
					optMeanMin  = 0.1;
				} else {
					optMeanMin	= Double.valueOf(args[0]);
				}

				if(args[1].equals("_")) {
					optMeanMax  = 0.9;
				} else {
					optMeanMax	= Double.valueOf(args[1]);
				}

				if(args[2].equals("_")) {
					optMeanStep  = 0.1;
				} else {
					optMeanStep	= Double.valueOf(args[2]);
				}

				if(args[3].equals("_")) {
					numberOfEdgesAdded  =  2;
				} else {
					numberOfEdgesAdded	= Integer.valueOf(args[3]);
				}

				if(args[4].equals("_")) {
					numberOfEdgesAddedStep  = 0;
				} else {
					numberOfEdgesAddedStep	= Integer.valueOf(args[4]);
				}

				if(args[5].equals("_")) {
					minNumberInteractions  = 20;
				} else {
					minNumberInteractions	= Integer.valueOf(args[5]);
				}

				if(args[6].equals("_")) {
					maxNumberInteractions  = 20;
				} else {
					maxNumberInteractions	= Integer.valueOf(args[6]);
				}

				if(args[7].equals("_")) {
					numberInteractionsStep  = 1;
				} else {
					numberInteractionsStep	= Integer.valueOf(args[7]);
				}

				if(args[8].equals("_")) {
					alterType  = 0;
				} else {
					alterType	= Integer.valueOf(args[8]);
				}
			}
		}
		if(args.length>1&&args.length<4){
			TrustModel currentModel = new TrustModel(startT,alterType);
			currentModel.optMean = optMeanMin;
			currentModel.runAllTopologyAnalysis();
		} else if(args.length>4) {
			//Switch this to do coopPct analysis
			TrustModel model; 
			int avgOver = 3;
			// Change mean optimism value
			for(double m = optMeanMin; m<=optMeanMax;m=m+optMeanStep){
				for(int n = 0; n<=numberOfEdgesAddedStep; n++){
					for(int o = minNumberInteractions; o<=maxNumberInteractions;o=o+numberInteractionsStep){
						for(int avg = avgOver;avg>0;avg--) {
							System.out.printf("CREATING MODEL\nNumberNodes %d\nIterations %d\nOptMean %f\nNumberOfEdgesAdded %d\nNeutralThreshold %b\nAlterType %d\nTimeStarted %s\n",numberOfNodes,iterations,m,numberOfEdgesAdded+n,false,alterType,timeStarted);
							model = new TrustModel(iterations,m,numberOfEdgesAdded+n,o,alterType);
							model.runCoopAnalysis();
							model = null;
						}
					}
				}
			}
		}
	}

}