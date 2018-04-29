package ThompsonsGroupV;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * CLASS NOT USED IN FINAL CONJUGACY CHECKING PROGRAM
 * Class to check whether two single-component Closed Abstract Strand Diagrams are isomorphic to one another
 * Assumes free loop sets of x an y are equivalent
 * Assumes x and y have the same number of vertices
 */
public class Isomorphism {

	ClosedAbstractStrandDiagram x;
	ClosedAbstractStrandDiagram y;
	Boolean isomorphic = false;
	
	HashMap<Vertex, Vertex> vertexMap;
	HashMap<Strand, Strand> strandMap;
	Node isomorphismFoundAt;
	
	public Isomorphism(ClosedAbstractStrandDiagram x, ClosedAbstractStrandDiagram y) {
		
		this.x = x;
		this.y = y;
		this.isomorphic = this.findIsomorphism();
		
	}
	
	public boolean areIsomorphic() {
		return isomorphic;
	}
	
	private boolean findIsomorphism() {
		
		
		if (x.getVertices().getLength() == y.getVertices().getLength()) {
			HashMap<Strand, Strand> strandBijection = new HashMap<Strand, Strand>();
			HashMap<Vertex, Vertex> vertexBijection = new HashMap<Vertex, Vertex>();
			Queue<Strand> strandsToProcess = new LinkedBlockingQueue<Strand>();
			Queue<Vertex> verticesToProcess = new LinkedBlockingQueue<Vertex>();
			Node fixedXNode  = x.getVertices().getFirstNode();
			Vertex fixedX = fixedXNode.getVertex(); //note that this vertex x is fixed for the entirety of the method
			Type fixedXType = fixedX.getType();
			Node yNode;
			if (isomorphic) {
				//isomorphism previously found, seeking a different isomorphism
				yNode = isomorphismFoundAt.getNext();
			}
			else {
				yNode = y.getVertices().getFirstNode();
			}
			int i = 0;
			Boolean keepGoing;
			Vertex v;
			Vertex w;
			while (i<y.getVertices().getLength()) {
				
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
						this.isomorphic = true;
						this.vertexMap = vertexBijection;
						this.strandMap = strandBijection;
						this.isomorphismFoundAt = yNode;
						//call to cohomology checker will happen here (instead of returning true)
						return true;
					}
					
				//if fixedX and fixedY do not have the same type, nothing happens - go to next iteration of loop
					
				}
				
				yNode = yNode.getNext();
				i++;
				
			}
			return false;
		}
		return false;

	}
	
}
