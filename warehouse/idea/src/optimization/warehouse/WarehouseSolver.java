package optimization.warehouse;

import data_structures.Customer;
import data_structures.Solution;
import data_structures.Warehouse;
import utils.InputReader;
import utils.OutputWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * User: iiotep9huy
 * Date: 8/18/13
 * Time: 12:29 PM
 * Project: Warehouse
 */
public class WarehouseSolver {

	static InputReader in;
	static OutputWriter out;

	static Random random = new Random(42);

	/**
	 * The main class
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: WarehouseSolver FILENAME");
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
		int warehousesNumber = in.readInt();
		int customersNumber = in.readInt();

		ArrayList<Warehouse> warehouses = new ArrayList<Warehouse>(warehousesNumber);
		for (int i = 0; i < warehousesNumber; ++i) {
			Warehouse warehouse = new Warehouse();
			warehouse.index = i;
			warehouse.capacity = in.readInt();
			warehouse.setupCost = in.readDouble();
			warehouses.add(warehouse);
		}

		ArrayList<Customer> customers = new ArrayList<Customer>(customersNumber);
		for (int i = 0; i < customersNumber; ++i) {
			Customer customer = new Customer();
			customer.index = i;
			customer.demand = in.readInt();
			customer.distances = new ArrayList<Double>(warehousesNumber);
			for (int j = 0; j < warehousesNumber; ++j) {
				customer.distances.add(in.readDouble());
			}
			customers.add(customer);
		}

		Solution bestSolution = new Solution(warehouses, customers);

		ArrayList<Integer> shuffledCustomersOrder = new ArrayList<Integer>(customers.size());
		for (int i = 0; i < customers.size(); ++i) {
			shuffledCustomersOrder.add(i);
		}

		for (int it = 0; it < 10000; ++it) {
			GreedySolver greedySolver = new GreedySolver();
			Solution solution = greedySolver.solve(warehouses, customers, shuffledCustomersOrder);

//			System.err.println("isValid " + solution.isValid());
			if (solution.isValid()) {
				if (!bestSolution.isValid() || (solution.getCost() < bestSolution.getCost())) {
					bestSolution = solution;
				}
			}

			Collections.shuffle(shuffledCustomersOrder, random);
		}

		SimulatedAnnealingSolver simulatedAnnealingSolver = new SimulatedAnnealingSolver();
		for (int i = 0; i < 20; ++i) {
			bestSolution = simulatedAnnealingSolver.solve(warehouses, customers, 2000, 0.9997, bestSolution);

			System.err.println("Best cost: " + bestSolution.calculateRealCost());

//			if (bestSolution.calculateRealCost() < 976740) {
//				break;
//			}
		}

		out.printLine(bestSolution.calculateRealCost() + " " + 0);
		for (int i = 0; i < customers.size(); ++i) {
			out.print(bestSolution.customerAssignments.get(i) + " ");
		}
	}
}
