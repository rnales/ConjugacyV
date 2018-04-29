package ThompsonsGroupV;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

/*
 * CLASS NOTE USED IN FINAL CONJUGACY CHECKING PROGRAM
 */
public class SeparateComponents {
	
	private LinkedList<ClosedAbstractStrandDiagram> components;
	private ClosedAbstractStrandDiagram diagram;
	private int numComponents = 0;
	
	public SeparateComponents() {

	}
	

	
	public int getNumComponents() {
		return numComponents;
	}
	
	public LinkedList<ClosedAbstractStrandDiagram> getComponents(ClosedAbstractStrandDiagram a){
		this.diagram = a;
		LinkedList<ClosedAbstractStrandDiagram> components = new LinkedList<ClosedAbstractStrandDiagram>();
		DoubleLinkedList vertices = diagram.getVertices();
		Stack<Node> nodesToProcess = new Stack<Node>();
		
		Node currentNode = vertices.getFirstNode();
		Node connectedNode;

		Vertex currentVertex;
		Vertex processVertex;
		Vertex connectedVertex;
		
		Collection<Strand> strands;
		
		int i = 0;
		int lengthVertices = vertices.getLength();
		while (i<lengthVertices) {
			currentVertex = currentNode.getVertex();
			if (!currentVertex.isFound()) {
				currentVertex.hasBeenFound(true);
				nodesToProcess.push(currentNode);
				DoubleLinkedList newComponent = new DoubleLinkedList(new Node(currentVertex));
				Node processNode;
				while (!nodesToProcess.isEmpty()) {
					processNode = nodesToProcess.pop();
					processVertex = processNode.getVertex();
					strands = processVertex.getStrands().values();
					for (Strand s : strands) {
						connectedVertex = processVertex.getOtherVertex(s);
						if (!connectedVertex.isFound()) {
							connectedVertex.hasBeenFound(true);
							connectedNode = connectedVertex.getNode();
							nodesToProcess.push(connectedNode);
							newComponent.add(new Node(connectedVertex));	
						}
					}
				}
			components.add(new ClosedAbstractStrandDiagram(newComponent));
			}
			i++;
			currentNode = currentNode.getNext();
		}
		this.numComponents = components.size();
		this.components = components;
		return components;
	}

}
