package optimization.tsp;

import data_structures.Point;
import data_structures.Vertex;
import utils.InputReader;
import utils.OutputWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: iiotep9huy
 * Date: 8/15/13
 * Time: 10:15 AM
 * Project: TSP
 */
public class TSPSolver {

	static InputReader in;
	static OutputWriter out;

	/**
	 * The main class
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: TSPSolver FILENAME");
			return;
		}
		String fileName = args[0];

		in = new InputReader(new FileInputStream(fileName));
		// out = new OutputWriter(new FileOutputStream("output.txt"));
		// in = new InputReader(System.in);
		out = new OutputWriter(System.out);

		try {
			long t1 = System.currentTimeMillis();
			solve();
			long t2 = System.currentTimeMillis();
			System.err.println("Execution time: " + (t2 - t1) / 1000.0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	/**
	 * Read the instance, solve it, and print the solution in the standard output
	 */
	public static void solve() throws Exception {
		int V = in.readInt();

		ArrayList<Vertex> vertexes = new ArrayList<Vertex>();

		for (int i = 0; i < V; ++i) {
			double x = in.readDouble();
			double y = in.readDouble();
			vertexes.add(new Vertex(i, new Point(x, y)));
		}

		double bestLength = 1e9;
		ArrayList<Integer> bestRoute = new ArrayList<Integer>();

		GreedySolver greedySolver = new GreedySolver();
		ArrayList<Integer> greedyRoute = greedySolver.solve(vertexes);
		double greedyLength = findRouteLength(vertexes, greedyRoute);

		if (greedyLength < bestLength) {
			bestLength = greedyLength;
			bestRoute = greedyRoute;
		}

		System.err.println("Greedy length: " + greedyLength);

		SimulatedAnnealingImprover simulatedAnnealingImprover = new SimulatedAnnealingImprover();
		bestLength = TwoOptImproveRoute(vertexes, bestRoute, 10000);
		bestLength -= simulatedAnnealingImprover.improve(vertexes, bestRoute, 650, 0.9997);

		System.err.println("Improved greedy " + bestLength);

//		bestLength = KOptImproveRoute(vertexes, bestRoute, 10000, 4);
//		bestLength = longImproveRoute(vertexes, bestRoute);

		for (int i = 0; i < 10; ++i) {
			RandomSolver randomSolver = new RandomSolver();
			ArrayList<Integer> randomRoute = randomSolver.solve(vertexes);
			double randomLength = findRouteLength(vertexes, randomRoute);

			randomLength -= simulatedAnnealingImprover.improve(vertexes, randomRoute, 12000, 0.99997);
//			System.err.println("Reheat");
//			randomLength -= simulatedAnnealingImprover.improve(vertexes, randomRoute, 200, 0.9999);
//			System.err.println("One more reheat");
//			randomLength -= simulatedAnnealingImprover.improve(vertexes, randomRoute, 10, 0.9999);
			System.err.println("Random length " + randomLength);

			if (randomLength < bestLength) {
				bestLength = randomLength;
				bestRoute = randomRoute;
				System.err.println("Random is better " + randomLength);
			}
		}

//		bestLength = TwoOptImproveRoute(vertexes, bestRoute, 100000);
		bestLength = KOptImproveRoute(vertexes, bestRoute, 100000, 4);
		bestLength = longImproveRoute(vertexes, bestRoute);


		System.err.println("Improved length: " + findRouteLength(vertexes, bestRoute));

//		Random random = new Random(42);
//		for (int it = 0; it < 50; ++it) {
//
//			int firstVertex = random.nextInt(vertexes.size() - 1) + 1;
//			GreedySolverWithChosenFirstVertex greedySolverWithChosenFirstVertex = new GreedySolverWithChosenFirstVertex();
//			ArrayList<Integer> greedyRouteWithFirstChosenVertex = greedySolverWithChosenFirstVertex.solve(vertexes, firstVertex);
//			double basicGreedyLength = findRouteLength(vertexes, greedyRouteWithFirstChosenVertex);
//
//			if (basicGreedyLength > (greedyLength * 11) / 10) {
//				--it;
//				continue;
//			}
//
//			double greedyLengthWithFirstChosenVertex = fastImproveRoute(vertexes, greedyRouteWithFirstChosenVertex);
//
//			if (greedyLengthWithFirstChosenVertex < bestLength) {
//				bestLength = greedyLengthWithFirstChosenVertex;
//				bestRoute = greedyRouteWithFirstChosenVertex;
//
//				System.err.println("GSWFV. Improved: " + bestLength);
//			}
//		}

//		bestLength = longImproveRoute(vertexes, bestRoute);
//		System.err.println("After final improvement: " + bestLength);

		out.printLine(bestLength + " " + 0);
		for (int i = 0; i < bestRoute.size(); ++i) {
			out.print(bestRoute.get(i) + " ");
		}
	}

	static double lsImproveRoute(ArrayList<Vertex> vertexes, ArrayList<Integer> route) {
		double length = findRouteLength(vertexes, route);
		LocalSwapImprover localSwapImprover = new LocalSwapImprover();
		while (true) {
			double improvement = localSwapImprover.improve(vertexes, route);
			if (improvement < 1e-6) {
				break;
			}
			length -= improvement;
			System.err.println("LS. Improvement " + improvement);
		}
		return length;
	}

	static double rsImproveRoute(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int iterationsNumber) {
		double length = findRouteLength(vertexes, route);
		RandomSwapImprover randomSwapImprover = new RandomSwapImprover();
		while (true) {
			double improvement = randomSwapImprover.improve(vertexes, route, iterationsNumber);
			if (improvement < 1e-6) {
				break;
			}
			length -= improvement;
			System.err.println("RS. Improvement " + improvement);
		}
		return length;
	}

	static double lpImproveRoute(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int permutationSize) {
		LocalPermutationImprover localPermutationImprover = new LocalPermutationImprover();

		double length = findRouteLength(vertexes, route);
		int improvementIterations = 10;
		while (true && (--improvementIterations > 0)) {
			double improvement = localPermutationImprover.improve(vertexes, route, permutationSize);
			if (improvement < 1e-6) {
				break;
			}
			length -= improvement;
			System.err.println("LP. Improvement " + improvement);
		}
		return length;
	}

	static double rpImproveRoute(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int permutationSize, int iterationsNumber) {
		RandomPermutationImprover randomPermutationImprover = new RandomPermutationImprover();

		double length = findRouteLength(vertexes, route);
		int improvementIterations = 10;
		while (true && (--improvementIterations > 0)) {
			double improvement = randomPermutationImprover.improve(vertexes, route, permutationSize, iterationsNumber);
			if (improvement < 1e-6) {
				break;
			}
			length -= improvement;
			System.err.println("RP. Improvement " + improvement);
			System.err.println("True length: " + findRouteLength(vertexes, route));
		}
		return length;
	}

	static double TwoOptImproveRoute(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int iterationsNumber) {
		TwoOptImprover twoOptImprover = new TwoOptImprover();

		double length = findRouteLength(vertexes, route);
		int improvementIterations = 10;
		while (true && (--improvementIterations > 0)) {
			double improvement = twoOptImprover.improve(vertexes, route, iterationsNumber);
			if (improvement < 1e-6) {
				break;
			}
			length -= improvement;
			System.err.println("2opt. Improvement " + improvement);
			assert findRouteLength(vertexes, route) == length;
		}
		return length;
	}

	static double KOptImproveRoute(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int iterationsNumber, int k) {
		KOptImprover kOptImprover = new KOptImprover();

		double length = findRouteLength(vertexes, route);
		int improvementIterations = 12;
		while (true && (--improvementIterations > 0)) {
			double improvement = kOptImprover.improve(vertexes, route, iterationsNumber, k);
			if (improvement < 1e-6) {
				break;
			}
			length -= improvement;
			System.err.println("Kopt[" + k + "]. Improvement " + improvement);
			assert findRouteLength(vertexes, route) == length;
		}
		return length;
	}

	static double fastImproveRoute(ArrayList<Vertex> vertexes, ArrayList<Integer> route) {
		rpImproveRoute(vertexes, route, 10, 1000);
		rpImproveRoute(vertexes, route, 7, 1000);
		rpImproveRoute(vertexes, route, 5, 1000);
		rpImproveRoute(vertexes, route, 3, 1000);
		lpImproveRoute(vertexes, route, 10);
		lpImproveRoute(vertexes, route, 8);
		lpImproveRoute(vertexes, route, 7);
		lpImproveRoute(vertexes, route, 6);
		lpImproveRoute(vertexes, route, 4);
		rsImproveRoute(vertexes, route, 100000);
		double length = lsImproveRoute(vertexes, route);
		return length;
	}

	static double longImproveRoute(ArrayList<Vertex> vertexes, ArrayList<Integer> route) {
		rpImproveRoute(vertexes, route, 10, 10000);
		rpImproveRoute(vertexes, route, 7, 10000);
		rpImproveRoute(vertexes, route, 5, 10000);
		rpImproveRoute(vertexes, route, 3, 10000);
		lpImproveRoute(vertexes, route, 10);
		lpImproveRoute(vertexes, route, 8);
		lpImproveRoute(vertexes, route, 7);
		lpImproveRoute(vertexes, route, 6);
		lpImproveRoute(vertexes, route, 4);
		rsImproveRoute(vertexes, route, 1000000);
		double length = lsImproveRoute(vertexes, route);
		return length;
	}

	static double findRouteLength(ArrayList<Vertex> vertexes, ArrayList<Integer> route) {
		if (route.size() <= 1) {
			return 0;
		}

		double length = 0;
		for (int i = 0; i < route.size() - 1; ++i) {
			length += Point.distance(vertexes.get(route.get(i)).position, vertexes.get(route.get(i + 1)).position);
		}
		length += Point.distance(vertexes.get(route.get(route.size() - 1)).position, vertexes.get(route.get(0)).position);
		return length;
	}

}
