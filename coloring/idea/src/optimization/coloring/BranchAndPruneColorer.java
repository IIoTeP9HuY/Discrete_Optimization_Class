package optimization.coloring;

import data_structures.Counter;
import data_structures.graph.Graph;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * User: iiotep9huy
 * Date: 7/6/13
 * Time: 9:12 AM
 * Project: Coloring
 */
public class BranchAndPruneColorer {

	final int MAX_ITERATIONS_NUMBER = 5000000;
	int iterationsNumber = 0;

	class VertexState implements Comparable<VertexState> {

		VertexState(int index, int zeroesNumber, int vertexDegree) {
			this.index = index;
			this.zeroesNumber = zeroesNumber;
			this.vertexDegree = vertexDegree;
		}

		public int index;
		public int zeroesNumber;
		public int vertexDegree;

		@Override
		public int compareTo(VertexState o) {
//			if (zeroesNumber == o.zeroesNumber)
//				return Integer.compare(vertexDegree, o.vertexDegree);
			if (zeroesNumber == o.zeroesNumber)
				return Integer.compare(index, o.index);
			return Integer.compare(zeroesNumber, o.zeroesNumber);
		}
	};

	public ArrayList<Integer> color(Graph graph, int colorsNumber) throws ImpossibleColoringException {
		ArrayList<Integer> colors = new ArrayList<Integer>();
		for (int i = 0; i < graph.size(); ++i) {
			colors.add(Constants.NO_COLOR);
		}
		ArrayList<Counter> availableColors = new ArrayList<Counter>();
		for (int i = 0; i < graph.size(); ++i) {
			availableColors.add(new Counter(colorsNumber));
		}

		TreeSet<VertexState> vertexStateTreeSet = new TreeSet<VertexState>();
		for (int i = 0; i < graph.size(); ++i) {
			vertexStateTreeSet.add(new VertexState(i, availableColors.get(i).zeroesNumber(), graph.vertexDegree(i)));
		}

		if (branchAndPrune(graph, colors, availableColors, -1, vertexStateTreeSet)) {
			return colors;
		} else {
			throw new ImpossibleColoringException();
		}
	}

	boolean branchAndPrune(Graph graph, ArrayList<Integer> colors, ArrayList<Counter> availableColors, int maxUsedColor,
	                       TreeSet<VertexState> vertexStateTreeSet)
			throws ImpossibleColoringException {


		if (++iterationsNumber > MAX_ITERATIONS_NUMBER) {
			throw new ImpossibleColoringException();
		}

		if (vertexStateTreeSet.isEmpty()) {
			for (int i = 0; i < graph.size(); ++i) {
				if (colors.get(i) == Constants.NO_COLOR) {
					throw new RuntimeException("Vertex " + i + " has no color!");
				}
			}
			return true;
		}

		int toughestVertex = vertexStateTreeSet.pollFirst().index;
		Counter currentCounter = availableColors.get(toughestVertex);
		int currentColor = currentCounter.findFirstZero();
		while (currentColor != -1) {

			colors.set(toughestVertex, currentColor);
			for (int v : graph.incidenceList.get(toughestVertex)) {
				++iterationsNumber;
				if (colors.get(v) == Constants.NO_COLOR) {
					vertexStateTreeSet.remove(new VertexState(v, availableColors.get(v).zeroesNumber(), graph.vertexDegree(v)));
				}
				availableColors.get(v).increment(currentColor);
				if (colors.get(v) == Constants.NO_COLOR) {
					vertexStateTreeSet.add(new VertexState(v, availableColors.get(v).zeroesNumber(), graph.vertexDegree(v)));
				}
			}
			if (branchAndPrune(graph, colors, availableColors, Math.max(maxUsedColor, currentColor), vertexStateTreeSet)) {
				return true;
			}
			for (int v : graph.incidenceList.get(toughestVertex)) {
				++iterationsNumber;
				if (colors.get(v) == Constants.NO_COLOR) {
					vertexStateTreeSet.remove(new VertexState(v, availableColors.get(v).zeroesNumber(), graph.vertexDegree(v)));
				}
				availableColors.get(v).decrement(currentColor);
				if (colors.get(v) == Constants.NO_COLOR) {
					vertexStateTreeSet.add(new VertexState(v, availableColors.get(v).zeroesNumber(), graph.vertexDegree(v)));
				}
			}
			colors.set(toughestVertex, Constants.NO_COLOR);

			if (currentColor > maxUsedColor) {
				break;
			}

			currentColor = currentCounter.findFirstZeroAfter(currentColor);
		}

		vertexStateTreeSet.add(new VertexState(toughestVertex, currentCounter.zeroesNumber(), graph.vertexDegree(toughestVertex)));

		return false;
	}
}


