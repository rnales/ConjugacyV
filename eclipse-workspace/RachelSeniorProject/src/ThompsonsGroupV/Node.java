package ThompsonsGroupV;

public class Node {
	
	private Vertex vertex;
	private Node next;
	private Node previous;
	private static int count = 0; //for use in assigning ids
	private int id; //arbitrary integer id assigned upon creation
	
	/*
	 * Constructor for nodes
	 */
	public Node(Vertex vertex) {
		this.vertex = vertex;
		this.id = count++;
		vertex.setNode(this);
	}
	
	/*
	 * Return the id of this node
	 */
	public int getId() {
		return id;
	}
	
	/*
	 * Return the vertex stored in this node
	 */
	public Vertex getVertex() {
		return vertex;
	}

	/*
	 * Store a vertex in this node
	 */
	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	/*
	 * Return the node which follows this node in a double linked list
	 */
	public Node getNext() {
		return next;
	}

	/*
	 * Set the "next" pointer to a given node
	 */
	public void setNext(Node next) {
		this.next = next;
	}

	/*
	 * Return the node which preceeds this node in a double linked list
	 */
	public Node getPrevious() {
		return previous;
	}

	/*
	 * Set the "previous" pointer to a given node
	 */
	public void setPrevious(Node previous) {
		this.previous = previous;
	}
	
	public String toString() {
		return id + "";
	}

}
