package ThompsonsGroupV;

public class DoubleLinkedList {
	
	private Node first;
	private Node last;
	private int length;
	
	/*
	 * Two constructors for a double linked list
	 */
	public DoubleLinkedList() {
		length = 0;
		first = null;
		last = null;
	}
	
	public DoubleLinkedList(Node first) {
		this.first = first;
		this.first.setPrevious(this.first);
		this.last = first;
		length++;
	}
	
	/*
	 * Return a pointer to the first node in the list
	 */
	public Node getFirstNode() {
		return first;
	}
	
	/*
	 * Add a vertex to this list
	 */
	public void add(Vertex vertex) {
		Node node = new Node(vertex);
		vertex.setNode(node);
		add(node);
	}
	
	/*
	 * Add a new node this list
	 */
	public void add(Node node) {
		if (length == 0) {
			first = node;
			first.setPrevious(first);
			last = node;
			first.setNext(last);
			last.setPrevious(first);
		}
		else {
			last.setNext(node);
			node.setPrevious(last);
			last = node;
		}
		length++;
	}
	
	/*
	 * Delete the given node from this list
	 */
	public void delete(Node node) {
		if (!this.contains(node)) throw new IllegalArgumentException("Argument must be a node contained in this list");
		if (length == 1) {
			first = null;
			last = null;
		}
		else if (node.equals(this.first)) {
			Node next = node.getNext();
			next.setPrevious(next);
			node.setNext(null);
			node.setPrevious(null);
			first = next;
		}
		else if (node.equals(this.last)) {
			Node prev = node.getPrevious();
			prev.setNext(prev);
			last.setNext(null);
			last.setPrevious(null);
			last = prev;
		}
		else {
			Node prev = node.getPrevious();
			Node next = node.getNext();
			prev.setNext(next);
			next.setPrevious(prev);
			node.setNext(null);
			node.setPrevious(null);
		}
		length--;
	}
	
	/*
	 * Check to see whether this list contains the given node
	 */
	private boolean contains(Node node) {
		Node current = this.first;
		int i=0;
		while (i<this.length) {
			if (current.equals(node)) return true;
			current = current.getNext();
			i++;
		}
		return false;
	}
	
	/*
	 * Return the length (number of nodes) of this list
	 */
	public int getLength() {
		return length;
	}
	
	/*
	 * Combine this list with another list
	 */
	public void append(DoubleLinkedList l) {
		Node lFirst = l.getFirstNode();
		this.add(lFirst);
		this.length = this.length + l.getLength() - 1; //already added first vertex, add one less than current length of l to length
	}
	
	/*
	 * For use in debugging
	 */
	public Vertex getVertexByID(int vertexID) {
		Node current = this.first;
		int i = 0;
		while (i<this.length) {
			Vertex currentVertex = current.getVertex();
			if (currentVertex.getVertexID() == vertexID) return currentVertex;
			else {
				current = current.getNext();
				i++;
			}
		}
		throw new IllegalArgumentException("List does not contain a vertex with that ID");
	}
	
	public static void main(String args[]) {
		
	}
	
}
