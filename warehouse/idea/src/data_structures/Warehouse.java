package data_structures;

/**
 * User: iiotep9huy
 * Date: 8/18/13
 * Time: 9:34 PM
 * Project: Warehouse
 */
public class Warehouse {
	public Warehouse() {
	}

	public Warehouse(int capacity, double setupCost) {
		this.capacity = capacity;
		this.setupCost = setupCost;
	}

	public int index;
	public int capacity;
	public double setupCost;
}
