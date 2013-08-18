package optimization.tsp;

import data_structures.Point;
import data_structures.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * User: iiotep9huy
 * Date: 8/17/13
 * Time: 10:32 PM
 * Project: TSP
 */
public class SimulatedAnnealingImprover {

	static Random random = new Random(42);

	public double improve(ArrayList<Vertex> vertexes, ArrayList<Integer> route, double startingTemperature, double alpha) {
		double improvement = 0;
		double temperature = startingTemperature;
		int it = 0;
		while (temperature > 1e-2) {
			++it;

			for (int i = 0; i < 5000; ++i)
			{
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
				double localImprovement = before - after;

				if (random.nextDouble() <= Math.exp(localImprovement / temperature)) {
					for (int shift = 0; shift < (b - a + 1) / 2; ++shift) {
						Collections.swap(route, a + shift, b - shift);
					}
					improvement += localImprovement;
//					System.err.println("LocalImprovement: " + localImprovement + " Temperature: " + temperature);
				}
			}

			temperature *= alpha;
			System.err.println("Improvement: " + improvement + " Temperature: " + temperature);
		}
		System.err.println("Iterations: " + it);
		return improvement;
	}
}
