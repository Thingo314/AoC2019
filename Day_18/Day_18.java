import java.util.*;

public class Day_18 {
	static ArrayList<ArrayList<Character>> vault = new ArrayList<>();
	static int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
	static int[][] diagonals = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
	static HashMap<Character, ArrayList<Integer>> keys = new HashMap<>();

	public static void main(String[] args) {
		int rowNum = 0;
		Scanner sc = new Scanner(System.in);
		ArrayList<Integer> position = new ArrayList<>();
		while (sc.hasNextLine()) {
			vault.add(new ArrayList<>());
			String line = sc.nextLine();
			for (int col = 0; col < line.length(); col++) {
				char c = line.charAt(col);
				vault.get(rowNum).add(c);
				if (Character.isLowerCase(c)) {
					keys.put(c, new ArrayList<>(Arrays.asList(rowNum, col)));
				} else if (c == '@') {
					position.add(rowNum);
					position.add(col);
				}
			}
			rowNum++;
		}
		sc.close();

		ArrayList<ArrayList<Integer>> positions = new ArrayList<>();
		positions.add(position);
		System.out.println("Part 1: " + outerSearch(positions));

		positions.clear();
		vault.get(position.get(0)).set(position.get(1), '#');
		for (int[] dir : directions)
			vault.get(position.get(0) + dir[0]).set(position.get(1) + dir[1], '#');
		for (int[] dir : diagonals) {
			vault.get(position.get(0) + dir[0]).set(position.get(1) + dir[1], '@');
			positions.add(new ArrayList<>(Arrays.asList(position.get(0) + dir[0], position.get(1) + dir[1])));
		}

		System.out.println("Part 2: " + outerSearch(positions));
	}

	static int outerSearch(ArrayList<ArrayList<Integer>> positions) {
		int minSteps = Integer.MAX_VALUE;
		PriorityQueue<SearchNode> nodes = new PriorityQueue<>();
		HashMap<ArrayList<ArrayList<Integer>>, HashMap<HashSet<Character>, Integer>> visited = new HashMap<>();

		SearchNode start = new SearchNode(positions, 0, new HashSet<>());

		nodes.add(start);
		visited.put(positions, new HashMap<>());
		visited.get(positions).put(new HashSet<>(), 0);

		while (nodes.size() > 0) {
			SearchNode node = nodes.remove();
			int currentSteps = node.steps;
			if (currentSteps > minSteps)
				continue;
			
			ArrayList<ArrayList<Integer>> currentPositions = node.pos;
			HashSet<Character> keysFound = node.keysCollected;

			HashMap<Character, ArrayList<Integer>> nextDirectKeys = innerSearch(currentPositions, keysFound);
			for (Map.Entry<Character, ArrayList<Integer>> entry : nextDirectKeys.entrySet()) {
				HashSet<Character> nextKeys = new HashSet<>(keysFound);
				char key = entry.getKey();
				nextKeys.add(key);
				int nextSteps = currentSteps + entry.getValue().get(0);
				if (nextKeys.equals(keys.keySet())) {
					if (minSteps > nextSteps)
						minSteps = nextSteps;
				} else {
					ArrayList<ArrayList<Integer>> nextPositions = new ArrayList<>();
					int indexOfChange = entry.getValue().get(1);
					for (int i = 0; i < currentPositions.size(); i++) {
						if (i == indexOfChange) {
							nextPositions.add(keys.get(key));
						} else {
							nextPositions.add(currentPositions.get(i));
						}
					}

					if (!visited.containsKey(nextPositions))
						visited.put(nextPositions, new HashMap<>());
					if (!visited.get(nextPositions).containsKey(nextKeys))
						visited.get(nextPositions).put(nextKeys, Integer.MAX_VALUE);
					
					int searchedSteps = visited.get(nextPositions).get(nextKeys);
					if (searchedSteps > nextSteps) {
						visited.get(nextPositions).put(nextKeys, nextSteps);
						SearchNode nextNode = new SearchNode(nextPositions, nextSteps, nextKeys);
						nodes.add(nextNode);
					}
				}
			}
		}

		return minSteps;
	}

	static HashMap<Character, ArrayList<Integer>> innerSearch(ArrayList<ArrayList<Integer>> positions, HashSet<Character> keysCollected) {
		// the character maps to the steps from the current position and the index of the current position
		HashMap<Character, ArrayList<Integer>> pathableKeys = new HashMap<>();

		for (int i = 0; i < positions.size(); i++) {
			ArrayList<Integer> pos = positions.get(i);
			HashSet<ArrayList<Integer>> visited = new HashSet<>();
			ArrayDeque<ArrayList<Integer>> positionsQueue = new ArrayDeque<>();
			ArrayDeque<Integer> steps = new ArrayDeque<>();

			visited.add(pos);
			positionsQueue.add(pos);
			steps.add(0);

			while (positionsQueue.size() > 0) {
				ArrayList<Integer> currentPosition = positionsQueue.pop();
				int row = currentPosition.get(0);
				int col = currentPosition.get(1);
				int currentSteps = steps.pop();

				for (int[] dir : directions) {
					int newRow = row + dir[0];
					int newCol = col + dir[1];

					if (newRow < 0 || newRow >= vault.size())
						continue;
					if (newCol < 0 || newCol >= vault.get(0).size())
						continue;

					char space = vault.get(newRow).get(newCol);
					if (space == '#')
						continue;

					ArrayList<Integer> newPos = new ArrayList<>(Arrays.asList(newRow, newCol));
					int newStep = currentSteps + 1;

					if (visited.contains(newPos))
						continue;

					if (Character.isUpperCase(space)) {
						char keyNeeded = Character.toLowerCase(space);
						if (!keysCollected.contains(keyNeeded))
							continue;
					}

					if (Character.isLowerCase(space)) {
						if (!keysCollected.contains(space)) {
							pathableKeys.put(space, new ArrayList<>(Arrays.asList(newStep, i)));
							continue;
						}
					}

					visited.add(newPos);
					positionsQueue.add(newPos);
					steps.add(newStep);
				}
			}
		}

		return pathableKeys;
	}
	
}

class SearchNode implements Comparable<SearchNode> {
	ArrayList<ArrayList<Integer>> pos;
	int steps;
	HashSet<Character> keysCollected;

	public SearchNode(ArrayList<ArrayList<Integer>> p, int s, HashSet<Character> k) {
		pos = p;
		steps = s;
		keysCollected = k;
	}

	public int compareTo(SearchNode sn) {
		if (keysCollected.size() < sn.keysCollected.size())
			return 1;
		if (keysCollected.size() > sn.keysCollected.size())
			return -1;

		if (steps < sn.steps)
			return -1;
		if (steps > sn.steps)
			return 1;

		return 0;
	}
}
