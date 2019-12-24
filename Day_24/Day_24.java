import java.util.*;

public class Day_24 {
	public static void main(String[] args) {
		int size = 5;
		ArrayList<ArrayList<Boolean>> hasBug = new ArrayList<>();
		ArrayList<ArrayList<Boolean>> state = new ArrayList<>();
		int rowNum = 0;
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			hasBug.add(new ArrayList<>());
			state.add(new ArrayList<>());
			for (int col = 0; col < line.length(); col++) {
				if (line.charAt(col) == '#') {
					hasBug.get(rowNum).add(true);
					state.get(rowNum).add(true);
				} else {
					hasBug.get(rowNum).add(false);
					state.get(rowNum).add(false);
				}
			}
			rowNum++;
		}
		sc.close();

		int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
		HashSet<ArrayList<ArrayList<Boolean>>> states = new HashSet<>();

		while (states.add(state)) {
			ArrayList<ArrayList<Boolean>> nextState = new ArrayList<>();
			for (int row = 0; row < size; row++) {
				nextState.add(new ArrayList<>());
				for (int col = 0; col < size; col++) {
					boolean containsBug = state.get(row).get(col);
					int adjacent = 0;
					
					for (int[] dir : directions) {
						int adjRow = row + dir[0];
						int adjCol = col + dir[1];

						if (adjRow < 0 || adjRow >= size || adjCol < 0 || adjCol >= size)
							continue;

						if (state.get(adjRow).get(adjCol))
							adjacent++;
					}

					nextState.get(row).add(adjacent == 1 || (adjacent == 2 && !containsBug));
				}
			}

			state = nextState;
		}

		int answer1 = 0;
		for (int row = 0; row < size; row++)
			for (int col = 0; col < size; col++)
				if (state.get(row).get(col))
					answer1 += (int) Math.pow(2, row * size + col);

		System.out.println("Part 1: " + answer1);

		int half = size / 2;
		HashMap<Integer, ArrayList<ArrayList<Boolean>>> bugSpace = new HashMap<>();
		bugSpace.put(0, hasBug);

		for (int iterate = 0; iterate < 200; iterate++) {
			HashMap<Integer, ArrayList<ArrayList<Boolean>>> newSpace = new HashMap<>();
			int maxDepth = Integer.MIN_VALUE;
			int minDepth = Integer.MAX_VALUE;
			for (int n : bugSpace.keySet()) {
				maxDepth = Math.max(maxDepth, n);
				minDepth = Math.min(minDepth, n);
			}

			for (int depth = minDepth - 1; depth <= maxDepth + 1; depth++) {
				ArrayList<ArrayList<Boolean>> space = new ArrayList<>();
				for (int row = 0; row < size; row++) {
					space.add(new ArrayList<>());
					for (int col = 0; col < size; col++) {
						if (row == half && col == half) {
							space.get(row).add(false);
							continue;
						}

						boolean containsBug = false;
						int adjacent = 0;
						if (bugSpace.containsKey(depth))
							containsBug = bugSpace.get(depth).get(row).get(col);

						for (int i = 0; i < directions.length; i++) {
							int[] dir = directions[i];
							int adjRow = row + dir[0];
							int adjCol = col + dir[1];
							int adjDepth = depth;

							if (adjRow < 0 || adjRow >= size || adjCol < 0 || adjCol >= size) {
								adjDepth--;
								if (!bugSpace.containsKey(adjDepth))
									continue;

								if (bugSpace.get(adjDepth).get(half + dir[0]).get(half + dir[1]))
									adjacent++;
							} else if (adjRow == half && adjCol == half) {
								adjDepth++;
								if (!bugSpace.containsKey(adjDepth))
									continue;

								int constantCoord = (i % 2 == 0) ? 0 : size - 1;
								int innerRow = 0;
								int innerCol = 0;
								for (int otherCoord = 0; otherCoord < size; otherCoord++) {
									if (i < 2) {
										innerRow = constantCoord;
										innerCol = otherCoord;
									} else {
										innerRow = otherCoord;
										innerCol = constantCoord;
									}
									if (bugSpace.get(adjDepth).get(innerRow).get(innerCol))
										adjacent++;
								}
							} else {
								if (!bugSpace.containsKey(depth))
									continue;

								if (bugSpace.get(depth).get(adjRow).get(adjCol))
									adjacent++;
							}

						}

						space.get(row).add(adjacent == 1 || (adjacent == 2 && !containsBug));
					}
				}

				boolean depthContainsBug = false;
				for (ArrayList<Boolean> bugRow : space) {
					for (boolean b : bugRow)
						if (b) {
							depthContainsBug = true;
							break;
						}
					if (depthContainsBug)
						break;
				}
				if (depthContainsBug)
					newSpace.put(depth, space);
			}

			bugSpace = newSpace;
		}

		int answer2 = 0;
		for (ArrayList<ArrayList<Boolean>> depth : bugSpace.values())
			for (ArrayList<Boolean> row : depth)
				for (boolean b : row)
					if (b)
						answer2++;

		System.out.println("Part 2: " + answer2);
	}
}
