package optimization.warehouse;

import data_structures.Customer;
import data_structures.Solution;
import data_structures.Warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * User: iiotep9huy
 * Date: 8/18/13
 * Time: 10:09 PM
 * Project: Warehouse
 */
public class GreedySolver {

	public Solution solve(final ArrayList<Warehouse> warehouses, final ArrayList<Customer> customers, ArrayList<Integer> customersOrder) {
		Solution solution = new Solution(warehouses, customers);
		ArrayList<Integer> sortedWarehousesIndexes = new ArrayList<Integer>(warehouses.size());
		for (int i = 0; i < warehouses.size(); ++i) {
			sortedWarehousesIndexes.add(i);
		}

		Collections.sort(sortedWarehousesIndexes, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				double rate1 = warehouses.get(o1).setupCost;
				double rate2 = warehouses.get(o2).setupCost;
//				double rate1 = warehouses.get(o1).setupCost / warehouses.get(o1).capacity;
//				double rate2 = warehouses.get(o2).setupCost / warehouses.get(o2).capacity;
				return Double.compare(rate1, rate2);
			}
		});

		int totalDemand = 0;
		for (int i = 0; i < customers.size(); ++i) {
			totalDemand += customers.get(i).demand;
		}

		int totalCapacity = 0;
		for (int taken = 1; taken <= warehouses.size(); ++taken) {
			int index = sortedWarehousesIndexes.get(taken - 1);
			solution.isSetup.set(index, true);
			totalCapacity += warehouses.get(index).capacity;
			if (totalDemand > totalCapacity) {
				continue;
			}
			boolean canFit = true;

			ArrayList<Integer> residualCapacity = new ArrayList<Integer>(warehouses.size());
			for (int i = 0; i < warehouses.size(); ++i) {
				residualCapacity.add(warehouses.get(i).capacity);
			}
			for (int customerNumber = 0; customerNumber < customers.size(); ++customerNumber) {
				int customerIndex = customersOrder.get(customerNumber);
				int demand = customers.get(customerIndex).demand;
				int bestWarehouse = -1;
				double bestDistance = 0;
				for (int warehouseNumber = 0; warehouseNumber < taken; ++warehouseNumber) {
					int warehouseIndex = sortedWarehousesIndexes.get(warehouseNumber);
					if (residualCapacity.get(warehouseIndex) >= demand) {
						double newDistance = customers.get(customerIndex).distances.get(warehouseIndex);
						if (bestWarehouse == -1 || (newDistance < bestDistance)) {
							bestDistance = newDistance;
							bestWarehouse = warehouseIndex;
						}
					}
				}
				if (bestWarehouse == -1) {
					canFit = false;
					break;
				}

				residualCapacity.set(bestWarehouse, residualCapacity.get(bestWarehouse) - demand);
				solution.customerAssignments.set(customerIndex, bestWarehouse);
			}

			if (canFit) {
				break;
			}
		}

		return solution;
	}
}
