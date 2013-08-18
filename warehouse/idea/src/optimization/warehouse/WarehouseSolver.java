package optimization.warehouse;

import utils.InputReader;
import utils.OutputWriter;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * User: iiotep9huy
 * Date: 8/18/13
 * Time: 12:29 PM
 * Project: Warehouse
 */
public class WarehouseSolver {

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

	}
}
