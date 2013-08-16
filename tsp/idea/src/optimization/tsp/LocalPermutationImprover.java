package optimization.tsp;

import data_structures.Point;
import data_structures.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: iiotep9huy
 * Date: 8/15/13
 * Time: 9:22 PM
 * Project: TSP
 */
public class LocalPermutationImprover {

	public double improve(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int permuteSize) {
		double improvement = 0;
		for (int i = 1; i < route.size() - permuteSize + 1; ++i) {
			Point S = vertexes.get(route.get(i - 1)).position;
			Point T = vertexes.get(route.get((i + permuteSize) % route.size())).position;
			List<Integer> subList = route.subList(i, i + permuteSize);

			double before = Point.distance(S, vertexes.get(subList.get(0)).position)
					+ Point.distance(vertexes.get(subList.get(subList.size() - 1)).position, T);

			for (int v = 0; v < subList.size() - 1; ++v) {
				before += Point.distance(vertexes.get(subList.get(v)).position, vertexes.get(subList.get(v + 1)).position);
			}

			double bestLocalImprovement = 0;
			List<Integer> bestSubList = new ArrayList<Integer>(subList.size());
			for (int v = 0; v < subList.size(); ++v) {
				bestSubList.add(subList.get(v));
			}

			int allowedIterations = 500;
			while (allowedIterations > 0) {

				Collections.shuffle(subList);

				double after = Point.distance(S, vertexes.get(subList.get(0)).position)
						+ Point.distance(vertexes.get(subList.get(subList.size() - 1)).position, T);

				for (int v = 0; v < subList.size() - 1; ++v) {
					after += Point.distance(vertexes.get(subList.get(v)).position, vertexes.get(subList.get(v + 1)).position);
				}

				if (before > after) {
					double localImprovement = before - after;
					if (bestLocalImprovement < localImprovement) {
						bestLocalImprovement = localImprovement;
						for (int v = 0; v < subList.size(); ++v) {
							bestSubList.set(v, subList.get(v));
						}
					}
					break;
				}
				--allowedIterations;
			}
			for (int v = 0; v < subList.size(); ++v) {
				subList.set(v, bestSubList.get(v));
			}
			improvement += bestLocalImprovement;
		}
		return improvement;
	}
}
