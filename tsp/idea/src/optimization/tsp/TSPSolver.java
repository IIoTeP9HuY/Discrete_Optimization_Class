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

		System.err.println("True length before: " + findRouteLength(vertexes, bestRoute));

		bestLength = complexImproveRoute(vertexes, bestRoute, 10, 10000);
		bestLength = complexImproveRoute(vertexes, bestRoute, 7, 10000);
		bestLength = complexImproveRoute(vertexes, bestRoute, 5, 10000);
		bestLength = complexImproveRoute(vertexes, bestRoute, 3, 10000);
		bestLength = simpleImproveRoute(vertexes, bestRoute, 10);
		bestLength = simpleImproveRoute(vertexes, bestRoute, 8);
		bestLength = simpleImproveRoute(vertexes, bestRoute, 7);
		bestLength = simpleImproveRoute(vertexes, bestRoute, 6);
		bestLength = simpleImproveRoute(vertexes, bestRoute, 4);

		System.err.println("Length after improvement: " + findRouteLength(vertexes, bestRoute));

		RandomSwapImprover randomSwapImprover = new RandomSwapImprover();
		while (true) {
			double improvement = randomSwapImprover.improve(vertexes, bestRoute, 1000000);
			if (improvement < 1e-6) {
				break;
			}
			bestLength -= improvement;
			System.err.println("RS. Improvement " + improvement);
		}

		LocalSwapImprover localSwapImprover = new LocalSwapImprover();
		while (true) {
			double improvement = localSwapImprover.improve(vertexes, bestRoute);
			if (improvement < 1e-6) {
				break;
			}
			bestLength -= improvement;
			System.err.println("LS. Improvement " + improvement);
		}

		out.printLine(bestLength + " " + 0);
		for (int i = 0; i < bestRoute.size(); ++i) {
			out.print(bestRoute.get(i) + " ");
		}
	}

	static double simpleImproveRoute(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int permutationSize) {
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

	static double complexImproveRoute(ArrayList<Vertex> vertexes, ArrayList<Integer> route, int permutationSize, int iterationsNumber) {
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
