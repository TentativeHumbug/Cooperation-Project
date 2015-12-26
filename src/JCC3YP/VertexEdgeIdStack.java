package JCC3YP;

import java.util.Stack;

/* 
 * Made this object so two values could be passed back from 
 * editGraphMethods both the vertex and edges removed.
 * */
public class VertexEdgeIdStack {
	private MyVertex v;
	private Stack<Integer> edgeIdStack;
	
	public VertexEdgeIdStack(MyVertex v, Stack<Integer> edgeIdStack) {
		this.v = v;
		this.edgeIdStack = edgeIdStack;
	}
	
	public MyVertex getVertex() {
		return v;
	}
	
	public Stack<Integer> getEdgeIdStack() {
		return edgeIdStack;
	}
}
