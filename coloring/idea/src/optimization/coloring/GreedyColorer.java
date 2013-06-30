package optimization.coloring;

import data_structures.Counter;
import data_structures.graph.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: iiotep9huy
 * Date: 6/30/13
 * Time: 6:55 PM
 * Project: Coloring
 */
public class GreedyColorer {
	public ArrayList<Integer> color(Graph graph, List<Integer> colorOrder, ArrayList<Integer> initialColors) throws Exception {
		ArrayList<Integer> colors = new ArrayList<Integer>();
		for (int i = 0; i < initialColors.size(); ++i) {
			colors.add(initialColors.get(i));
		}
		int sufficientColorsNumber = graph.getSufficientColorsNumber();
		for (int source : colorOrder) {
			if (colors.get(source) == Constants.NO_COLOR) {
				Counter counter = new Counter(sufficientColorsNumber);
				for (int destination : graph.incidenceList.get(source))	{
					int color = colors.get(destination);
					if (color != Constants.NO_COLOR) {
						counter.increment(color);
					}
				}
				int availableColor = counter.findFirstZero();
				if (availableColor == -1) {
					throw new Exception("Can't paint vertex " + source);
				}
				colors.set(source, availableColor);
			}
		}
		return colors;
	}
}
