package optimization.tsp;

import data_structures.Point;
import data_structures.Vertex;

import java.util.ArrayList;
import java.util.Collections;

/**
 * User: iiotep9huy
 * Date: 8/15/13
 * Time: 9:02 PM
 * Project: TSP
 */
public class LocalSwapImprover {

	public double improve(ArrayList<Vertex> vertexes, ArrayList<Integer> route) {
		double improvement = 0;
		for (int i = 1; i < route.size() - 1; ++i) {
			Point S = vertexes.get(route.get(i - 1)).position;
			Point A = vertexes.get(route.get(i)).position;
			Point B = vertexes.get(route.get(i + 1)).position;
			Point T = vertexes.get(route.get((i + 2) % route.size())).position;

			double before = Point.distance(S, A) + Point.distance(B, T);
			double after = Point.distance(S, B) + Point.distance(A, T);
			if (before > after) {
				Collections.swap(route, i, i + 1);
				improvement += before - after;
			}
		}
		return improvement;
	}
}
