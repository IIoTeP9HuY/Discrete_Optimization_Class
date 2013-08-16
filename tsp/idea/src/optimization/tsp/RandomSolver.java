package optimization.tsp;

import data_structures.Vertex;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: iiotep9huy
 * Date: 8/15/13
 * Time: 10:33 PM
 * Project: TSP
 */
public class RandomSolver {

	ArrayList<Integer> solve(ArrayList<Vertex> vertexes) {
		ArrayList<Integer> route = new ArrayList<Integer>();
		for (int i = 0; i < vertexes.size(); ++i) {
			route.add(i);
		}

		Random random = new Random();
		for (int i = 0; i < vertexes.size(); ++i) {
			int a = random.nextInt(vertexes.size() - 1) + 1;
			int b = random.nextInt(vertexes.size() - 1) + 1;

			Integer tmp = route.get(a);
			route.set(a, route.get(b));
			route.set(b, tmp);
		}

		return route;
	}
}
