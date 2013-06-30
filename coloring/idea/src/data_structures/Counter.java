package data_structures;

import java.util.Collections;

/**
 * User: iiotep9huy
 * Date: 6/30/13
 * Time: 5:23 PM
 * Project: Coloring
 */
public class Counter {
	public Counter(int size) {
		this.size = size;
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
		++counter[value];
	}

	public int get(int value) {
		return counter[value];
	}

	public int findFirstZero() {
		for (int i = 0; i < size; ++i) {
			if (get(i) == 0) {
				return i;
			}
		}
		return -1;
	}

	private int size;
	private int[] counter;
}
