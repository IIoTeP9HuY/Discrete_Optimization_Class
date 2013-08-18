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
public class KOptImprover {

	// Let's suppose k = 2 for simplicity
	// Having route v1, v2, v3, v4, v5, v6
	// And swapping A = 2 with B = 5
	// Returns v1, v5, v4, v3, v2, v6
	// So the cost changes like that
	// D(v1, v2) + D(v5, v6) - D(v1, v5) - D(v2, v6)
	// D(v(A - 1), vA) + D(vB, v(B + 1)) - D(v(A - 1), vB) - D(vA, v(B + 1))

	static Random random = new Random(42);

	public static int findSwapCandidate(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int base, int baseNext) {

		double bestDistance = 1e9;
		int closestPosition = -1;
		Point baseNextPoint = vertexes.get(route.get(baseNext)).position;
		for (int c = 1; c < route.size(); ++c) {
//			int c = random.nextInt(route.size() - 1) + 1;
			if (c == base || c == baseNext || c == baseNext + 1) {
				continue;
			}

			Point C = vertexes.get(route.get(c)).position;
			double distance = Point.distance(baseNextPoint, C);
			if (distance < bestDistance) {
				closestPosition = c;
				bestDistance = distance;
			}
		}
		int swapCandidate = closestPosition - 1;
//		return swapCandidate;
		return random.nextInt(route.size() - 1) + 1;

	}

	public double improve(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int iterations, int k) {
		double basicLength = TSPSolver.findRouteLength(vertexes, route);
		for (int it = 0; it < iterations; ++it) {
			double improvement = 0;
			double bestImprovement = 0;
			ArrayList<Integer> bestRoute = new ArrayList<Integer>(route.size());
			bestRoute.addAll(route);

			int maxDistancePointPosition = -1;
			double maxDistance = 0;
			for (int i = 1; i < route.size() - 1; ++i) {
				Point A = vertexes.get(route.get(i)).position;
				Point B = vertexes.get(route.get(i + 1)).position;
				if (Point.distance(A, B) > maxDistance) {
					maxDistance = Point.distance(A, B);
					maxDistancePointPosition = i;
				}
			}
			maxDistancePointPosition = random.nextInt(route.size() - 2) + 1;

			int base = maxDistancePointPosition;
			int baseNext = maxDistancePointPosition + 1;
			int swapCandidate = findSwapCandidate(vertexes, route, base, baseNext);

			// a -> b
			// c is near b
			// want a !-> b, b -> c
			// a -> (c - 1)
			// a, b ... (c - 1), c
			// turns into
			// a, c - 1, ... b, c
			// swap(c-1, b)
			// swap(swapCandidate, baseNext)

//			System.err.println("Max distance: " + maxDistance);
//			System.err.println(base + " " + baseNext + " " + swapCandidate);
			for (int i = 0; i < k; ++i) {

				int a = baseNext;
				int b = swapCandidate;

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

				for (int shift = 0; shift < (b - a + 1) / 2; ++shift) {
					Collections.swap(route, a + shift, b - shift);
				}
				improvement += before - after;

				if (improvement > bestImprovement) {
					bestImprovement = improvement;
					Collections.copy(bestRoute, route);
				}
//				System.err.println("Improvement: " + improvement);

				base = base;
				baseNext = swapCandidate;
				swapCandidate = findSwapCandidate(vertexes, route, base, baseNext);
			}
//			System.err.println("Best improvement: " + bestImprovement);
			Collections.copy(route, bestRoute);
		}
		return basicLength - TSPSolver.findRouteLength(vertexes, route);
	}
}
