package data_structures;

/**
 * User: iiotep9huy
 * Date: 6/30/13
 * Time: 5:23 PM
 * Project: Coloring
 */
public class Counter {
	public Counter(int size) {
		this.size = size;
		this.zeroesNumber = size;
		counter = new int[size];
		clear();
	}

	public void clear() {
		fill(0);
	}

	public void fill(int value) {
		for (int i = 0; i < size; ++i) {
			counter[i] = value;
		}
	}

	public void increment(int value) {
		if (++counter[value] == 1) {
			--zeroesNumber;
		}
	}

	public void decrement(int value) {
		if (--counter[value] == 0) {
			++zeroesNumber;
		}
	}

	public int get(int value) {
		return counter[value];
	}

	public int findFirstZero() {
		return findFirstZeroAfter(-1);
	}

	public int findMinimum() {
		int minPosition = -1;
		for (int i = 0; i < size; ++i) {
			if (minPosition == -1 || counter[minPosition] > counter[i])	{
				minPosition = i;
			}
		}
		return minPosition;
	}

	public int findFirstZeroAfter(int position) {
		for (int i = position + 1; i < size; ++i) {
			if (get(i) == 0) {
				return i;
			}
		}
		return -1;
	}

	public int zeroesNumber() {
		return zeroesNumber;
	}

	private int zeroesNumber;
	private int size;
	private int[] counter;
}
