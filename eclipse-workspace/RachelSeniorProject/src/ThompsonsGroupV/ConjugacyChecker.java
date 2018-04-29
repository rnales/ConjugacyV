package ThompsonsGroupV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import Jama.Matrix;

public class ConjugacyChecker {
	
	TreeDiagram a;
	TreeDiagram b;
	ClosedAbstractStrandDiagram A;
	ClosedAbstractStrandDiagram B;
	LinkedList<ClosedAbstractStrandDiagram> aComponents;
	LinkedList<ClosedAbstractStrandDiagram> bComponents;
	int numComponents;
	LinkedHashMap<ClosedAbstractStrandDiagram, Integer> aComponentsSizes;
	LinkedHashMap<ClosedAbstractStrandDiagram, Integer> bComponentsSizes;
	int i;
	int j;
	Boolean keepConstructingIsomorphism;
	
	public ConjugacyChecker() {
		
	}
	
	public ConjugacyChecker(ArrayList<BinarySequence> aDomain, ArrayList<BinarySequence> aRange, ArrayList<Integer> aPerm, 
			ArrayList<BinarySequence> bDomain, ArrayList<BinarySequence> bRange, ArrayList<Integer> bPerm) {
		
		this.a = new TreeDiagram(aDomain, aRange, aPerm);
		this.b = new TreeDiagram(bDomain, bRange, bPerm);
	
	}
	
	public ConjugacyChecker(TreeDiagram a, TreeDiagram b) {
		this.a = a;
		this.b = b;
		
		
	}
	
	public ConjugacyChecker(String aString, String bString) {
		this.a = parseWord(aString);
		this.b = parseWord(bString);
	}
	
	public TreeDiagram parseWord(String word) {
		int length = word.length();
		String s;
		TreeDiagram temp;
		TreeDiagram t = TreeDiagram.getGenerator(word.charAt(0) + "");
		int i = 1;
		while (i<length) {
			s = word.charAt(i) + "";
			temp = TreeDiagram.getGenerator(s);
			t = temp.multiply(t);
			i++;
		}
		return t;
	}
	
	public boolean areConjugate() { //n = number of vertices
		A = a.convertToStrandDiagram().close(); //quadratic on n
		B = b.convertToStrandDiagram().close(); //quadratic on n
		
		A.reduce(); //quadratic on n
		B.reduce(); //quadratic on n
		

		/* If A and B do not have the same number of vertices then they are not conjugate */
		if (A.size() != B.size()) { //constant
			return false;
		}
		
		/* If A and B do not have the same sets of free loops then they are not conjugate */
		if ( !(A.getFreeLoops().equals( B.getFreeLoops() )) ){ //linear on n/2
			return false;
		}
		

		/* Check if diagrams contain ONLY free loops - if so, they are conjugate, done! */
		if (A.size() == 0) { //constant
			return true;
		}
		
		aComponents = A.getComponents(); //quadratic on n
		bComponents = B.getComponents(); //quadratic on n
		numComponents = aComponents.size(); //constant
		
		/* If A and B do not have the same number of components then they are not conjugate */
		if (numComponents != bComponents.size()) { //constant
			return false;
		}
		
		
		i = 0;
		aComponentsSizes = new LinkedHashMap<ClosedAbstractStrandDiagram, Integer>();
		bComponentsSizes = new LinkedHashMap<ClosedAbstractStrandDiagram, Integer>();
		
		while (i<numComponents) {
			ClosedAbstractStrandDiagram a = aComponents.get(i); //linear on n
			ClosedAbstractStrandDiagram b = bComponents.get(i); //linear on n
			aComponentsSizes.put(a, a.size()); //linear on n
			bComponentsSizes.put(b, b.size()); //linear on n
			i++;
		}
		
		LinkedHashMap<ClosedAbstractStrandDiagram, Integer> aComponentsSorted = sortHashMapByValues(aComponentsSizes); //???
		LinkedHashMap<ClosedAbstractStrandDiagram, Integer> bComponentsSorted = sortHashMapByValues(bComponentsSizes);

		/* If the sizes of the components in A do not equal the sizes of the components in B then they are not conjugate */
		
		ClosedAbstractStrandDiagram[] aKeys = aComponentsSorted.keySet().toArray(new ClosedAbstractStrandDiagram[aComponentsSorted.size()]);
		ClosedAbstractStrandDiagram[] bKeys = bComponentsSorted.keySet().toArray(new ClosedAbstractStrandDiagram[bComponentsSorted.size()]);
		
		
		i = 0;
		while (i < numComponents) { //linear on max num components
			ClosedAbstractStrandDiagram aKey = aKeys[i];
			ClosedAbstractStrandDiagram bKey = bKeys[i];
			int aSize = aComponentsSorted.get(aKey); //linear on max num components
			int bSize = bComponentsSorted.get(bKey);
			
			if (aSize != bSize) {
				return false;
			}
			i++;
		}
		
		/* Check for isomorphism between A and B */
		
		i = 0;
		ClosedAbstractStrandDiagram aCurrComponent;
		ClosedAbstractStrandDiagram bCurrComponent;
		/*If mapping found from aCurrComponent to bCurrComponent, add bCurrComponent to mappingFound list
		 * We will use this to ensure that we do not map more than one component in A to the same component in B*/
		ArrayList<ClosedAbstractStrandDiagram> mappingFound = new ArrayList<ClosedAbstractStrandDiagram>();
		
		/*The following loop will iterate through the components of A, with the goal of finding an isomorphism for each one*/
		while (i < numComponents) { //linear on max num components
			aCurrComponent = aKeys[i];
			/*The following while loop will iterate through the components of B, with the goal of discovering an isomorphism 
			between aCurrComponent and the current component of B*/
			j=0;
			while (j<numComponents) { //linear on max num components
				bCurrComponent = bKeys[j];
				/* If we already found a mapping for aCurrComponent, break */
				if (aCurrComponent.foundConjugacy()) {
					break;
				}
				
				/* If a mapping exists between this component and a component of A, skip to the next component of B */
				if (bCurrComponent.foundConjugacy()) {
					j++;
					continue;
				}
				
				/* If these components do not have the same size then they cannot be isomorphic, skip to the next component of B */
				if (bCurrComponent.size() != aCurrComponent.size()) {
					j++;
					continue;
				}
				
				/* Attempt to construct isomorphism between aCurrComponent and bCurrComponent */
				HashMap<Strand, Strand> strandBijection = new HashMap<Strand, Strand>();
				HashMap<Vertex, Vertex> vertexBijection = new HashMap<Vertex, Vertex>();
				Queue<Strand> strandsToProcess = new LinkedBlockingQueue<Strand>();
				Queue<Vertex> verticesToProcess = new LinkedBlockingQueue<Vertex>();
				Node fixedANode  = aCurrComponent.getVertices().getFirstNode();
				Vertex fixedA = fixedANode.getVertex(); //note that this vertex a is fixed for the entirety of the current (j) while loop
				Type fixedAType = fixedA.getType();
				Node bNode = bCurrComponent.getVertices().getFirstNode();
				
				int k = 0;
				Vertex v;
				Vertex w;
				
				/* Try mapping fixedA to all vertices in this component of B until an isomorphism is found */
				while (k<bCurrComponent.getVertices().getLength()) { //linear on num vertices in B
					aCurrComponent.unmarkAll(); //linear
					bCurrComponent.unmarkAll(); //linear
					strandBijection.clear();
					vertexBijection.clear();
					strandsToProcess.clear();
					verticesToProcess.clear();
					Vertex fixedB = bNode.getVertex(); //note that this vertex b is fixed only within this iteration of the current (k) while loop
					Type fixedBType = fixedB.getType();
					if (fixedAType.equals(fixedBType)) {
						//if fixedA and fixedB have the same type then we can start constructing an isomorphism
						fixedA.mark();
						fixedB.mark();
						verticesToProcess.add(fixedA);
						vertexBijection.put(fixedA, fixedB);
						/* If at any point we discover an inconsistency in the mapping,
						 *  then the isomorphism has failed and we will mark keepConstructingIsomorphism=false*/
						keepConstructingIsomorphism = true;
						/* Assuming fixedA maps to fixedB, process all strands/vertices */
						while (keepConstructingIsomorphism && (!verticesToProcess.isEmpty() || !strandsToProcess.isEmpty())) {
							//linear on n + 3n/2 (num vertices and edges)
							if (!verticesToProcess.isEmpty()) {
								//Process a vertex
								v = verticesToProcess.remove();
								w = vertexBijection.get(v);
								if (v.numDistinctStrands() != w.numDistinctStrands()) {
									keepConstructingIsomorphism = false;
									break;
								}
								for (Role r: v.getStrands().keySet()) { //constant (num strands is always 3)
									Strand s = v.getStrand(r);
									Strand t = w.getStrand(r);
									if (s.isMarked() && t.isMarked()) {
										/* Make sure that s and t are mapped to each other */
										 if (strandBijection.containsKey(s)) {
											if (strandBijection.get(s).equals(t)) {
												/* this is what we want, proceed to the next strand */
												continue;
											}
											else {
												/* if s is mapped to a strand other than t, 
												 * then this isomorphism doesn't work */
												keepConstructingIsomorphism = false;
												/* break from for loop and try mapping fixed A to another vertex in bCurrComponent */
												break;
											}
										}
										 else {
											 /*should be unreachable since as soon as we mark a strand we add it to the bijection*/
											 throw new RuntimeException("Issue with isomorphism checker! Debug immediately!");
										 }
									}
									else if ( !s.isMarked() && !t.isMarked() ){
										s.mark();
										t.mark();
										strandsToProcess.add(s);
										strandBijection.put(s, t);
									}
									else {
										/* if s is marked but not t, or vice versa, 
										 * then this isomorphism doesn't work */
										keepConstructingIsomorphism = false;
										/* break from for loop and try mapping fixed A to another vertex in bCurrComponent */
										break;
									}
								//Finished examining strands connected to vertex v	
								}
							//Finished processing a vertex
							}
							
							if (keepConstructingIsomorphism && !strandsToProcess.isEmpty()) {
								//Process a strand
								Strand s = strandsToProcess.remove();
								Strand t = strandBijection.get(s);
								Vertex sBegin = s.getBeginVertex();
								Vertex sEnd = s.getEndVertex();
								Vertex tBegin = t.getBeginVertex();
								Vertex tEnd = t.getEndVertex();
								if (sBegin.isMarked() && tBegin.isMarked()) {
									/* If both are marked, then 
									 * make sure sBegin and tBegin are  mapped to each other */
									if (vertexBijection.containsKey(sBegin)) {
										if (vertexBijection.get(sBegin).equals(tBegin)) {
											/* this is what we want, proceed to sEnd and tEnd */
										}
										else {
											/* If sBegin and tBegin are not mapped to each other then this isomorphism has failed, 
											 * break from for loop and try mapping fixed A to another vertex in bCurrComponent */
											keepConstructingIsomorphism = false;
											break;
										}
									}
									else {
										/*should be unreachable since as soon as we mark a vertex we add it to the bijection*/
										throw new RuntimeException("Issue with isomorphism checker! Debug immediately!");
									}
								}
								else if (!sBegin.isMarked() && !tBegin.isMarked()) {
									/* Check if sBegin and tBegin have the same type */
									if ( (sBegin.getType().equals(tBegin.getType()) )
											&& (s.getBeginRole().equals(t.getBeginRole())) ) {
										/* Since type is the same, mark map sBegin and tBegin to each other */
										sBegin.mark();
										tBegin.mark();
										vertexBijection.put(sBegin, tBegin);
										verticesToProcess.add(sBegin);
									}
									else {
										/* If sBegin and tBegin do not have the same type then we can not map them to one another
										 * so this isomorphism has failed,
										 * break from for loop and try mapping fixed A to another vertex in bCurrComponent */
										keepConstructingIsomorphism = false;
										break;
									}
									
									
								}
								else {
									/* If only one of sBegin/tBegin are marked then this isomorphism has failed,
									 * break from for loop and try mapping fixed A to another vertex in bCurrComponent
									 */
									keepConstructingIsomorphism = false;
									break;
								}
								
								if (sEnd.isMarked() && tEnd.isMarked()) {
									/* If both are marked, then 
									 * make sure sEnd and tEnd are  mapped to each other */
									if (vertexBijection.containsKey(sEnd)) {
										if (vertexBijection.get(sEnd).equals(tEnd)) {
											/* this is what we want, proceed */
										}
										else {
											/* If sBegin and tBegin are not mapped to each other then this isomorphism has failed, 
											 * break from for loop and try mapping fixed A to another vertex in bCurrComponent */
											keepConstructingIsomorphism = false;
											break;
										}
									}
									else {
										/*should be unreachable since as soon as we mark a vertex we add it to the bijection*/
										throw new RuntimeException("Issue with isomorphism checker! Debug immediately!");
									}
								}
								else if (!sEnd.isMarked() && !tEnd.isMarked()) {
									/* Check if sEnd and tEnd have the same type */
									if ( (sEnd.getType().equals(tEnd.getType()) )
											&& (s.getBeginRole().equals(t.getBeginRole())) ) {
										/* Since type/role is the same, mark map sEnd and tEnd to each other */
										sEnd.mark();
										tEnd.mark();
										vertexBijection.put(sEnd, tEnd);
										verticesToProcess.add(sEnd);
									}
									else {
										/* If sEnd and tEnd do not have the same type then we can not map them to one another
										 * so this isomorphism has failed,
										 * break from for loop and try mapping fixed A to another vertex in bCurrComponent */
										keepConstructingIsomorphism = false;
										break;
									}
									
								}
								else {
									/* If only one of sEnd/tEnd are marked then this isomorphism has failed,
									 * break from for loop and try mapping fixed A to another vertex in bCurrComponent
									 */
									keepConstructingIsomorphism = false;
									break;
								}
								//finished examining vertices connected to current strand
							}
							//finished processing strand s
						}
						//end of while loop for current mapping, all strands/vertices processed
						/* Check if isomorphism failed */
						if (keepConstructingIsomorphism) {
							/*Isomorphism successful! Now check cohomology class */
							for (Strand s: strandBijection.keySet()) {
								Strand t = strandBijection.get(s);
							}
							
	
							//computing the difference in cocycles between components, and 
							//determining whether the cocycle difference is in the column space of the coboundary matrix
							
							/* Construct coboundary matrix */
							Matrix A = aCurrComponent.getCoboundaryMatrix();
							
							int rankA = A.rank();
							int aRowDim = A.getRowDimension();
							int aColDim = A.getColumnDimension();
							
							/* Compute the difference in component cocycles as a vector */
							Matrix B = aCurrComponent.getCocycleDifference(strandBijection);
							
							Matrix AB = new Matrix(aRowDim, aColDim + 1);
							
							int i0 = 0;
							int i1 = aRowDim - 1;
							int j0 = 0;
							int j1 = aColDim - 1;
							int j2 = aColDim;
							
							
							AB.setMatrix(i0, i1, j0, j1, A);
							AB.setMatrix(i0, i1, j2, j2, B);
							
							/* If the rank of the original coboundary matrix is the same 
							 * as the rank of the coboundary matrix with the difference in cocycles as an added rightmost column, 
							 * then the difference in cocycles is in the column space of the coboundary matrix.
							 * Therefore, these two components are in the same cohomology class */
							if (rankA == AB.rank()) {
								bCurrComponent.foundConjugacy(true);
								aCurrComponent.foundConjugacy(true);
							}
							else {
							}
							
							
						}
						
					
						//end of case in which fixedA and fixedB have the same type
					}
					/* If we found a mapping which preserves the cohomology class, break from this while loop and move to next component of B*/
					if (bCurrComponent.foundConjugacy()) {
						break;
					}
					//If no mapping found, stay in this while loop try next node (try a new mapping on the same component)
					bNode = bNode.getNext();
					k++;
				
					//end of loop for vertices of bCurrComponent
				}
				j++;
				//end of loop for bCurrComponent
			}
			i++;
		
			//end of loop for aCurrComponent
		}
		
		/* If mappings were found for all components in B then A and B are conjugate */
		
		for (ClosedAbstractStrandDiagram a: aKeys) {
			if (!a.foundConjugacy()) {
				return false;
			}
		}
		for (ClosedAbstractStrandDiagram b: bKeys) {
			if (!b.foundConjugacy()) {
				return false;
			}
		}
		/* eventually, after all checks.... */
		return true;
	}
	
	public static LinkedHashMap<ClosedAbstractStrandDiagram, Integer> sortHashMapByValues(
	        HashMap<ClosedAbstractStrandDiagram, Integer> passedMap) {
	    ArrayList<ClosedAbstractStrandDiagram> mapKeys = new ArrayList<ClosedAbstractStrandDiagram>(passedMap.keySet());
	    ArrayList<Integer> mapValues = new ArrayList<Integer>(passedMap.values()); //linear on max number of components
	    Collections.sort(mapValues); //linearithmic on max number of components
	    LinkedHashMap<ClosedAbstractStrandDiagram, Integer> sortedMap =
	        new LinkedHashMap<>();
	    Iterator<Integer> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) { //linear on max number of components
	        Integer val = valueIt.next();
	        Iterator<ClosedAbstractStrandDiagram> keyIt = mapKeys.iterator();
	        while (keyIt.hasNext()) { //linear on max number of components
	            ClosedAbstractStrandDiagram key = keyIt.next();
	            Integer comp1 = passedMap.get(key); //linear on max number of components
	            Integer comp2 = val;
	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
	
	

	
	public static void main(String args[]) {
		
		/* TWO COMPONENT TEST */
		
//		int a1 = TreeDiagram.randInt(20);
//		int a2 = TreeDiagram.randInt(20);
//		int b = TreeDiagram.randInt(20);
//		
//		while(a1<2) {
//			a1 = TreeDiagram.randInt(20);
//		}
//		while(a2<2) {
//			a2 = TreeDiagram.randInt(20);
//		}
//		while(b<2) {
//			b = TreeDiagram.randInt(20);
//		}
//		
//		
//		TreeDiagram x1 = TreeDiagram.generateRandomElement(a1);
//		System.out.println("x1: " + System.getProperty("line.separator") + x1);
//		TreeDiagram x2 = TreeDiagram.generateRandomElement(a2);
//		System.out.println("x2: " + System.getProperty("line.separator") + x2);
//		TreeDiagram x = TreeDiagram.plus(x1, x2);
//		System.out.println("x = plus(x1,x2): " + System.getProperty("line.separator") + x);
//		TreeDiagram c = TreeDiagram.generateRandomElement(b);
//		System.out.println("c: " + System.getProperty("line.separator") + c);
//		TreeDiagram cInv = c.getInverse();
//		TreeDiagram y = cInv.multiply((x.multiply(c)));
//		
//		/* x and y should be conjugate */
//		
//		ConjugacyChecker cc = new ConjugacyChecker(x,y);
//		System.out.println(cc.areConjugate());
		
		
		/* SINGLE COMPONENT TEST */
		
//		int a = TreeDiagram.randInt(20);
//		int b = TreeDiagram.randInt(20);
//		while(a<5) {
//			a = TreeDiagram.randInt(20);
//		}
//		while(b<5) {
//			b = TreeDiagram.randInt(20);
//		}
//		
//		
//		TreeDiagram x = TreeDiagram.generateRandomElement(a);
//		System.out.println("x: " + System.getProperty("line.separator") + x);
//		TreeDiagram c = TreeDiagram.generateRandomElement(b);
//		System.out.println("c: " + System.getProperty("line.separator") + c);
//		TreeDiagram cInv = c.getInverse();
//		TreeDiagram y = cInv.multiply((x.multiply(c)));
//		
//		/* x and y should be conjugate */
//		
//		ConjugacyChecker cc = new ConjugacyChecker(x,y);
//		System.out.println(cc.areConjugate());
		
		
		/* SPECIFIC ELEMENT TEST */
		
		String domain[] = {"0", "10", "110", "111"};
		ArrayList<BinarySequence> d = BinarySequence.newList(domain);
		String range[] = {"0", "100", "101", "11"};
		ArrayList<BinarySequence> r = BinarySequence.newList(range);
		ArrayList<Integer> p = new ArrayList<Integer>(Arrays.asList(2, 0, 3, 1));
		TreeDiagram x = new TreeDiagram(d,r,p);
		System.out.println("x: " + System.getProperty("line.separator") + x);
		
//		String domain2[] = {"0", "100", "101", "11"};
//		ArrayList<BinarySequence> d2 = BinarySequence.newList(domain2);
//		String range2[] = {"0", "10", "110", "111"};
//		ArrayList<BinarySequence> r2 = BinarySequence.newList(range2);
//		ArrayList<Integer> p2 = new ArrayList<Integer>(Arrays.asList(3, 0, 1, 2));
//		TreeDiagram c = new TreeDiagram(d2,r2,p2);
//		System.out.println("c: " + System.getProperty("line.separator") + c);
		
		String domain2[] = {"0", "100", "101", "11"};
		ArrayList<BinarySequence> d2 = BinarySequence.newList(domain2);
		String range2[] = {"0", "10", "110", "111"};
		ArrayList<BinarySequence> r2 = BinarySequence.newList(range2);
		ArrayList<Integer> p2 = new ArrayList<Integer>(Arrays.asList(3, 0, 1, 2));
		TreeDiagram c = new TreeDiagram(d2,r2,p2);
		System.out.println("y: " + System.getProperty("line.separator") + c);
		
		TreeDiagram cInv = c.getInverse();
		System.out.println("cInv: " + System.getProperty("line.separator") + cInv);
		TreeDiagram y = cInv.multiply((x.multiply(c)));
		System.out.println("y: " + System.getProperty("line.separator") + y);

		ConjugacyChecker cc = new ConjugacyChecker(x,y);
		System.out.println(cc.areConjugate());
		
		
		
	}

}

/*
LinkedHashMap<ClosedAbstractStrandDiagram, Integer> map = new LinkedHashMap<ClosedAbstractStrandDiagram, Integer>();
int i = 0;
while (i<3) {
	TreeDiagram a = TreeDiagram.generateRandomElement(4);
	ClosedAbstractStrandDiagram A = a.convertToStrandDiagram().close();
	A.reduce();
	map.put(A, a.size());
	i++;
}
System.out.println(map);

LinkedHashMap<ClosedAbstractStrandDiagram, Integer> sorted = sortHashMapByValues(map);
System.out.println(sorted);
*/


/*
LinkedHashMap<Integer,String> map = new LinkedHashMap<Integer,String>();
map.put(1, "banana");
map.put(7, "cat");
map.put(5, "apple");
LinkedHashMap<Integer,String> sorted = sortHashMapByValues2(map);
System.out.println(sorted);
*/

//String domain[] = {"00", "01", "10", "11"};
//ArrayList<BinarySequence> d = BinarySequence.newList(domain);
//String range[] = {"00", "01", "10", "11"};
//ArrayList<BinarySequence> r = BinarySequence.newList(range);
//ArrayList<Integer> p = new ArrayList<Integer>(Arrays.asList(0,1,2,3));
//TreeDiagram t = new TreeDiagram(d,r,p);
//
//
//String domain2[] = {"00", "01", "1"};
//ArrayList<BinarySequence> d2 = BinarySequence.newList(domain2);
//String range2[] = {"00", "01", "1"};
//ArrayList<BinarySequence> r2 = BinarySequence.newList(range2);
//ArrayList<Integer> p2 = new ArrayList<Integer>(Arrays.asList(0,1,2));
//TreeDiagram t2 = new TreeDiagram(d2,r2,p2);
//
//ConjugacyChecker cc = new ConjugacyChecker(t,t2);
//System.out.println(cc.areConjugate());

///* Get least squares solution X to AX = B (A is coboundary matrix, B is cocycle difference) */
//Matrix solution = coboundaryMatrix.solve(cocycleDiff);
//
///* Get product of multiplying original coboundary matrix by least square solution */
//Matrix solutionProduct = coboundaryMatrix.times(solution);	
//
///* If product is within .01 (ish) of cocycleDiff then we assume that the least squares solution was a valid solution
// * Thus these two components belong to the same conjugacy class */
// 
//if (coboundaryMatrix.equals(solutionProduct)) {
//	mappingFound.add(bCurrComponent);
//}
///* If not, then then this isomorphism has failed us, keep looking */

//public class SortMapByValue {
//
//
//public SortMapByValue() {
//	
//}
//
//public TreeMap<ClosedAbstractStrandDiagram, Integer> sortMapByValue(TreeMap<ClosedAbstractStrandDiagram, Integer> map){
//	Comparator<ClosedAbstractStrandDiagram> comparator = new ValueComparator(map);
//	//TreeMap is a map sorted by its keys. 
//	//The comparator is used to sort the TreeMap by keys. 
//	TreeMap<ClosedAbstractStrandDiagram, Integer> result = new TreeMap<ClosedAbstractStrandDiagram, Integer>(comparator);
//	result.putAll(map);
//	return result;
//}
//}
//
//
//class ValueComparator implements Comparator<ClosedAbstractStrandDiagram>{
//
//TreeMap<ClosedAbstractStrandDiagram, Integer> map = new TreeMap<ClosedAbstractStrandDiagram, Integer>();
//
//public ValueComparator(TreeMap<ClosedAbstractStrandDiagram, Integer> map){
//	this.map.putAll(map);
//}
//
//@Override
//public int compare(ClosedAbstractStrandDiagram c1, ClosedAbstractStrandDiagram c2) {
//	if(map.get(c1) <= map.get(c2)){
//		return -1;
//	}else{
//		return 1;
//	}	
//}
//}
