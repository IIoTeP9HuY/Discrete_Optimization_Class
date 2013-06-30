package data_structures.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * User: iiotep9huy
 * Date: 6/30/13
 * Time: 5:04 PM
 * Project: Coloring
 */
public class Graph {
	public Graph(int verticesNumber) {
		incidenceList = new ArrayList<ArrayList<Integer>>(verticesNumber);
		while (incidenceList.size() < verticesNumber) {
			incidenceList.add(new ArrayList<Integer>());
		}
	}

	public void addEdge(int firstVertex, int secondVertex) {
		addArc(firstVertex, secondVertex);
		addArc(secondVertex, firstVertex);
	}

	public void addArc(int source, int destination) {
		incidenceList.get(source).add(destination);
	}

	public int getSufficientColorsNumber() {
		int maxVertexDegree = 0;
		for (int i = 0; i < incidenceList.size(); ++i) {
			maxVertexDegree = Math.max(maxVertexDegree, incidenceList.get(i).size());
		}
		int sufficientColorsNumber = maxVertexDegree + 1;
		return sufficientColorsNumber;
	}

	public ArrayList<ArrayList<Integer>> incidenceList;
}
