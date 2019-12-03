import java.util.*;

public class Day_03 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String[] wire1 = sc.nextLine().split(",");
		String[] wire2 = sc.nextLine().split(",");
		sc.close();
		HashMap<ArrayList<Integer>, Integer> wire1Path = pathThrough(wire1);
		HashMap<ArrayList<Integer>, Integer> wire2Path = pathThrough(wire2);

		int closestIntersectionDist = Integer.MAX_VALUE;
		int lowestSteps = Integer.MAX_VALUE;
		for (ArrayList<Integer> position : wire1Path.keySet()) {
			if (wire2Path.containsKey(position)) {
				int dist = Math.abs(position.get(0)) + Math.abs(position.get(1));
				closestIntersectionDist = Math.min(closestIntersectionDist, dist);
				int steps = wire1Path.get(position) + wire2Path.get(position);
				lowestSteps = Math.min(lowestSteps, steps);
			}

		}

		System.out.println("Part 1: " + closestIntersectionDist);
		System.out.println("Part 2: " + lowestSteps);
	}

	static HashMap<ArrayList<Integer>, Integer> pathThrough(String[] path) {
		HashMap<ArrayList<Integer>, Integer> result = new HashMap<>();
		int x = 0;
		int y = 0;
		int steps = 0;

		for (String instruction : path) {
			char dir = instruction.charAt(0);
			int dist = Integer.parseInt(instruction.substring(1));
			for (int i = 0; i < dist; i++) {
				switch (dir) {
					case 'R':
						x++;
						break;
					case 'U':
						y++;
						break;
					case 'L':
						x--;
						break;
					case 'D':
						y--;
						break;
				}
				steps++;
				result.put(new ArrayList<>(Arrays.asList(x, y)), steps);
			}
		}

		return result;
	}
}
