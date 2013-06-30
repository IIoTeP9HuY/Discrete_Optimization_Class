package optimization.coloring;

import data_structures.Counter;
import data_structures.graph.Graph;
import utils.InputReader;
import utils.OutputWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: iiotep9huy
 * Date: 6/30/13
 * Time: 4:23 PM
 * Project: Coloring
 */
public class ColoringSolver {

	static InputReader in;
	static OutputWriter out;
	final static int DEFAULF_ITERATIONS_NUMBER = 100000000;

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

	static ArrayList<Integer> sortedByDegreeOrder(Graph graph) {
		ArrayList<Integer> order = generateSimpleOrder(graph.incidenceList.size());
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

		ArrayList<Integer> vertiocesOrder = generateSimpleOrder(V);

		ArrayList<Integer> colors = new ArrayList<Integer>(V);
		while (colors.size() < V) {
			colors.add(Constants.NO_COLOR);
		}

		GreedyColorer greedyColorer = new GreedyColorer();

		ArrayList<Integer> bestColors = new ArrayList<Integer>();
		int bestColorsNumber = V + 1;
		int iterationsNumber = (int) Math.ceil(DEFAULF_ITERATIONS_NUMBER / (E + V + 5)) + 1;
		for (int i = 0; i < iterationsNumber; ++i) {
			Collections.shuffle(verticesOrder);
			ArrayList<Integer> newColors = greedyColorer.color(graph, verticesOrder, colors);
			int newColorsNumber = findColorsNumber(newColors);

			if (newColorsNumber < bestColorsNumber) {
				System.err.println("Found better: " + newColorsNumber);
				bestColorsNumber = newColorsNumber;
				bestColors = newColors;
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
