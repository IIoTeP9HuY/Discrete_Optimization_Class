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
public class TwoOptImprover {

	// Let's suppose k = 2 for simplicity
	// Having route v1, v2, v3, v4, v5, v6
	// And swapping A = 2 with B = 5
	// Returns v1, v5, v4, v3, v2, v6
	// So the cost changes like that
	// D(v1, v2) + D(v5, v6) - D(v1, v5) - D(v2, v6)
	// D(v(A - 1), vA) + D(vB, v(B + 1)) - D(v(A - 1), vB) - D(vA, v(B + 1))

	public double improve(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int iterations) {
		double improvement = 0;
		Random random = new Random(42);
		for (int i = 0; i < iterations; ++i) {

			int a = random.nextInt(route.size() - 1) + 1;
			int b = random.nextInt(route.size() - 1) + 1;

			if (a == b) {
				continue;
			}
			if (a > b) {
				int tmp = a;
				a = b;
				b = tmp;
			}

			Point S1 = vertexes.get(route.get(a - 1)).position;
			Point A = vertexes.get(route.get(a)).position;
			Point B = vertexes.get(route.get(b)).position;
			Point T2 = vertexes.get(route.get((b + 1) % route.size())).position;

			double before = Point.distance(S1, A) + Point.distance(B, T2);
			double after = Point.distance(S1, B) + Point.distance(A, T2);
			if (before > after) {
				for (int shift = 0; shift < (b - a + 1) / 2; ++shift) {
					Collections.swap(route, a + shift, b - shift);
				}
				improvement += before - after;
			}
		}
		return improvement;
	}
}
