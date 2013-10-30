package data_structures;

import optimization.warehouse.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * User: iiotep9huy
 * Date: 8/18/13
 * Time: 10:10 PM
 * Project: Warehouse
 */
public class Solution {

	public Solution(Solution solution) {
		warehouses = solution.warehouses;
		customers = solution.customers;

		cost = 0.0;

		isSetup = new ArrayList<Boolean>(warehouses.size());
		for (int i = 0; i < warehouses.size(); ++i) {
			isSetup.add(solution.isSetup.get(i));
		}

		customerAssignments = new ArrayList<Integer>(customers.size());
		for (int i = 0; i < customers.size(); ++i) {
			customerAssignments.add(solution.customerAssignments.get(i));
		}

		customersNumber = new ArrayList<Integer>(warehouses.size());
		for (int i = 0; i < warehouses.size(); ++i) {
			customersNumber.add(solution.customersNumber.get(i));
		}

		openWarehousesNumber = 0;

		residualCapacity = new ArrayList<Integer>(warehouses.size());
		for (int i = 0; i < warehouses.size(); ++i) {
			residualCapacity.add(solution.residualCapacity.get(i));
		}
	}

	public Solution(ArrayList<Warehouse> warehouses, ArrayList<Customer> customers) {
		this.warehouses = warehouses;
		this.customers = customers;

		cost = 0.0;

		isSetup = new ArrayList<Boolean>(warehouses.size());
		for (int i = 0; i < warehouses.size(); ++i) {
			isSetup.add(false);
		}

		customerAssignments = new ArrayList<Integer>(customers.size());
		for (int i = 0; i < customers.size(); ++i) {
			customerAssignments.add(Constants.NO_ASSIGNMENT);
		}

		customersNumber = new ArrayList<Integer>(warehouses.size());
		for (int i = 0; i < warehouses.size(); ++i) {
			customersNumber.add(0);
		}

		openWarehousesNumber = 0;

		residualCapacity = new ArrayList<Integer>(warehouses.size());
		for (int i = 0; i < warehouses.size(); ++i) {
			residualCapacity.add(warehouses.get(i).capacity);
		}
	}

	public void fillFields() {

		Collections.fill(customersNumber, 0);

		ArrayList<Boolean> realIsSetup = new ArrayList<Boolean>();
		for (int i = 0; i < warehouses.size(); ++i) {
			realIsSetup.add(false);
			residualCapacity.set(i, warehouses.get(i).capacity);
		}

		boolean badAssignment = false;
		for (int i = 0; i < customers.size(); ++i) {
			int assignment = customerAssignments.get(i);
			if (assignment == Constants.NO_ASSIGNMENT) {
				badAssignment = true;
			} else {
				residualCapacity.set(assignment, residualCapacity.get(assignment) - customers.get(i).demand);
				customersNumber.set(assignment, customersNumber.get(assignment) + 1);
				realIsSetup.set(assignment, true);
			}
		}

		openWarehousesNumber = 0;
		for (int i = 0; i < warehouses.size(); ++i) {
			if (realIsSetup.get(i)) {
				++openWarehousesNumber;
			}
			isSetup.set(i, realIsSetup.get(i));
		}
	}

	public double calculateCost() {
		cost = 0;

		for (int i = 0; i < warehouses.size(); ++i) {
			if (isSetup.get(i)) {
				cost += warehouses.get(i).setupCost;
			}
		}

		for (int i = 0; i < customers.size(); ++i) {
			int assignment = customerAssignments.get(i);
			if (assignment != Constants.NO_ASSIGNMENT) {
				cost += customers.get(i).distances.get(assignment);
			}
		}
		return cost;
	}

	public double calculateRealCost() {
		cost = 0;

		ArrayList<Boolean> realIsSetup = new ArrayList<Boolean>();
		for (int i = 0; i < warehouses.size(); ++i) {
			realIsSetup.add(false);
		}

		boolean badAssignment = false;
		for (int i = 0; i < customers.size(); ++i) {
			int assignment = customerAssignments.get(i);
			if (assignment == Constants.NO_ASSIGNMENT) {
				System.err.println("Bad assignment of " + i);
				badAssignment = true;
			} else {
				cost += customers.get(i).distances.get(assignment);
				realIsSetup.set(assignment, true);
			}
		}

		for (int i = 0; i < warehouses.size(); ++i) {

			assert realIsSetup.get(i) == (customersNumber.get(i) > 0);

			if (realIsSetup.get(i)) {
				cost += warehouses.get(i).setupCost;
			}
		}

		return badAssignment ? Double.MAX_VALUE : cost;
	}

	public boolean isValid() {
		for (int i = 0; i < customerAssignments.size(); ++i) {
			if (customerAssignments.get(i) == Constants.NO_ASSIGNMENT) {
				return false;
			}
		}

		ArrayList<Integer> residualCapacity = new ArrayList<Integer>(warehouses.size());
		for (int i = 0; i < warehouses.size(); ++i) {
			residualCapacity.add(warehouses.get(i).capacity);
		}

		for (int i = 0; i < customerAssignments.size(); ++i) {
			int demand = customers.get(i).demand;
			int assignment = customerAssignments.get(i);
			residualCapacity.set(assignment, residualCapacity.get(assignment) - demand);
			if (residualCapacity.get(assignment) < 0) {
				System.err.println("Bad residual capacity");
				return false;
			}
		}

		return true;
	}

	public double getCost() {
		if (change) {
			cost = calculateCost();
			change = false;
		}
		return cost;
	}

	public Integer getCustomerAssignment(int index) {
		return customerAssignments.get(index);
	}

	public void setCustomerAssignment(int index, int customerAssignment) {
		int previousAssignment = customerAssignments.get(index);
		if (previousAssignment != Constants.NO_ASSIGNMENT) {
			cost -= customers.get(index).distances.get(customerAssignment);
		}
		customerAssignments.set(index, customerAssignment);

		if (customerAssignment != Constants.NO_ASSIGNMENT) {
			cost += customers.get(index).distances.get(customerAssignment);
		}
	}

	public Boolean getSetup(int index) {
		return isSetup.get(index);
	}

	public void setSetup(int index, Boolean setup) {
		if (isSetup.get(index) != setup) {
			int sign = 1;
			if (!setup) {
				sign = -1;
			}
			cost += warehouses.get(index).setupCost * sign;
		}
		isSetup.set(index, setup);
	}

	public ArrayList<Warehouse> warehouses;
	public ArrayList<Integer> customersNumber;
	public ArrayList<Customer> customers;
	public ArrayList<Integer> residualCapacity;
	public int openWarehousesNumber;

	public ArrayList<Integer> customerAssignments;
	public ArrayList<Boolean> isSetup;
	double cost;
	boolean change = true;
}
