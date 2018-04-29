package ThompsonsGroupV;

public class MultiComponentTest {

	static int numElements = 100;
	static int incorrectFindings = 0;
	static int minNumGenerators = 5;
	static int maxNumGenerators = 20;
	
	public static void main (String args[]) {
		
		double numElementsDouble = numElements;
		int i = 0;
		long averageRunTime = 0;
		while (i<numElements) {
			int a1 = TreeDiagram.randInt(maxNumGenerators);
			int a2 = TreeDiagram.randInt(maxNumGenerators);
			int b = TreeDiagram.randInt(maxNumGenerators);
			
			while(a1<minNumGenerators) {
				a1 = TreeDiagram.randInt(maxNumGenerators);
			}
			while(a2<minNumGenerators) {
				a2 = TreeDiagram.randInt(maxNumGenerators);
			}
			while(b<minNumGenerators) {
				b = TreeDiagram.randInt(maxNumGenerators);
			}
			
			
			TreeDiagram x1 = TreeDiagram.generateRandomElement(a1);
			TreeDiagram x2 = TreeDiagram.generateRandomElement(a2);
			TreeDiagram x = TreeDiagram.plus(x1, x2);
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
//int a1 = TreeDiagram.randInt(20);
//int a2 = TreeDiagram.randInt(20);
//int b = TreeDiagram.randInt(20);
//
//while(a1<2) {
//	a1 = TreeDiagram.randInt(20);
//}
//while(a2<2) {
//	a2 = TreeDiagram.randInt(20);
//}
//while(b<2) {
//	b = TreeDiagram.randInt(20);
//}
//
//
//TreeDiagram x1 = TreeDiagram.generateRandomElement(a1);
//System.out.println("x1: " + System.getProperty("line.separator") + x1);
//TreeDiagram x2 = TreeDiagram.generateRandomElement(a2);
//System.out.println("x2: " + System.getProperty("line.separator") + x2);
//TreeDiagram x = TreeDiagram.plus(x1, x2);
//System.out.println("x = plus(x1,x2): " + System.getProperty("line.separator") + x);
//TreeDiagram c = TreeDiagram.generateRandomElement(b);
//System.out.println("c: " + System.getProperty("line.separator") + c);
//TreeDiagram cInv = c.getInverse();
//TreeDiagram y = cInv.multiply((x.multiply(c)));
//
///* x and y should be conjugate */
//
//ConjugacyChecker cc = new ConjugacyChecker(x,y);
//System.out.println(cc.areConjugate());
