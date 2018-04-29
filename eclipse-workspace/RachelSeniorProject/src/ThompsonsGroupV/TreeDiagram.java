package ThompsonsGroupV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Random;

public class TreeDiagram {

	/*
	 * An ordered list of complete binary prefixes which represents the intervals
	 * for the domain of an element of V i.e. the top tree in the tree diagram
	 */
	private ArrayList<BinarySequence> domain;
	/*
	 * An ordered list of complete binary prefixes which represents the intervals
	 * for the range of an element of V i.e. the bottom tree in the tree diagram
	 */
	private ArrayList<BinarySequence> range;
	/*
	 * A permutation which represents the mapping for an interval in the domain of V
	 * to an interval of the range beginning with 0
	 */
	private ArrayList<Integer> permutation;

	/*
	 * The number of cuts in the dyadic subdivision
	 */
	private int size;

	public TreeDiagram(ArrayList<BinarySequence> domain, ArrayList<BinarySequence> range, ArrayList<Integer> p) {
		int d = domain.size();
		if (d == 0)
			throw new IllegalArgumentException("The domain must contain at least one interval");
		if (d != range.size())
			throw new IllegalArgumentException("The domain and range must contain the same number of intervals");
		if (d != p.size())
			throw new IllegalArgumentException("The map must be the same length as the domain");
		if (!p.contains(0))
			throw new IllegalArgumentException("The map must contain a 0 for the first interval");

		this.domain = domain;
		this.range = range;
		this.permutation = p;
		this.size = d;
	}

	/*
	 * Return the number of cuts in the dyadic subdivision i.e. the number of
	 * intervals in the domain/range
	 */
	public int size() {
		return this.size;
	}

	/*
	 * Return the domain code
	 */
	public ArrayList<BinarySequence> getDomain() {
		return this.domain;
	}

	/*
	 * Return the range code
	 */
	public ArrayList<BinarySequence> getRange() {
		return this.range;
	}

	/*
	 * Return the permutation code
	 */
	public ArrayList<Integer> getPermutation() {
		return this.permutation;
	}

	/*
	 * Return the inverse of this tree diagram
	 */
	@SuppressWarnings("unchecked")
	public TreeDiagram getInverse() {
		ArrayList<BinarySequence> newDomain = (ArrayList<BinarySequence>) this.range.clone();
		ArrayList<BinarySequence> newRange = (ArrayList<BinarySequence>) this.domain.clone();
		ArrayList<Integer> newPermutation = (ArrayList<Integer>) this.permutation.clone();
		for (int i = 0; i < size; i++) {
			int p = permutation.get(i);
			newPermutation.set(p, i);
		}
		TreeDiagram inverse = new TreeDiagram(newDomain, newRange, newPermutation);
		return inverse;
	}

	/*
	 * Determine whether the sequence in domain code at a given index and
	 * corresponding sequence in range code meet conditions for a reduction to occur
	 * Returns a ReductionInfo object
	 */
	private ReductionInfo isReduction(ArrayList<Integer> perm, ArrayList<BinarySequence> dom,
			ArrayList<BinarySequence> ran, int idx) {
		ReductionInfo r = new ReductionInfo();
		int p1 = perm.get(idx);
		int p2 = perm.get(idx + 1);
		if (p1 == p2 - 1) {
			BinarySequence dom1 = dom.get(idx);
			BinarySequence dom2 = dom.get(idx + 1);
			if (BinarySequence.isOrderedCaret(dom1, dom2)) {
				int rangeIdx1 = perm.get(idx);
				int rangeIdx2 = perm.get(idx + 1);
				BinarySequence ran1 = ran.get(rangeIdx1);
				BinarySequence ran2 = ran.get(rangeIdx2);
				if (BinarySequence.isOrderedCaret(ran1, ran2)) {
					r.isReduction = true;
					r.p2 = p2;
					r.domIdx1 = idx;
					r.domIdx2 = idx + 1;
					r.domGCP = dom1.getGCP(dom2);
					r.lastDigit = BinarySequence.getLastDigit(r.domGCP);
					r.rangeIdx1 = rangeIdx1;
					r.rangeIdx2 = rangeIdx2;
					r.rangeGCP = ran1.getGCP(ran2);
					return r;
				}
			}
		}
		r.isReduction = false;
		return r;
	}

	/*
	 * Helper class to store necessary information to reduce tree diagrams
	 */
	private class ReductionInfo {
		boolean isReduction;
		int p2;
		int domIdx1;
		int domIdx2;
		BinarySequence domGCP;
		int rangeIdx1;
		int rangeIdx2;
		BinarySequence rangeGCP;
		String lastDigit;
	}

	/*
	 * Helper method to perform a reduction at a given index in a permutation code
	 * i.e. remove a caret
	 */
	private ArrayList<Integer> reducePerm(ArrayList<Integer> perm, ReductionInfo r) {
		int j = 0;
		while (j < perm.size()) {
			int current = perm.get(j);
			if (current == r.p2) {
				perm.remove(j);
			} else if (current > r.p2) {
				perm.set(j, current - 1);
				j++;
			} else
				j++;
		}
		return perm;
	}

	/*
	 * Helper method to perform a reduction at a given index in a domain code i.e.
	 * remove a caret
	 */
	private ArrayList<BinarySequence> reduceDom(ArrayList<BinarySequence> dom, ReductionInfo r) {
		int j = 0;
		while (j < dom.size()) {
			if (j == r.domIdx1)
				dom.set(j, r.domGCP);
			else if (j == r.domIdx2)
				dom.remove(j);
			j++;
		}
		return dom;
	}

	/*
	 * Helper method to perform a reduction at a given index in a range code i.e.
	 * remove a caret
	 */
	private ArrayList<BinarySequence> reduceRange(ArrayList<BinarySequence> ran, ReductionInfo r) {
		int j = 0;
		while (j < ran.size()) {
			if (j == r.rangeIdx1)
				ran.set(j, r.rangeGCP);
			else if (j == r.rangeIdx2)
				ran.remove(j);
			j++;
		}
		return ran;
	}

	/*
	 * Reduce the tree diagram, Return the current tree diagram in reduced form
	 */
	public TreeDiagram reduce() {
		ArrayList<BinarySequence> newDomain = new ArrayList<BinarySequence>(this.domain);
		ArrayList<BinarySequence> newRange = new ArrayList<BinarySequence>(this.range);
		ArrayList<Integer> newPermutation = new ArrayList<Integer>((ArrayList<Integer>) this.permutation);
		boolean foundReduction;
		int i = 0;
		while (i < newPermutation.size() - 1) {
			foundReduction = false;
			ReductionInfo r = this.isReduction(newPermutation, newDomain, newRange, i);
			if (r.isReduction) {
				foundReduction = true;
				newPermutation = reducePerm(newPermutation, r);
				newDomain = reduceDom(newDomain, r);
				newRange = reduceRange(newRange, r);
			}
			// if no reduction happened, increment i (keep looking)
			if (!foundReduction)
				i++;
			// if a reduction happened in the right child, back up to see if another
			// reduction was created
			else if (i > 0 && r.lastDigit.equals("1"))
				i--;
			// else a reduction happened in the left child so we should check this spot
			// again, nothing happens to i
		}
		return new TreeDiagram(newDomain, newRange, newPermutation);
	}

	/*
	 * Helper method for "un-reducing" tree diagrams Create a caret in a list from a
	 * given prefix with the left child in the 0th index and the right child in the
	 * 1st index
	 */
	private static ArrayList<BinarySequence> makeCaret(BinarySequence prefix) {
		ArrayList<BinarySequence> b = new ArrayList<BinarySequence>(Arrays
				.asList(new BinarySequence(prefix.toString() + "0"), new BinarySequence(prefix.toString() + "1")));
		return b;
	}

	/*
	 * Helper method to "un-reduce" a permutation code by adding a caret at a given
	 * index
	 */
	private static ArrayList<Integer> addCaretPerm(ArrayList<Integer> perm, int idx) {
		int threshold = perm.size();
		int p = perm.get(idx);
		boolean addLast = false;
		boolean lastTime = false;
		if (idx == (threshold - 1)) {
			if (p == (threshold - 1)) {
				perm.add(threshold, threshold);
				return perm;
			} else
				addLast = true;
		}
		int current;
		int j = 0;
		while (j < perm.size()) {
			current = perm.get(j);
			if (lastTime) {
				perm.add(j + 1, p + 1);
				break;
			} else if (j == idx + 1)
				perm.add(j, p + 1);
			else if (current > p)
				perm.set(j, current + 1);
			if (j == (perm.size() - 1) && addLast) {
				addLast = false;
				lastTime = true;
				continue;
			}
			j++;
		}
		return perm;
	}

	/*
	 * Helper method to "un-reduce" tree diagrams by adding a caret to a given
	 * domain or range code at the given index
	 */
	private static ArrayList<BinarySequence> addCaret(ArrayList<BinarySequence> array, int idx) {
		BinarySequence prefix = array.get(idx);
		ArrayList<BinarySequence> carets = makeCaret(prefix);
		BinarySequence leftCaret = carets.get(0);
		BinarySequence rightCaret = carets.get(1);
		int j = 0;
		int threshold = array.size();
		if (idx == threshold - 1)
			threshold++;
		while (j < threshold) {
			if (j == idx)
				array.set(j, leftCaret);
			else if (j == idx + 1)
				array.add(j, rightCaret);
			j++;
		}
		return array;
	}

	/*
	 * Return the product of this tree diagram (g) and a given tree diagram (f) Note
	 * that g.multiply(f) = g o f = f first, then g (f on top, g on bottom)
	 */
	public TreeDiagram multiply(TreeDiagram f) {
		ArrayList<BinarySequence> domainf = new ArrayList<BinarySequence>(f.getDomain());
		ArrayList<BinarySequence> rangef = new ArrayList<BinarySequence>(f.getRange());
		ArrayList<Integer> permf = (ArrayList<Integer>) new ArrayList<Integer>(f.getPermutation());
		ArrayList<BinarySequence> domaing = new ArrayList<BinarySequence>(this.domain);
		ArrayList<Integer> permg = new ArrayList<Integer>(this.permutation);
		ArrayList<BinarySequence> rangeg = new ArrayList<BinarySequence>(this.range);
		ArrayList<Integer> newPerm = new ArrayList<Integer>();
		int i = 0;
		BinarySequence rangefCurrent;
		BinarySequence domaingCurrent;
		while (i < rangef.size()) {
			rangefCurrent = rangef.get(i);
			domaingCurrent = domaing.get(i);
			if (!rangefCurrent.equals(domaingCurrent)) {
				if (rangefCurrent.getLength() < domaingCurrent.getLength()) {
					int rangefIdx = i;
					int domainfIdx = permf.indexOf(i);
					rangef = addCaret(rangef, rangefIdx);
					permf = addCaretPerm(permf, domainfIdx);
					domainf = addCaret(domainf, domainfIdx);
				} else {
					int domaingIdx = i;
					int rangegIdx = permg.get(i);
					domaing = addCaret(domaing, domaingIdx);
					permg = addCaretPerm(permg, domaingIdx);
					rangeg = addCaret(rangeg, rangegIdx);
				}
			} else
				i++;
		}
		newPerm = composePerms(permf, permg);
		return new TreeDiagram(domainf, rangeg, newPerm);
	}

	/*
	 * Method to compose to permutation codes, for use in multiplying tree diagrams
	 */
	public static ArrayList<Integer> composePerms(List<Integer> perm1, List<Integer> perm2) {
		ArrayList<Integer> newPerm = new ArrayList<Integer>();
		int size1 = perm1.size();
		int size2 = perm2.size();
		int k = 0;
		if (size1 > size2) {
			while (k < size1) {
				int idx = perm1.get(k);
				if (idx > size2 - 1)
					newPerm.add(idx);
				else {
					int p = perm2.get(idx);
					newPerm.add(p);
				}
				k++;
			}
		} else if (size2 > size1) {
			while (k < size2) {
				int p;
				if (k > size1 - 1)
					p = perm2.get(k);
				else {
					int idx = perm1.get(k);
					p = perm2.get(idx);
				}
				newPerm.add(p);
				k++;
			}
		} else {
			while (k < perm1.size()) {
				int idx = perm1.get(k);
				int p = perm2.get(idx);
				newPerm.add(p);
				k++;
			}
		}

		return newPerm;
	}

	/*
	 * Helper method to convert a tree diagram to an abstract strand diagram
	 */
	private void addVertex(Vertex v, DoubleLinkedList vertices) {
		Node node = new Node(v);
		vertices.add(node);
	}

	public AbstractStrandDiagram convertToStrandDiagram() { //quadratic on size of tree diagram
		DoubleLinkedList vertices = new DoubleLinkedList(); // constant
		Vertex source = new Vertex(Type.SOURCE); // constant
		source.setPrefixID("source"); // constant
		Vertex sink = new Vertex(Type.SINK); // constant
		sink.setPrefixID("sink"); // constant
		addVertex(source, vertices); // constant
		addVertex(sink, vertices); // constant
		// Create all split vertices
		Set<String> splitPrefixes = new HashSet<String>(); // constant
		for (BinarySequence b : this.domain) { // linear in length of domain -> n^2
			splitPrefixes.addAll(b.getProperPrefixes()); // linear in length of binary sequence = 1/2 length of domain
		}
		Hashtable<String, Vertex> splits = new Hashtable<String, Vertex>(); // constant
		for (String s : splitPrefixes) { // linear in size of splitPrefixes set,n=domain length, domain length - 1 -> n^2
			Vertex v = new Vertex(Type.SPLIT); // constant
			addVertex(v, vertices); // constant
			splits.put(s, v); // linear in size of splitPrefixes set -> n-1
			v.setPrefixID(s); // constant
		}
		Enumeration<String> keys = splits.keys(); // linear n-1
		while (keys.hasMoreElements()) { // n-1
			String key = keys.nextElement(); // constant
			Vertex v = splits.get(key); // linear in length of string (n-1)
			Strand s; // constant
			if (key.equals("e")) { // constant
				s = new Strand(source, v, Role.CHILD, Role.PARENT); // constant
				source.setStrand(Role.CHILD, s); // constant
				source.setEndVertex(s, v, Role.PARENT); // constant
			} else {
				Role r = Role.NULL; // constant
				if (BinarySequence.getLastDigit(key).equals("0")) //constant
					r = Role.LCHILD; // constant
				else
					r = Role.RCHILD; // constant
				String parent; // constant
				if (key.equals("0") || key.equals("1")) //constant
					parent = "e"; // linear in length of key
				else
					parent = BinarySequence.getPrefix(key); // linear in length of key (n-1)
				Vertex vParent = splits.get(parent); // linear, n-1
				s = new Strand(vParent, v, r, Role.PARENT); // constant
				vParent.setStrand(r, s); //constant
				v.setStrand(Role.PARENT, s); //constant
			}
		}
		// Create all merge vertices: runtime is the same for merges as splits
		Set<String> mergePrefixes = new HashSet<String>();
		for (BinarySequence b : this.range)
			mergePrefixes.addAll(b.getProperPrefixes());
		Hashtable<String, Vertex> merges = new Hashtable<String, Vertex>();
		for (String s : mergePrefixes) {
			Vertex v = new Vertex(Type.MERGE);
			addVertex(v, vertices);
			merges.put(s, v);
			v.setPrefixID(s);
		}
		keys = merges.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			Vertex v = merges.get(key);
			Strand s;
			if (key.equals("e")) {
				s = new Strand(v, sink, Role.CHILD, Role.PARENT);
				v.setStrand(Role.CHILD, s);
				v.setEndVertex(s, sink, Role.PARENT);
			} else {
				Role r;
				if (BinarySequence.getLastDigit(key).equals("0"))
					r = Role.LPARENT;
				else
					r = Role.RPARENT;
				String child;
				if (key.equals("0") || key.equals("1"))
					child = "e";
				else
					child = BinarySequence.getPrefix(key);
				Vertex vChild = merges.get(child);
				s = new Strand(v, vChild, Role.CHILD, r);
				v.setStrand(Role.CHILD, s);
				vChild.setStrand(r, s);
			}
		}
		// Use permutation to create strands to connect splits with merges
		int domIdx = 0; // constant
		int rangeIdx = -1; // constant
		while (domIdx < permutation.size()) { // linear in n
			rangeIdx = permutation.get(domIdx); // linear in n
			BinarySequence domain = this.domain.get(domIdx); // linear in n
			BinarySequence range = this.range.get(rangeIdx); // linear in n
			// get split info
			String splitId; // constant
			if (domain.toString().equals("0") || domain.toString().equals("1")) // constant
				splitId = "e"; // constant
			else
				splitId = domain.getLargestProperPrefix(); // linear in length of domain (n-1)
			Vertex split = splits.get(splitId); // linear in length of splits map (n-1)
			String last = BinarySequence.getLastDigit(domain); // constant
			Role beginRole = Role.NULL; // constant
			if (last.equals("0")) //constant
				beginRole = Role.LCHILD; // constant
			else
				beginRole = Role.RCHILD; // constant
			// get merge info
			String mergeId; // same as above
			if (range.toString().equals("0") || range.toString().equals("1"))
				mergeId = "e";
			else
				mergeId = range.getLargestProperPrefix();
			Vertex merge = merges.get(mergeId);
			last = BinarySequence.getLastDigit(range);
			Role endRole = Role.NULL;
			if (last.equals("0"))
				endRole = Role.LPARENT;
			else
				endRole = Role.RPARENT;
			// use info to construct new strand
			Strand s = new Strand(split, merge, beginRole, endRole); // constant
			split.setStrand(beginRole, s); // constant
			merge.setStrand(endRole, s); // constant
			// increment domIdx
			domIdx++; // constant
		}
		return new AbstractStrandDiagram(vertices, source, sink); // constant
	}

	/*
	 * Remainder of methods used for debugging and testing
	 */

	/*
	 * The next 8 methods return generators A, B, C, and pi0 as well as there
	 * inverses
	 */
	public static TreeDiagram A() {
		ArrayList<BinarySequence> domain = new ArrayList<BinarySequence>(
				Arrays.asList(new BinarySequence("0"), new BinarySequence("10"), new BinarySequence("11")));
		ArrayList<BinarySequence> range = new ArrayList<BinarySequence>(
				Arrays.asList(new BinarySequence("00"), new BinarySequence("01"), new BinarySequence("1")));
		ArrayList<Integer> perm = new ArrayList<Integer>(Arrays.asList(0, 1, 2));
		return new TreeDiagram(domain, range, perm);
	}

	public static TreeDiagram Ainv() {
		return A().getInverse();
	}

	public static TreeDiagram B() {
		ArrayList<BinarySequence> domain = new ArrayList<BinarySequence>(Arrays.asList(new BinarySequence("0"),
				new BinarySequence("10"), new BinarySequence("110"), new BinarySequence("111")));
		ArrayList<BinarySequence> range = new ArrayList<BinarySequence>(Arrays.asList(new BinarySequence("0"),
				new BinarySequence("100"), new BinarySequence("101"), new BinarySequence("11")));
		ArrayList<Integer> perm = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
		return new TreeDiagram(domain, range, perm);
	}

	public static TreeDiagram Binv() {
		return B().getInverse();
	}

	public static TreeDiagram C() {
		ArrayList<BinarySequence> domain = new ArrayList<BinarySequence>(
				Arrays.asList(new BinarySequence("0"), new BinarySequence("10"), new BinarySequence("11")));
		ArrayList<BinarySequence> range = new ArrayList<BinarySequence>(
				Arrays.asList(new BinarySequence("0"), new BinarySequence("10"), new BinarySequence("11")));
		ArrayList<Integer> perm = new ArrayList<Integer>(Arrays.asList(2, 0, 1));
		return new TreeDiagram(domain, range, perm);
	}

	public static TreeDiagram Cinv() {
		return C().getInverse();
	}

	public static TreeDiagram pi0() {
		ArrayList<BinarySequence> domain = new ArrayList<BinarySequence>(
				Arrays.asList(new BinarySequence("0"), new BinarySequence("10"), new BinarySequence("11")));
		ArrayList<BinarySequence> range = new ArrayList<BinarySequence>(
				Arrays.asList(new BinarySequence("0"), new BinarySequence("10"), new BinarySequence("11")));
		ArrayList<Integer> perm = new ArrayList<Integer>(Arrays.asList(1, 0, 2));
		return new TreeDiagram(domain, range, perm);
	}

	public static TreeDiagram pi0inv() {
		return pi0().getInverse();
	}

	/*
	 * Returns a random element of V generated by multiplying any number (given by
	 * user) of generators Note that numGenerators must be greater than or equal to
	 * 2
	 */
	public static TreeDiagram generateRandomElement(int numGenerators) {
		if (numGenerators < 2)
			throw new IllegalArgumentException("You must input an integer greater than or equal to 2");
		TreeDiagram product;
		ArrayList<TreeDiagram> generators = new ArrayList<TreeDiagram>(
				Arrays.asList(A(), B(), C(), pi0(), Ainv(), Binv(), Cinv(), pi0inv()));
		int j = randInt(8);
		product = generators.get(j);
		int i = 1;
		while (i < numGenerators) {
			j = randInt(8);
			TreeDiagram t = generators.get(j);
			product = product.multiply(t);
			i++;
		}
		return product;
	}

	/*
	 * Return a random integer between 0 and u, the upper bound, NOT inclusive
	 */
	public static int randInt(int u) {
		Random rn = new Random();
		return rn.nextInt(u);
	}
	
	/*
	 * Parse a string and return the corresponding element of V
	 */
	public static TreeDiagram getGenerator(String s) {
		if(s.equals("A")) return A();
		else if (s.equals("B")) return B();
		else if (s.equals("C")) return C();
		else if (s.equals("P")) return pi0();
		else if (s.equals("a")) return Ainv();
		else if (s.equals("b")) return Binv();
		else if (s.equals("c")) return Cinv();
		else if (s.equals("p")) return pi0inv();
		throw new IllegalArgumentException("Please enter a valid word");
	}

	/*
	 * For use in creating multi-component elements Implement "plus" operator, where
	 * plus(f, g) = f "plus" g
	 */
	public static TreeDiagram plus(TreeDiagram f, TreeDiagram g) {
		ArrayList<BinarySequence> fDomain = f.getDomain();
		ArrayList<BinarySequence> fRange = f.getRange();
		ArrayList<Integer> fPerm = f.getPermutation();
		int fLength = fPerm.size();
		ArrayList<BinarySequence> gDomain = g.getDomain();
		ArrayList<BinarySequence> gRange = g.getRange();
		ArrayList<Integer> gPerm = g.getPermutation();
		ArrayList<BinarySequence> domain = new ArrayList<BinarySequence>();
		ArrayList<BinarySequence> range = new ArrayList<BinarySequence>();
		ArrayList<Integer> perm = new ArrayList<Integer>();
		for (BinarySequence b : fDomain)
			domain.add(new BinarySequence(0 + b.toString()));
		for (BinarySequence b : gDomain)
			domain.add(new BinarySequence(1 + b.toString()));
		for (BinarySequence b : fRange)
			range.add(new BinarySequence(0 + b.toString()));
		for (BinarySequence b : gRange)
			range.add(new BinarySequence(1 + b.toString()));
		for (int i : fPerm)
			perm.add(i);
		for (int i : gPerm)
			perm.add(i + fLength);
		return new TreeDiagram(domain, range, perm);
	}

	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append(domainString());
		r.append(permString());
		r.append(rangeString());
		return r.toString();
	}

	public String domainString() {
		StringBuilder r = new StringBuilder();
		for (int i = 0; i < this.size; i++) {
			BinarySequence b = domain.get(i);
			if (i == this.size - 1) {
				r.append(b);
			} else {
				r.append(b + ", ");
			}
		}
		r.append(System.lineSeparator());
		return r.toString();
	}

	public String rangeString() {
		StringBuilder r = new StringBuilder();
		for (int i = 0; i < this.size; i++) {
			BinarySequence b = range.get(i);
			if (i == this.size - 1) {
				r.append(b);
			} else {
				r.append(b + ", ");
			}
		}
		r.append(System.lineSeparator());
		return r.toString();
	}

	public String permString() {
		StringBuilder r = new StringBuilder();
		for (int i = 0; i < this.size; i++) {
			int b = permutation.get(i);
			if (i == this.size - 1) {
				r.append(b);
			} else {
				r.append(b + ", ");
			}
		}
		r.append(System.lineSeparator());

		return r.toString();
	}

	/*
	 * Multiple with prints statements, used for debugging
	 */
	public TreeDiagram multiply2(TreeDiagram f) {
		System.out.println("f: " + System.getProperty("line.separator") + f);
		System.out.println("g: " + System.getProperty("line.separator") + this);
		ArrayList<BinarySequence> domainf = f.getDomain();
		ArrayList<BinarySequence> rangef = f.getRange();
		ArrayList<Integer> permf = (ArrayList<Integer>) f.getPermutation();

		ArrayList<BinarySequence> domaing = new ArrayList<BinarySequence>(this.domain);
		ArrayList<Integer> permg = new ArrayList<Integer>(this.permutation);
		ArrayList<BinarySequence> rangeg = new ArrayList<BinarySequence>(this.range);

		ArrayList<Integer> newPerm = new ArrayList<Integer>();

		int i = 0;
		BinarySequence rangefCurrent;
		BinarySequence domaingCurrent;

		while (i < rangef.size()) {
			System.out.println("i = " + i);
			rangefCurrent = rangef.get(i);
			domaingCurrent = domaing.get(i);
			System.out.println("rangefCurrent:" + System.getProperty("line.separator") + rangefCurrent);
			System.out.println("domaingCurrent:" + System.getProperty("line.separator") + domaingCurrent);

			if (!rangefCurrent.equals(domaingCurrent)) {

				if (rangefCurrent.getLength() < domaingCurrent.getLength()) {
					int rangefIdx = i;
					int domainfIdx = permf.indexOf(i);

					System.out.println("adding caret in f");

					rangef = addCaret(rangef, rangefIdx);
					permf = addCaretPerm(permf, domainfIdx);
					domainf = addCaret(domainf, domainfIdx);

				}

				else {
					int domaingIdx = i;
					int rangegIdx = permg.get(i);

					System.out.println("adding caret in g");

					domaing = addCaret(domaing, domaingIdx);
					permg = addCaretPerm(permg, domaingIdx);
					rangeg = addCaret(rangeg, rangegIdx);

				}
			}

			else {
				i++;
			}
			System.out.println("f: " + System.getProperty("line.separator") + domainf
					+ System.getProperty("line.separator") + permf + System.getProperty("line.separator") + rangef);
			System.out.println("g:" + System.getProperty("line.separator") + domaing
					+ System.getProperty("line.separator") + permg + System.getProperty("line.separator") + rangeg);
		}
		newPerm = composePerms(permf, permg);
		return new TreeDiagram(domainf, rangeg, newPerm);
	}

	public static void main(String[] args) {
		String s = "a";
		String t = "A";
		System.out.println(s.equals(t));
	}

}