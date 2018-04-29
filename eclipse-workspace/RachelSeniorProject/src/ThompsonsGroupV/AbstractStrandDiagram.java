package ThompsonsGroupV;
import java.util.HashMap;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class AbstractStrandDiagram {

	private Vertex source;
	private Vertex sink;
	private DoubleLinkedList vertices;
	private int length;
	private Stack<Vertex> splitsToCheck;
	
	/*
	 * Constructor for an empty Abstract Strand Diagram
	 */
	public AbstractStrandDiagram() {
		length = 0;
	}
	
	/*
	 * Two constructors from a list of vertices
	 */
	public AbstractStrandDiagram(DoubleLinkedList vertices, Vertex source, Vertex sink) {
		this.vertices = vertices;
		length = vertices.getLength();
		this.source = source;
		this.sink = sink;
	}
	
	public AbstractStrandDiagram(DoubleLinkedList vertices) {
		this.vertices = vertices;
		length = vertices.getLength();
		int i = 0;
		Node node = vertices.getFirstNode();
		Vertex v;
		Boolean foundSink = false;
		Boolean foundSource = false;
		while (i<length-1 && !foundSink && !foundSource) {
			v = node.getVertex();
			if (v.getType().equals(Type.SOURCE)) {
				source = v;
				foundSource = true;
			}
			else if (v.getType().equals(Type.SINK)) {
				sink = v;
				foundSink = true;
			}
			i++;
			node = node.getNext();
		}
	}
	
	/*
	 * Return the sink of this diagram
	 */
	public Vertex getSink() {
		return sink;
	}
	
	/*
	 * Return the source of this diagram
	 */
	public Vertex getSource() {
		return source;
	}
	
	/*
	 * Return this diagram's list of vertices
	 */
	public DoubleLinkedList getVertices() {
		return vertices;
	}
	
	/*
	 * Close this diagram and 
	 * return a closed abstract strand diagram
	 */
	public ClosedAbstractStrandDiagram close() {
		return new ClosedAbstractStrandDiagram(this);
	}
	
	/*
	 * Delete a vertex from this diagram
	 */
	public void deleteVertex(Vertex v) {
		Node node = v.getNode();
		vertices.delete(node);
		v.delete();
		length--;
	}
	
	/*
	 * Compose this diagram with f
	 * Currently modifies f and g 
	 * Since there is no "copy" function for strand diagrams
	 */
	public AbstractStrandDiagram multiply (AbstractStrandDiagram f) {
		Vertex fSink = f.getSink();
		Strand connectingStrand = fSink.getStrand(Role.PARENT);
		Vertex fBottom = connectingStrand.getBeginVertex();
		
		Vertex gSource = this.getSource();
		Vertex gTop = gSource.getStrand(Role.CHILD).getEndVertex();
		this.deleteVertex(gSource);
		f.deleteVertex(fSink);
		
		fBottom.setEndVertex(connectingStrand, gTop, Role.PARENT);
		
		DoubleLinkedList fVertices = f.getVertices();
		DoubleLinkedList gVertices = this.getVertices();
		fVertices.append(gVertices);
		return new AbstractStrandDiagram(fVertices, f.getSource(), this.getSink());	
	}
	
	/*
	 * Return a stack of all of this diagram's split vertices
	 * Used to check for reductions
	 */
	public static Stack<Vertex> getSplitsToCheck(DoubleLinkedList vertices){
		Stack<Vertex> splitsToCheck = new Stack<Vertex>();
		int i = 0;
		Node current = vertices.getFirstNode();
		int length = vertices.getLength();
		while (i<length) {
			Vertex v = current.getVertex();
			if (v.getType().equals(Type.SPLIT)) {
				splitsToCheck.push(v);
				v.setInStack(true);
			}
			current = current.getNext();
			i++;
		}
		return splitsToCheck;
	}
	
	/*
	 * Reduce this diagram
	 */
	public AbstractStrandDiagram reduce() {
		this.splitsToCheck = getSplitsToCheck(this.vertices);
		while (!splitsToCheck.isEmpty()) {
			Vertex split = splitsToCheck.pop();
			//If vertex has been deleted from strand diagram it may still be in the stack, we should not look for reductions in this case
			if (split.hasBeenDeleted()) {
				continue;
			}
			else {
				split.setInStack(false);
				
				Strand lchildStrand = split.getStrand(Role.LCHILD);
				Strand rchildStrand = split.getStrand(Role.RCHILD);
				
				Vertex lchildEnd = split.getOtherVertex(lchildStrand);
				Vertex rchildEnd = split.getOtherVertex(rchildStrand);
				
				Role lchildEndRole = lchildStrand.getEndRole();
				Role rchildEndRole = rchildStrand.getEndRole();
				
				Strand splitParentStrand = split.getStrand(Role.PARENT);
				Vertex splitParent = split.getOtherVertex(splitParentStrand);

				if (isReductionI(lchildEnd, rchildEnd, lchildEndRole, rchildEndRole)) {
					reductionI(split, splitParent, splitParentStrand, lchildEnd, lchildStrand, rchildEnd, rchildStrand, 
							lchildEndRole, rchildEndRole);
					continue;
				}
				else if (isReductionII(splitParent))  {
					reductionII(split, splitParent, splitParentStrand, lchildEnd, lchildStrand, rchildEnd, rchildStrand, 
							lchildEndRole, rchildEndRole);
					continue;
				}
			}
		}
		return this;	
	}
	
	/*
	 * Check to see if requirements to perform a type I reduction are satisfied
	 */
	public boolean isReductionI(Vertex lchildEnd, Vertex rchildEnd, Role lchildEndRole, Role rchildEndRole) {
		if (lchildEnd.equals(rchildEnd)
				&& lchildEndRole.equals(Role.LPARENT)
				&& rchildEndRole.equals(Role.RPARENT)) return true;
		else return false;
	}
	
	/*
	 * Check to see if requirements to perform a type II reduction are satisfied
	 */
	public boolean isReductionII(Vertex splitParent) {
		if (splitParent.getType().equals(Type.MERGE)) return true;
		else return false;
	}
	
	/*
	 * Type I reduction helper method
	 */
	public void reductionI(Vertex split, Vertex splitParent, Strand splitParentStrand, Vertex lchildEnd, Strand lchildStrand, 
			Vertex rchildEnd, Strand rchildStrand, Role lchildEndRole, Role rchildEndRole) {
		
			Vertex merge = lchildEnd;
			Strand mergeChildStrand = merge.getStrand(Role.CHILD);
			Vertex mergeChild = merge.getOtherVertex(mergeChildStrand);
			Role newEndRole = mergeChildStrand.getEndRole();
			
			splitParent.setEndVertex(splitParentStrand, mergeChild, newEndRole);
			//Delete vertices
			split.hasBeenDeleted(true);
			merge.hasBeenDeleted(true);
			deleteVertex(split);
			deleteVertex(merge);
			
			//Delete strands
			lchildStrand.delete();
			rchildStrand.delete();
			mergeChildStrand.delete();
			
			//Add any neighboring splits to the stack in case of newly created reduction cases 	
			if (splitParent.getType().equals(Type.SPLIT)) {
				if (!splitParent.isInStack()) {
					splitsToCheck.push(splitParent);	
					splitParent.setInStack(true);
				}
			}
			if (mergeChild.getType().equals(Type.SPLIT)) {
				if (!mergeChild.isInStack()) {
					splitsToCheck.push(mergeChild);
					mergeChild.setInStack(true);
				}
			}	
	}
	
	/*
	 * Type II reduction helper method
	 */
	public void reductionII(Vertex split, Vertex splitParent, Strand splitParentStrand, Vertex lchild, Strand lchildStrand, 
			Vertex rchild, Strand rchildStrand, Role lchildEndRole, Role rchildEndRole) {
			
			Vertex merge = splitParent;
			Strand lparentStrand = merge.getStrand(Role.LPARENT);
			Strand rparentStrand = merge.getStrand(Role.RPARENT);
			Vertex lparent = merge.getOtherVertex(lparentStrand);
			Vertex rparent = merge.getOtherVertex(rparentStrand);
						
			Role newLeftRole = lchildStrand.getEndRole();
			Role newRightRole = rchildStrand.getEndRole();

			lparent.setEndVertex(lparentStrand, lchild, newLeftRole);
			rparent.setEndVertex(rparentStrand, rchild, newRightRole);
			
			//Delete vertices
			split.hasBeenDeleted(true);
			merge.hasBeenDeleted(true);
			deleteVertex(merge);
			deleteVertex(split);
			
			//Delete strands
			lchildStrand.delete();
			rchildStrand.delete();
			splitParentStrand.delete();
			
			//Add any neighboring splits to the stack in case of newly created reduction cases
			if (lparent.getType().equals(Type.SPLIT)) {
				splitsToCheck.push(lparent);
				lparent.setInStack(true);
			}
			if (rparent.getType().equals(Type.SPLIT)) {
				splitsToCheck.push(rparent);
				rparent.setInStack(true);
			}
			if (lchild.getType().equals(Type.SPLIT)) {
				splitsToCheck.push(lchild);
				lchild.setInStack(true);
			}
			if (rchild.getType().equals(Type.SPLIT)) {
				splitsToCheck.push(rchild);
				rchild.setInStack(true);
			}
		
	}
	
	/*
	 * Used in debugging
	 */
	public String toString() {
		StringBuilder r = new StringBuilder("BEGIN ABSTRACT STRAND DIAGRAM");
		r.append(System.getProperty("line.separator"));
		r.append(System.getProperty("line.separator"));
		int i = 0;
		Node node = vertices.getFirstNode();
		
		while(i<length) {
			r.append("Vertex " + i);
			r.append(System.getProperty("line.separator"));
			r.append(node.getVertex().toString());
			r.append(System.getProperty("line.separator"));
			node = node.getNext();
			i++;
		}
		
		r.append("END OF ABSTRACT STRAND DIAGRAM");

		return r.toString();
	}
	
	public boolean equals(AbstractStrandDiagram other) {
		HashMap<Strand, Strand> strandBijection = new HashMap<Strand, Strand>();
		HashMap<Vertex, Vertex> vertexBijection = new HashMap<Vertex, Vertex>();
		Queue<Strand> strandsToProcess = new LinkedBlockingQueue<Strand>();
		Queue<Vertex> verticesToProcess = new LinkedBlockingQueue<Vertex>();
		Node fixedXNode  = this.getVertices().getFirstNode();
		Vertex fixedX = fixedXNode.getVertex(); //note that this vertex x is fixed for the entirety of the method
		Type fixedXType = fixedX.getType();
		Node yNode = other.getVertices().getFirstNode();
		int i = 0;
		Boolean keepGoing;
		Vertex v;
		Vertex w;
		while (i<other.getVertices().getLength()) {
			
			Vertex fixedY = yNode.getVertex(); //note that this vertex y is fixed only within this iteration of the while loop
			Type fixedYType = fixedY.getType();
			if (fixedXType.equals(fixedYType)) {
				
				//if fixedX and fixedY have the same type then we can start constructing an isomorphism
				fixedX.mark();
				fixedY.mark();
				verticesToProcess.add(fixedX);
				vertexBijection.put(fixedX, fixedY);
				keepGoing = true;
				while (keepGoing && (!verticesToProcess.isEmpty() || !strandsToProcess.isEmpty())) {
					
					if (!verticesToProcess.isEmpty()) {
						//Process a vertex
						v = verticesToProcess.remove();
						w = vertexBijection.get(v);
						for (Strand s: v.getStrands().values()) {
							Role r = s.getRole(v);
							Strand t = w.getStrand(r);
							if (s.isMarked() && t.isMarked()) {
								//the following should be an unnecessary check, 
								//since once s and t are marked they will be added to the bijection... remove?
								 if (strandBijection.containsKey(s)) {
									if (strandBijection.get(s).equals(t)) {
										//this is what we want
										continue;
									}
									else {
										//this isomorphism doesn't work
										keepGoing = false;
										break;
									}
								}
								 else {
									 //this should be unreachable...
									 //raise alarm! create exception
								 }
							}
							else if ( !s.isMarked() && !t.isMarked() ){
								s.mark();
								t.mark();
								strandsToProcess.add(s);
								strandBijection.put(s, t);
							}
							else {
								//if s is marked but not t, or vice versa, then
								//this isomorphism doesn't work
								keepGoing = false;
								break;
							}
							
						}
					}
					
					if (!strandsToProcess.isEmpty()) {
						//Process a strand
						Strand s = strandsToProcess.remove();
						Strand t = strandBijection.get(s);
						Vertex sBegin = s.getBeginVertex();
						Vertex sEnd = s.getEndVertex();
						Vertex tBegin = t.getBeginVertex();
						Vertex tEnd = t.getEndVertex();
						if (sBegin.isMarked() && tBegin.isMarked()) {
							if (vertexBijection.containsKey(sBegin)) {
								if (vertexBijection.get(sBegin).equals(tBegin)) {
									//this is what we want
								}
								else {
									//this isomorphism doesn't work
									keepGoing = false;
									break;
								}
							}
							else {
								//again, shouldn't be necessary..
								//raise the alarm! create exception
							}
						}
						else if (!sBegin.isMarked() && !tBegin.isMarked()) {
							if ( (sBegin.getType().equals(tBegin.getType()) )
									&& (s.getRole(sBegin).equals(t.getRole(tBegin))) ) {
								sBegin.mark();
								tBegin.mark();
								vertexBijection.put(sBegin, tBegin);
								verticesToProcess.add(sBegin);
							}
							else {
								//this isomorphism doesn't work
								keepGoing = false;
								break;
							}
							
							
						}
						else {
							keepGoing = false;
							break;
						}
						
						if (sEnd.isMarked() && tEnd.isMarked()) {
							if (vertexBijection.containsKey(sEnd)) {
								if (vertexBijection.get(sEnd).equals(tEnd)) {
									//this is what we want
								}
								else {
									//this isomorphism doesn't work
									keepGoing = false;
									break;
								}
							}
							else {
								//shouldn't be necessary and should be deleted
								vertexBijection.put(sEnd, tEnd);
							}
						}
						else if (!sEnd.isMarked() && !tEnd.isMarked()) {
							if ( (sEnd.getType().equals(sBegin.getType()) )
									&& (s.getRole(sEnd).equals(t.getRole(tEnd))) ) {
								sEnd.mark();
								tEnd.mark();
								vertexBijection.put(sEnd, tEnd);
								verticesToProcess.add(sEnd);
							}
							
						}
						else {
							keepGoing = false;
							break;
						}
					}
				}
				if (keepGoing) {
					//Isomorphism did not fail! i.e. isomorphism found
					return true;
				}
				
			//if fixedX and fixedY do not have the same type, nothing happens - go to next iteration of loop
				
			}
			
			yNode = yNode.getNext();
			i++;
			
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		

	}
	
}
