package optimization.warehouse;

import data_structures.Customer;
import data_structures.Solution;
import data_structures.Warehouse;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: iiotep9huy
 * Date: 8/19/13
 * Time: 8:18 PM
 * Project: Warehouse
 */
public class SimulatedAnnealingSolver {

	static Random random = new Random(42);

	boolean accept(double delta, double temperature) {
		return random.nextDouble() <= Math.exp(-delta / temperature);
	}

	public Solution solve(ArrayList<Warehouse> warehouses, ArrayList<Customer> customers, double startingTemperature, double alpha,
	                      Solution startingSolution) {

		Solution bestSolution = new Solution(startingSolution);
		double bestCost = bestSolution.calculateCost();

		double temperature = startingTemperature;
		Solution solution = new Solution(startingSolution);
		solution.fillFields();

		double currentCost = startingSolution.calculateCost();
		int iterationsNumber = 0;

		while (temperature > 1e-2) {

			int effective = 0;

			for (int it = 0; it < 20000; ++it) {
				int action = random.nextInt(5);

				if (currentCost < bestCost) {
					bestCost = currentCost;
					System.err.println("Found better: " + bestCost);
					bestSolution = new Solution(solution);
				}

				double newCost = currentCost;
				switch (action) {
					case 0: { // Open random house
						int warehouseIndex = random.nextInt(warehouses.size());
						if (solution.isSetup.get(warehouseIndex) == true) {
							continue;
						}
						newCost = currentCost + warehouses.get(warehouseIndex).setupCost;
						if (accept(newCost - currentCost, temperature)) {
							solution.isSetup.set(warehouseIndex, true);
							currentCost = newCost;
							++solution.openWarehousesNumber;
						}
						++effective;
					}
					case 1: { // Close random house
						int warehouseIndex = random.nextInt(warehouses.size());
						if (solution.isSetup.get(warehouseIndex) == false || solution.customersNumber.get(warehouseIndex) > 0) {
							continue;
						}
						newCost = currentCost - warehouses.get(warehouseIndex).setupCost;
						if (accept(newCost - currentCost, temperature)) {
							solution.isSetup.set(warehouseIndex, false);
							currentCost = newCost;
							--solution.openWarehousesNumber;
						}
						++effective;
					}
					case 2: { // Swap random customers

						int firstCustomerIndex = random.nextInt(customers.size());
						int secondCustomerIndex = random.nextInt(customers.size());
						Customer firstCustomer = customers.get(firstCustomerIndex);
						Customer secondCustomer = customers.get(secondCustomerIndex);

						if (firstCustomerIndex == secondCustomerIndex) {
							continue;
						}

						int firstAssignment = solution.customerAssignments.get(firstCustomerIndex);
						int secondAssignment = solution.customerAssignments.get(secondCustomerIndex);
						if (firstAssignment == secondAssignment) {
							continue;
						}

						int firstDemand = firstCustomer.demand;
						int secondDemand = secondCustomer.demand;
						int firstCapacity = solution.residualCapacity.get(firstAssignment);
						int secondCapacity = solution.residualCapacity.get(secondAssignment);

						if (firstCapacity + firstDemand - secondDemand < 0) {
							continue;
						}

						if (secondCapacity + secondDemand - firstDemand < 0) {
							continue;
						}

						newCost = currentCost - firstCustomer.distances.get(firstAssignment)
								- secondCustomer.distances.get(secondAssignment)
								+ firstCustomer.distances.get(secondAssignment)
								+ secondCustomer.distances.get(firstAssignment);

						if (accept(newCost - currentCost, temperature)) {
							currentCost = newCost;

							solution.customerAssignments.set(firstCustomerIndex, secondAssignment);
							solution.customerAssignments.set(secondCustomerIndex, firstAssignment);
							solution.residualCapacity.set(firstAssignment, firstCapacity + firstDemand - secondDemand);
							solution.residualCapacity.set(secondAssignment, secondCapacity + secondDemand - firstDemand);

						}
						++effective;
					}
					case 3: { // Move random customer to random place

						int customerIndex = random.nextInt(customers.size());
						int warehouseIndex = random.nextInt(warehouses.size());
						Customer customer = customers.get(customerIndex);

						if (!solution.isSetup.get(warehouseIndex)) {
							continue;
						}

						if (solution.customerAssignments.get(customerIndex) == warehouseIndex) {
							continue;
						}

						if (solution.residualCapacity.get(warehouseIndex) < customer.demand) {
							continue;
						}

						int oldAssignment = solution.customerAssignments.get(customerIndex);

						newCost = currentCost - customer.distances.get(oldAssignment) + customer.distances.get(warehouseIndex);

						if (accept(newCost - currentCost, temperature)) {
							currentCost = newCost;
							solution.customerAssignments.set(customerIndex, warehouseIndex);
							solution.customersNumber.set(warehouseIndex, solution.customersNumber.get(warehouseIndex) + 1);
							solution.customersNumber.set(oldAssignment, solution.customersNumber.get(oldAssignment) - 1);
							solution.residualCapacity.set(warehouseIndex, solution.residualCapacity.get(warehouseIndex) - customer.demand);
							solution.residualCapacity.set(oldAssignment, solution.residualCapacity.get(oldAssignment) + customer.demand);
						}
						++effective;
					}
					case 4: { // Open and close random houses
						int warehouseToOpenIndex = random.nextInt(warehouses.size());
						if (solution.isSetup.get(warehouseToOpenIndex) == true) {
							continue;
						}

						int warehouseToCloseIndex = random.nextInt(warehouses.size());
						if (solution.isSetup.get(warehouseToCloseIndex) == false || solution.customersNumber.get(warehouseToCloseIndex) > 0) {
							continue;
						}

						newCost = currentCost + warehouses.get(warehouseToOpenIndex).setupCost - warehouses.get(warehouseToCloseIndex).setupCost;

						if (accept(newCost - currentCost, temperature)) {
							solution.isSetup.set(warehouseToOpenIndex, true);
							solution.isSetup.set(warehouseToCloseIndex, false);
							currentCost = newCost;
						}
						++effective;
					}
				}
			}

			if (currentCost < bestCost) {
				bestCost = currentCost;
				System.err.println("Found better: " + bestCost);
				bestSolution = new Solution(solution);
			}

			if (iterationsNumber % 1000 == 0) {
				System.err.println(String.format("Cost: %12.3f | Temperature: %6.3f", currentCost, temperature));
//				System.err.println("Real cost: " + solution.calculateRealCost());
			}
			++iterationsNumber;
			temperature *= alpha;
		}

		return bestSolution;
	}
}
