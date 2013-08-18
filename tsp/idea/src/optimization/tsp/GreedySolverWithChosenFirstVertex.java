package optimization.tsp;

import data_structures.Point;
import data_structures.Vertex;

import java.util.ArrayList;

/**
 * User: iiotep9huy
 * Date: 8/15/13
 * Time: 10:29 AM
 * Project: TSP
 */
public class GreedySolverWithChosenFirstVertex {

	ArrayList<Integer> solve(ArrayList<Vertex> vertexes, int firstVertex) {
		ArrayList<Integer> route = new ArrayList<Integer>();
		boolean[] visited = new boolean[vertexes.size()];
		int currentVertex = 0;
		route.add(currentVertex);
		visited[currentVertex] = true;

		currentVertex = firstVertex;
		route.add(currentVertex);
		visited[currentVertex] = true;
		for (int i = 0; i < vertexes.size() - 2; ++i) {

			int closestVertex = -1;
			double closestDistance = 1e9;
			for (int nextVertex = 0; nextVertex < vertexes.size(); ++nextVertex) {
				if (visited[nextVertex]) {
					continue;
				}

				double newDistance = Point.distance(vertexes.get(currentVertex).position, vertexes.get(nextVertex).position);
				if (closestVertex == -1 || newDistance < closestDistance) {
					closestVertex = nextVertex;
					closestDistance = newDistance;
				}
			}

			currentVertex = closestVertex;
			route.add(currentVertex);
			visited[currentVertex] = true;
		}
		return route;
	}
}
