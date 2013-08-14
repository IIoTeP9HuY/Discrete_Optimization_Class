package optimization.coloring;

import data_structures.graph.Graph;
import utils.InputReader;
import utils.OutputWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * User: iiotep9huy
 * Date: 6/30/13
 * Time: 4:23 PM
 * Project: Coloring
 */
public class ColoringSolver {

	static InputReader in;
	static OutputWriter out;
	final static int DEFAULF_ITERATIONS_NUMBER = 20000000;

	/**
	 * The main class
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: ColoringSolver FILENAME");
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

	static ArrayList<Integer> generateSimpleOrder(int V) {
		ArrayList<Integer> order = new ArrayList<Integer>();
		for (int i = 0; i < V; ++i) {
			order.add(i);
		}
		return order;
	}

	static ArrayList<Integer> generateRandomVerticesOrder(int V) {
		ArrayList<Integer> order = generateSimpleOrder(V);
		Collections.shuffle(order);
		return order;
	}

	static ArrayList<Integer> sortedByDegreeOrder(final Graph graph) {
		ArrayList<Integer> order = generateSimpleOrder(graph.incidenceList.size());
		Collections.sort(order, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return Integer.compare(-graph.incidenceList.get(o1).size(), -graph.incidenceList.get(o2).size());
			}
		});
		return order;
	}

	/**
	 * Read the instance, solve it, and print the solution in the standard output
	 */
	public static void solve() throws Exception {
		int V = in.readInt();
		int E = in.readInt();
		Graph graph = new Graph(V);
		for (int i = 0; i < E; ++i) {
			int firstVertex = in.readInt();
			int secondVertex = in.readInt();
			graph.addEdge(firstVertex, secondVertex);
		}

//		ArrayList<Integer> verticesOrder = generateSimpleOrder(V);
		ArrayList<Integer> verticesOrder = sortedByDegreeOrder(graph);

		ArrayList<Integer> colors = new ArrayList<Integer>(V);
		while (colors.size() < V) {
			colors.add(Constants.NO_COLOR);
		}

		GreedyColorer greedyColorer = new GreedyColorer();

		ArrayList<Integer> bestColors = new ArrayList<Integer>();
		int bestColorsNumber = V + 1;
		int iterationsNumber = 10;
		for (int i = 0; i < iterationsNumber; ++i) {
			greedyColorer.setBestColorsNumber(bestColorsNumber);
			try {
				ArrayList<Integer> newColors = greedyColorer.color(graph, verticesOrder, colors);
				int newColorsNumber = findColorsNumber(newColors);

				if (newColorsNumber < bestColorsNumber) {
					System.err.println("Found better: " + newColorsNumber);
					bestColorsNumber = newColorsNumber;
					bestColors = newColors;
				}
			} catch (WorseColoringException e) {
			}
			Collections.shuffle(verticesOrder);
		}

		BranchAndPruneColorer branchAndPruneColorer = new BranchAndPruneColorer();
		while (true) {
			try {
				ArrayList<Integer> branchAndPruneColors = branchAndPruneColorer.color(graph, bestColorsNumber - 1);
				bestColors = branchAndPruneColors;
				--bestColorsNumber;
				System.err.println("BP. Found better: " + bestColorsNumber);
			} catch (ImpossibleColoringException e) {
				break;
			}
		}

		int CRIterationsNumber = (int) Math.ceil(DEFAULF_ITERATIONS_NUMBER / (E + V + 5)) + 1;
		System.err.println("IT: " + CRIterationsNumber);
		ConflictResolvingColorer conflictResolvingColorer = new ConflictResolvingColorer();
		while (true) {
			try {
				ArrayList<Integer> conflictResolverColors = conflictResolvingColorer.color(graph, bestColorsNumber - 1, CRIterationsNumber);
				bestColors = conflictResolverColors;
				--bestColorsNumber;
				System.err.println("CR. Found better: " + bestColorsNumber);
			} catch (ImpossibleColoringException e) {
				break;
			}
		}

		out.printLine(bestColorsNumber + " " + 0);
		for (int i = 0; i < V; ++i) {
			out.print(bestColors.get(i) + " ");
		}
		out.printLine();
	}

	static int findColorsNumber(ArrayList<Integer> colors) {
		int maxColor = 0;
		for (int color : colors) {
			maxColor = Math.max(maxColor, color);
		}
		return maxColor + 1;
	}
}
