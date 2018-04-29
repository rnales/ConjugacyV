package ThompsonsGroupV;

public class Strand {
	private Vertex begin;
	private Vertex end;
	private Role beginRole;
	private Role endRole;
	private int cValue = 0; //default value is 0, Strand Diagrams don't use this (only Closed Strand Diagrams use)
	private boolean isMarked = false; //for use in isomorphism algorithm
	private boolean isFound = false; // for use in construction of coboundary matrix
	private static int count = 0; //for use in assigning IDs
	private int id; //for use in strand equality method
	private int rowID; //for use in construction of coboundary matrix
	
	/*
	 * Three constructors for new strands
	 */
	public Strand() {
		this(new Vertex(), new Vertex(), Role.NULL, Role.NULL);
	}
	
	public Strand(Vertex begin, Vertex end, Role beginRole, Role endRole) {
		this.begin = begin;
		this.end = end;
		this.beginRole = beginRole;
		this.endRole = endRole;
		this.id = count++;
	}
	
	public Strand(Vertex begin, Vertex end) {
		this(begin, end, Role.NULL, Role.NULL);
	}
	
	/*
	 * To delete a strand, set all of its data to null
	 */
	public void delete() {
		this.begin = null;
		this.end = null;
		this.beginRole = null;
		this.endRole = null;
	}
	
	/*
	 * Object equality for two strands
	 */
	public boolean equals(Strand other) {
		if (this.id == other.getId()) return true;
		else return false;
	}
	
	/*
	 * Return the row id for this strand in the coboundary matrix
	 */
	public int getRowID() {
		return rowID;
	}
	
	/*
	 * Assign a row id for this strand in the coboundary matrix
	 */
	public void setRowID(int r) {
		rowID = r;
	}
	
	/*
	 * Return the integer id (assigned upon creation) of this strand
	 */
	public int getId() {
		return id;
	}
	
	/*
	 * Assign a c value to this strand
	 */
	public void setCValue(int c) {
		cValue = c;
	}
	
	/*
	 * Increment this strand's c value
	 */
	public void incrementCValue() {
		cValue++;
	}
	
	/*
	 * Decrement this strand's c value
	 */
	public void decrementCValue() {
		cValue--;
	}
	
	/*
	 * Return this strand's c value
	 */
	public int getCValue() {
		return cValue;
	}
	
	/*
	 * Change the role played at the beginning of this strand
	 */
	public void setBeginRole(Role role) {
		beginRole = role;
	}
	
	/*
	 * Return the role played at the beginning of this strand
	 */
	public Role getBeginRole() {
		return beginRole;
	}
	
	/*
	 * Change the role played at the end of this strand
	 */
	public void setEndRole(Role role) {
		endRole = role;
	}
	
	/*
	 * Return the role played at the end of this strand
	 */
	public Role getEndRole() {
		return endRole;
	}
	
	/*
	 * Return the vertex at the beginning of this strand
	 */
	public Vertex getBeginVertex() {
		return begin;
	}
	
	/*
	 * Set the vertex at the beginning of this strand
	 */
	public void setBeginVertex(Vertex vertex) {
		begin = vertex;
	}
	
	/*
	 * Return the vertex at the end of this strand
	 */
	public Vertex getEndVertex() {
		return end;
	}
	
	/*
	 * Get the vertex at the end of this strand
	 */
	public void setEndVertex(Vertex vertex) {
		end = vertex;
	}
	
	/*
	 * The following six methods are getters and setters 
	 * for this strand's boolean markers
	 */
	public void mark() {
		isMarked = true;
	}
	
	public void unMark() {
		isMarked = false;
	}
	
	public boolean isMarked() {
		return isMarked;
	}
	
	public boolean isFound() {
		return isFound;
	}
	
	public void markFound() {
		isFound = true;
	}
	
	public void markNotFound() {
		isFound = false;
	}
	
	/*
	 * Not a safe method, not used in final conjugacy checking program
	 */
	public Role getRole(Vertex vertex) {
		if (vertex.equals(this.begin)) return this.beginRole;
		else if (vertex.equals(this.end)) return this.endRole;
		throw new IllegalArgumentException("Argument must be a vertex attached to this strand");
	}
	
	/* 
	 * For use in debugging 
	 */
	
	public String toString() {
		StringBuilder r = new StringBuilder("STRAND ID: " + this.id + ", Number of Crossings: " + this.cValue);
		r.append(System.getProperty("line.separator"));
		r.append("Begin Vertex Prefix ID: " + this.begin.getPrefixID() + ", Vertex ID: " + this.begin.getVertexID() + ", " + this.begin.getType() + ", " + this.beginRole);
		r.append(System.getProperty("line.separator"));
		r.append("End Vertex Prefix ID: " + this.end.getPrefixID() + ", Vertex ID: " + this.end.getVertexID() + ", " + this.end.getType() + ", " + this.endRole);
		r.append(System.getProperty("line.separator"));
		return r.toString();
	}
	
//	public String toString() {
//		return this.id + "";
//	}
	
	public static void main(String args[]) {
		
	}

	
	
}
