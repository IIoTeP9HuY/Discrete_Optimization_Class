package optimization.coloring;

import data_structures.Counter;
import data_structures.graph.Graph;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: iiotep9huy
 * Date: 8/14/13
 * Time: 9:00 PM
 * Project: Coloring
 */
public class ConflictResolvingColorer {

	public ArrayList<Integer> color(Graph graph, int colorsNumber, int iterationsNumber) throws ImpossibleColoringException {
		ArrayList<Integer> colors = new ArrayList<Integer>();
		Random random = new Random();
		for (int j = 0; j < 2; ++j) {
			colors.clear();
			for (int i = 0; i < graph.size(); ++i) {
				colors.add(random.nextInt(colorsNumber));
			}

			Counter counter = new Counter(colorsNumber);
			for (int i = 0; i < iterationsNumber; ++i) {
				for (int v = 0; v < graph.size(); ++v) {
					counter.clear();
					for (int u : graph.incidenceList.get(v)) {
						counter.increment(colors.get(u));
					}
					int bestColor = counter.findMinimum();
					int conflictsNumber = counter.get(bestColor);
					ArrayList<Integer> options = new ArrayList<Integer>();
					for (int c = 0; c < colorsNumber; ++c) {
						if (counter.get(c) == conflictsNumber && (c != colors.get(v))) {
							options.add(c);
						}
					}
					if (options.size() > 0) {
						bestColor = options.get(random.nextInt(options.size()));
					}
					colors.set(v, bestColor);
				}

				int conflictsNumber = 0;
				boolean badColoring = false;
				for (int v = 0; v < graph.size(); ++v) {
					for (int u : graph.incidenceList.get(v)) {
						if (colors.get(v) ==  colors.get(u)) {
							badColoring = true;
							++conflictsNumber;
						}
					}
				}
				if (!badColoring) {
//					System.err.println("Restarts number: " + j);
//					System.err.println("Iterations number: " + i);
					return colors;
				}
//				System.err.println("Conflicts number: " + conflictsNumber);
			}
		}

		throw new ImpossibleColoringException();
	}
}
