package optimization.tsp;

import data_structures.Point;
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

	static class Vertex {
		Vertex(int index, Point position) {
			this.index = index;
			this.position = position;
		}

		int index;
		Point position;
	}

	/**
	 * Read the instance, solve it, and print the solution in the standard output
	 */
	public static void solve() throws Exception {
		int V = in.readInt();

		ArrayList<Vertex> vertexes = new ArrayList<Vertex>();

		for (int i = 0; i < V; ++i) {
			int x = in.readInt();
			int y = in.readInt();
			vertexes.add(new Vertex(i, new Point(x, y)));
		}
	}


}
