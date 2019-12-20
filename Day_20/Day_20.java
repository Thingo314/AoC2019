import java.util.*;

public class Day_20 {
	public static void main(String[] args) {
		ArrayList<ArrayList<Character>> donut = new ArrayList<>();
		HashMap<String, HashMap<ArrayList<Integer>, Integer>> portals = new HashMap<>();
		Scanner sc = new Scanner(System.in);
		int rowNum = 0;
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			ArrayList<Character> row = new ArrayList<>();
			for (char c : line.toCharArray())
				row.add(c);
			donut.add(row);
			rowNum++;
		}
		sc.close();

		for (rowNum = 0; rowNum < donut.size(); rowNum++) {
			ArrayList<Character> row = donut.get(rowNum);
			for (int i = 0; i < row.size(); i++) {
				char c = donut.get(rowNum).get(i);
				if (Character.isLetter(c)) {
					if (rowNum <= 0)
						continue;
					char up = donut.get(rowNum - 1).get(i);
					char right = ' ';
					if (i + 1 < row.size())
						right = row.get(i + 1);
					if (Character.isLetter(right)) {
						String portal = c + "" + row.get(i + 1);
						int left = i - 1;
						if (left < 0) {
							addPortal(portals, portal, rowNum, i + 2, -1);
						} else {
							int level = -1;
							if (left != row.size() - 3)
								level = 1;
							if (row.get(left) == '.') {
								addPortal(portals, portal, rowNum, left, level);
							} else {
								addPortal(portals, portal, rowNum, i + 2, level);
							}
						}
					} else if (Character.isLetter(up)) {
						String portal = up + "" + c;
						int above = rowNum - 2;
						if (above < 0) {
							addPortal(portals, portal, rowNum + 1, i, -1);
						} else {
							int level = -1;
							if (above != donut.size() - 3)
								level = 1;
							if (donut.get(above).get(i) == '.') {
								addPortal(portals, portal, above, i, level);
							} else {
								addPortal(portals, portal, rowNum + 1, i, level);
							}
						}
					}
				}
			}
		}
		ArrayList<Integer> start = new ArrayList<>(portals.get("AA").keySet()).get(0);
		HashMap<ArrayList<Integer>, ArrayList<Integer>> visited = new HashMap<>();
		ArrayDeque<ArrayList<Integer>> positions = new ArrayDeque<>();
		ArrayDeque<Integer> steps = new ArrayDeque<>();
		ArrayDeque<Integer> levels = new ArrayDeque<>();

		visited.put(start, new ArrayList<>(Arrays.asList(0)));
		positions.add(start);
		steps.add(0);
		levels.add(0);

		int[][] directions = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
		int answer1 = 0;
		int answer2 = 0;
		boolean found1 = false;
		boolean found2 = false;

		while (!found1 || !found2) {
			int currentStep = steps.pop();
			int currentLevel = levels.pop();
			ArrayList<Integer> currentPos = positions.pop();
			int row = currentPos.get(0);
			int col = currentPos.get(1);

			for (int i = 0; i < directions.length; i++) {
				int newRow = row + directions[i][0];
				int newCol = col + directions[i][1];
				int newLevel = currentLevel;
				ArrayList<Integer> newPos = new ArrayList<Integer>(Arrays.asList(newRow, newCol));

				char space = donut.get(newRow).get(newCol);
				if (space == '#')
					continue;

				if (Character.isLetter(space)) {
					int otherRow = newRow + directions[i][0];
					int otherCol = newCol + directions[i][1];
					char other = donut.get(otherRow).get(otherCol);

					String portal = space + "";
					if (i < 2) {
						portal += other;
					} else {
						portal = other + portal;
					}
					if (portal.equals("AA"))
						continue;
					if (portal.equals("ZZ")) {
						if (!found1) {
							answer1 = currentStep;
							found1 = true;
						}
						if (currentLevel == 0) {
							answer2 = currentStep;
							found2 = true;
						}
						if (found1 && found2) {
							break;
						} else {
							continue;
						}
					}

					ArrayList<ArrayList<Integer>> portalPositions = new ArrayList<>(portals.get(portal).keySet());
					int index = portalPositions.indexOf(currentPos);
					index = (index + 1) % 2;
					newPos = portalPositions.get(index);
					newLevel += portals.get(portal).get(currentPos);
				}
				if (newLevel < 0)
					continue;

				if (visited.containsKey(newPos))
					if (visited.get(newPos).contains(newLevel))
						continue;

				if (!visited.containsKey(newPos))
					visited.put(newPos, new ArrayList<>());

				visited.get(newPos).add(newLevel);
				positions.add(newPos);
				steps.add(currentStep + 1);
				levels.add(newLevel);
			}

			if (found1 && found2)
				break;
		}
		System.out.println("Part 1: " + answer1);
		System.out.println("Part 2: " + answer2);
	}

	static void addPortal(HashMap<String, HashMap<ArrayList<Integer>, Integer>> portals, String portal, int r, int c, int l) {
		if (!portals.containsKey(portal))
			portals.put(portal, new HashMap<>());

		ArrayList<Integer> position = new ArrayList<>(Arrays.asList(r, c));
		portals.get(portal).put(position, l);
	}
}
