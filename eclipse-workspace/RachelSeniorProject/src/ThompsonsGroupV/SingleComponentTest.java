package ThompsonsGroupV;
//get standard deviation as well!!!
public class SingleComponentTest {

	static int numElements = 1000;
	static int incorrectFindings = 0;
	static int minNumGenerators = 5;
	static int maxNumGenerators = 20;
	
	public static void main (String args[]) {
		
		double numElementsDouble = numElements;
		int i = 0;
		long averageRunTime = 0;
		while (i<numElements) {
			int a = TreeDiagram.randInt(maxNumGenerators);
			int b = TreeDiagram.randInt(maxNumGenerators);
			while(a<minNumGenerators) {
				a = TreeDiagram.randInt(maxNumGenerators);
			}
			while(b<minNumGenerators) {
				b = TreeDiagram.randInt(maxNumGenerators);
			}
			TreeDiagram x = TreeDiagram.generateRandomElement(a);
			TreeDiagram c = TreeDiagram.generateRandomElement(b);
			TreeDiagram cInv = c.getInverse();
			TreeDiagram y = cInv.multiply((x.multiply(c)));
			long startTime = System.currentTimeMillis();
			ConjugacyChecker cc = new ConjugacyChecker(x,y);
			boolean areConjugate = cc.areConjugate();
			long endTime = System.currentTimeMillis();
			long runTime = endTime - startTime;
			averageRunTime += runTime;
			if (!areConjugate) {
				incorrectFindings++;
				System.out.println("EDGE CASE FOUND");
				System.out.println("x:" + System.getProperty("line.separator") + x);
				System.out.println("y:" + System.getProperty("line.separator") + y);
			}
			i++;
		}
		System.out.println(incorrectFindings);
		System.out.println("Average run time: " + averageRunTime/numElementsDouble);
		
		
	}
	
	
	
}
