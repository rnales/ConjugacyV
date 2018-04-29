package ThompsonsGroupV;
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
 * Class to store binary sequences and operations on binary sequences 
 * for use in constructing tree diagrams
 */
public class BinarySequence {
	
	private String val;
	private int length;
	
	/*
	 * Bug: constructor accepts strings containing not just integers (i.e. letters as well)
	 * Note that "e" represents the empty sequence
	 */
	public BinarySequence(String sequence) {
		int[] badDigits = {2,3,4,5,6,7,8,9};
		for (int i : badDigits) {
			String s = String.valueOf(i);
			if (sequence.contains(s)) throw new IllegalArgumentException("You must enter a sequence containing only 0's and 1's");
		}
		val = sequence;
		length = val.length();
	}
	
	/*
	 * Return value of sequence as a string
	 */
	public String getVal() {
		return val;
	}
	
	/*
	 * Return length of sequence
	 */
	public int getLength() {
		return length;
	}
	
	/*
	 * Return a set of all proper prefixes of sequence as strings
	 * Excludes the string which is equal to the value of this sequence
	 */
	public Set<String> getProperPrefixes(){
		Set<String> prefixes = new HashSet<String>();
		//Add empty sequence "e", which is a prefix for all binary sequences
		prefixes.add("e");
		int i = 1;
		while (i<this.length) {
			String p = this.val.substring(0, i);
			prefixes.add(p);
			i++;
		}
		return prefixes;
	}
	
	/*
	 * Return the prefix of length exactly one less than the length of this sequence
	 */
	public String getLargestProperPrefix() {
		return val.substring(0, length-1);	
	}
	
	/*
	 * A method to return the greatest common prefix (GCP) of two binary sequences
	 * Returns the empty string if there is no common prefix between the two sequences
	 */
	public BinarySequence getGCP(BinarySequence other) {
		String val2 = other.toString();
		int lenOther = other.getLength();
		int maxLen;
		if (lenOther<length) maxLen = lenOther;
		else maxLen = length;
		String gcpTemp = "";
		for (int i=0; i<maxLen; i++) {
			char a = val.charAt(i);
			char b = val2.charAt(i);
			if (a==b) gcpTemp = gcpTemp + a;
			else break;
		}
		BinarySequence gcp = new BinarySequence(gcpTemp);
		return gcp;
	}
	
	/*
	 * Return the last digit of this sequence as a string
	 */
	public static String getLastDigit(BinarySequence sequence) {
		String seq = sequence.toString();
		int len = seq.length();
		char last = seq.charAt(len-1);
		return last + "";	
	}
	
	/*
	 * Return the last character in a string as a string
	 */
	public static String getLastDigit(String string) {
		int len = string.length();
		if (len == 1) return string;
		else {
			char last = string.charAt(len-1);
			return last + "";
		}
	}
	
	/*
	 * Return the largest prefix of a given string as a string
	 */
	public static String getPrefix(String string) {
		int length = string.length();
		StringBuilder sequence = new StringBuilder(string);
		sequence.deleteCharAt(length - 1);
		return sequence.toString();
	}
	
	/*
	 * Identify whether two binary sequences form an ordered caret
	 * Note that the inputs must be ordered,
	 * i.e. the first sequence is the left child of the caret 
	 * and the second sequence is the right child of the caret
	 */
	public static boolean isOrderedCaret(BinarySequence firstSequence, BinarySequence secondSequence) {
		int length = firstSequence.getLength();
		if (firstSequence.equals(secondSequence) || length != secondSequence.getLength()) return false;
		else {
			if (firstSequence.getGCP(secondSequence).getLength() == length - 1) {
				if (firstSequence.toString().charAt(length - 1) == '0' 
						&& secondSequence.toString().charAt(length - 1) == '1') {
					return true;
				}
			}
			return false;
		}	
	}
	
	/*
	 * Return whether the value of this sequence is equal to the value of another sequence
	 */
	public boolean equals(BinarySequence other) {
		if (this.toString().equals(other.toString())) return true;
		else return false;
	}
	
	public String toString() {
		return val;
	}
	
	/*
	 * For use in testing
	 */
	public static ArrayList<BinarySequence> newList(String[] array){
		ArrayList<BinarySequence> result = new ArrayList<BinarySequence>();
		for (String s: array) result.add(new BinarySequence(s));
		return result;
	}
	
	public static void main(String args[]) {
		BinarySequence b = new BinarySequence("01010");
		BinarySequence a = new BinarySequence("0101");
		System.out.println(a.getGCP(b));
		
	}

}
