package optimization.tsp;

import data_structures.Point;
import data_structures.Vertex;

import java.util.*;

/**
 * User: iiotep9huy
 * Date: 8/16/13
 * Time: 7:33 PM
 * Project: TSP
 */
public class RandomPermutationImprover {

	public double improve(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int permuteSize, int iterationsNumber) {

		double improvement = 0;

		Random random = new Random(42);
		for (int it = 0; it < iterationsNumber; ++it) {
			ArrayList<Integer> pickPositions = new ArrayList<Integer>(permuteSize);
			HashSet<Integer> picked = new HashSet<Integer>();
			for (int i = 0; i < permuteSize; ++i) {
				int nextPick = random.nextInt(route.size() - 1) + 1;
				if (!picked.contains(nextPick)) {
					pickPositions.add(nextPick);
					picked.add(nextPick);
				}
			}
			Collections.sort(pickPositions);

			ArrayList<Integer> pick = new ArrayList<Integer>(pickPositions.size());
			for (int i = 0; i < pickPositions.size(); ++i) {
				pick.add(route.get(pickPositions.get(i)));
			}

//			System.err.println("pick.size() = " + pick.size());
//			System.err.println("Pick: ");
//			for (int i = 0; i < pick.size(); ++i) {
//				System.err.print(pick.get(i) + " ");
//			}
//			System.err.println("");
//
//			System.err.println("Pick positions: ");
//			for (int i = 0; i < pick.size(); ++i) {
//				System.err.print(pickPositions.get(i) + " ");
//			}
//			System.err.println("");

//			if (pickPositions.size() == 2) {
//				if (pickPositions.get(0) + 1 == pickPositions.get(1)) {
//					double trueBefore = 0;
//
//					int a = pickPositions.get(0);
//					int b = pickPositions.get(1);
//					int s1 = route.get(a - 1);
//					int t1 = route.get((a + 1) % route.size());
//					int s2 = route.get(b - 1);
//					int t2 = route.get((b + 1) % route.size());
//
//					Point S1 = vertexes.get(s1).position;
//					Point A = vertexes.get(route.get(a)).position;
//					Point T1 = vertexes.get(t1).position;
//					Point S2 = vertexes.get(s2).position;
//					Point B = vertexes.get(route.get(b)).position;
//					Point T2 = vertexes.get(t2).position;
//
//					trueBefore += Point.distance(S1, A);
//					trueBefore += Point.distance(A, B);
//					trueBefore += Point.distance(B, T2);
//					System.err.println("trueBefore = " + trueBefore);
//				}
//			}

			double bestLocalImprovement = 0;
			ArrayList<Integer> bestLocalPick = new ArrayList<Integer>(pick.size());
			for (int i = 0; i < pick.size(); ++i) {
				bestLocalPick.add(pick.get(i));
			}

			double before = calculatePickBordersLength(vertexes, route, pick, pickPositions);

//			System.err.println("Before: " + before);

			for (int it_1 = 0; it_1 < 300; ++it_1) {
				Collections.shuffle(pick);

				double after = calculatePickBordersLength(vertexes, route, pick, pickPositions);

				if (before > after) {
					double localImprovement = before - after;
//					System.err.println("After: " + after);

//					if (pickPositions.size() == 2) {
//						if (pickPositions.get(0) + 1 == pickPositions.get(1)) {
//							double trueAfter = 0;
//
//							int a = pickPositions.get(0);
//							int b = pickPositions.get(1);
//							int s1 = route.get(a - 1);
//							int t1 = route.get((a + 1) % route.size());
//							int s2 = route.get(b - 1);
//							int t2 = route.get((b + 1) % route.size());
//
//							Point S1 = vertexes.get(s1).position;
//							Point A = vertexes.get(pick.get(0)).position;
//							Point T1 = vertexes.get(t1).position;
//							Point S2 = vertexes.get(s2).position;
//							Point B = vertexes.get(pick.get(1)).position;
//							Point T2 = vertexes.get(t2).position;
//
//							trueAfter += Point.distance(S1, A);
//							trueAfter += Point.distance(A, B);
//							trueAfter += Point.distance(B, T2);
//							System.err.println("trueAfter = " + trueAfter);
//						}
//					}

					if (localImprovement > bestLocalImprovement) {
						bestLocalImprovement = localImprovement;
						for (int i = 0; i < pick.size(); ++i) {
							bestLocalPick.set(i, pick.get(i));
						}
					}
				}
			}
			improvement += bestLocalImprovement;
			for (int i = 0; i < pick.size(); ++i) {
				route.set(pickPositions.get(i), bestLocalPick.get(i));
			}

//			break;
		}

		return improvement;
	}

	double calculatePickBordersLength(ArrayList<Vertex> vertexes, ArrayList<Integer> route, ArrayList<Integer> pick, ArrayList<Integer> pickPositions) {
		double length = 0;
		for (int i = 0; i < pick.size(); ++i) {
			int indexA = pick.get(i);
			int indexS = pickPositions.get(i) - 1;
			int indexT = (pickPositions.get(i) + 1) % vertexes.size();

			Point S = vertexes.get(route.get(indexS)).position;
			Point A = vertexes.get(indexA).position;
			Point T = vertexes.get(route.get(indexT)).position;

			if (i != 0 && indexS == pickPositions.get(i - 1)) {
				S = vertexes.get(pick.get(i - 1)).position;
			}
			length += Point.distance(S, A);

			if (i != pick.size() - 1 && indexT == pickPositions.get(i + 1)) {
				// ignore here
			} else {
				length += Point.distance(T, A);
			}

		}
		return length;
	}
}
