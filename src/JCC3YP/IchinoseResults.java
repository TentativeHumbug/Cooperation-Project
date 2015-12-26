package JCC3YP;

public class IchinoseResults {
	// Option variables and defaults TODO explain what these do
	private static int avgOver;
	private static int numberOfNodes = 5000-2;
	private static int alterType;

	public static void main(String[] args) {
		if(args.length==0) {
			System.err.println("At least alterType is required.");
			return;
		} else if(args[0].equals("help")||args[0].equals("Help")||args[0].equals("?")) {
			System.out.println("Help");
			return;
		} else {
			if(args[0].equals("_")) {
				alterType  = 0;
			} else {
				alterType	= Integer.valueOf(args[0]);
			}
		}

		IchinoseModel currentModel = new IchinoseModel(numberOfNodes,alterType);
		currentModel.runAll();
	}
}


/*public void oldMain(String[] args) {
		System.out.println("Start Simulations");
		// Models for each type of topological change
		int avgOver = 5;
		for(int i = 1; i<=avgOver;i++) {
			PrisonerDilemaModel modelRR = new PrisonerDilemaModel(5000-2,1);
			collectDegreeData(modelRR,"RR"+i);
			modelRR = null;
		}
		for(int i = 1; i<=avgOver;i++) {
			PrisonerDilemaModel modelRP = new PrisonerDilemaModel(5000-2,2);
			collectDegreeData(modelRP,"RP"+i);
			modelRP = null;
		}
		for(int i = 1; i<=avgOver;i++) {
			PrisonerDilemaModel modelTR = new PrisonerDilemaModel(5000-2,3);
			collectDegreeData(modelTR,"TR"+i);
			modelTR = null;
		}
		for(int i = 1; i<=avgOver;i++) {
			PrisonerDilemaModel modelTP = new PrisonerDilemaModel(5000-2,4);
			collectDegreeData(modelTP,"TP"+i);
			modelTP = null;
		}

		// Collect info on % of cooperators
		int avgOver2 = 5;
		for(int i = 1; i<=avgOver2;i++) {
			// 20 steps from 1 to 3 in 0.1 increments
			for(double t = 1; t<=3;t=t+0.1) {
				PrisonerDilemaModel modelRR = new PrisonerDilemaModel(5000-2,1,t);
				collectPctCoopData(modelRR,"RR"+i);
				modelRR = null;
			}
			for(double t = 1; t<=3;t=t+0.1) {
				PrisonerDilemaModel modelRP = new PrisonerDilemaModel(5000-2,2,t);
				collectPctCoopData(modelRP,"RP"+i);
				modelRP = null;
			}
			for(double t = 1; t<=3;t=t+0.1) {
				PrisonerDilemaModel modelTR = new PrisonerDilemaModel(5000-2,3,t);
				collectPctCoopData(modelTR,"TR"+i);
				modelTR = null;
			}
			for(double t = 1; t<=3;t=t+0.1) {
				PrisonerDilemaModel modelTP = new PrisonerDilemaModel(5000-2,4,t);
				collectPctCoopData(modelTP,"TP"+i);
				modelTP = null;
			}
		}
	}*/