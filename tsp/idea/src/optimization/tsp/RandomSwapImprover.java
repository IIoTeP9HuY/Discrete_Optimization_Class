package optimization.tsp;

import data_structures.Point;
import data_structures.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * User: iiotep9huy
 * Date: 8/16/13
 * Time: 5:51 PM
 * Project: TSP
 */
public class RandomSwapImprover {

	public double improve(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int iterations) {
		double improvement = 0;
		Random random = new Random();
		for (int i = 0; i < iterations; ++i) {

			int a = random.nextInt(route.size() - 1) + 1;
			int b = random.nextInt(route.size() - 1) + 1;

			if (a == b + 1 || a == b - 1) {
				continue;
			}

			Point S1 = vertexes.get(route.get(a - 1)).position;
			Point A = vertexes.get(route.get(a)).position;
			Point T1 = vertexes.get(route.get((a + 1) % route.size())).position;
			Point S2 = vertexes.get(route.get(b - 1)).position;
			Point B = vertexes.get(route.get(b)).position;
			Point T2 = vertexes.get(route.get((b + 1) % route.size())).position;

			double before = Point.distance(S1, A) + Point.distance(A, T1) + Point.distance(S2, B) + Point.distance(B, T2);
			double after = Point.distance(S1, B) + Point.distance(B, T1) + Point.distance(S2, A) + Point.distance(A, T2);
			if (before > after) {
				Collections.swap(route, a, b);
				improvement += before - after;
			}
		}
		return improvement;
	}
}
