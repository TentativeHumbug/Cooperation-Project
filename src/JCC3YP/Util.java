package JCC3YP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
/*
 * Utility functions for out putting to files calculating means and variances.
 */
public class Util {

	private Util() {
		throw new AssertionError();
	}   

	public static Double getMean(ArrayList<Double> data) {
		Double sum = 0.0;
		for(Double a : data)
			sum += a;
		return sum/data.size();
	}

	public static Double getVariance(ArrayList<Double> data) {
		Double mean = getMean(data);
		Double temp = 0.0;
		for(Double a :data)
			temp += (mean-a)*(mean-a);
		return temp/data.size();
	}

	public static void outToCsv(ArrayList<Double> a,String folder,String file) throws IOException {
		File out = new File("./"+folder+"/"+file+".csv");
		File parent = out.getParentFile();
		if(!parent.exists() && !parent.mkdirs()){
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(out,true));		
		for(Double d : a)	{
			String s = d.toString() + ",";
			writer.write(s);
			writer.flush();
		}
		writer.close();
	}

	public static void outToCsv(Stack<String> s,String folder,String file) throws IOException {
		File out = new File("./"+folder+"/"+file+".csv");
		File parent = out.getParentFile();
		if(!parent.exists() && !parent.mkdirs()){
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(out,true));		
		writer.write(s.pop());
		while(0 < s.size()){
			writer.write(",");
			writer.write(s.pop());
		}
		writer.flush();
		writer.close();
	}

	public static void outToCsv(String s,String folder,String file) throws IOException {
		File out = new File("./"+folder+"/"+file+".csv");
		File parent = out.getParentFile();
		if(!parent.exists() && !parent.mkdirs()){
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(out,true));		
		writer.write(s);
		writer.flush();
		writer.close();
	}

	public static void addToCsv(String s,String folder,String file) throws IOException {
		File out = new File("./"+folder+"/"+file+".csv");
		File parent = out.getParentFile();
		if(!parent.exists() && !parent.mkdirs()){
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(out,true));		
		s = s +",";
		writer.append(s);
		writer.flush();
		writer.close();
	}
}