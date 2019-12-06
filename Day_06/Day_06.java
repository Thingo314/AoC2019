import java.util.*;

public class Day_06 {
	public static void main(String[] args) {
		HashMap<String, ArrayList<String>> orbits = new HashMap<>();

		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split("\\)");
			if (!orbits.containsKey(line[0]))
				orbits.put(line[0], new ArrayList<String>());
			orbits.get(line[0]).add(line[1]);
		}
		sc.close();

		int part1 = 0;
		for (Map.Entry<String, ArrayList<String>> orbit : orbits.entrySet())
			part1 += getOrbits(orbits, orbit.getKey());

		System.out.println("Part 1: " + part1);
		System.out.println("Part 2: " + search(orbits));
	}

	static int getOrbits(HashMap<String, ArrayList<String>> orbits, String centre) {
		if (!orbits.containsKey(centre))
			return 0;

		ArrayList<String> orbit = orbits.get(centre);
		int result = orbit.size();
		for (String object : orbit)
			result += getOrbits(orbits, object);
		return result;
	}

	static int search(HashMap<String, ArrayList<String>> orbits) {
		// the value represents the number of steps to move into orbit of the key
		HashMap<String, Integer> visited = new HashMap<>();
		visited.put("YOU", 0);
		// find where i am orbiting
		String location = findOrbit(orbits, "YOU");
		// as i am already orbiting this location, the number of steps is 0
		visited.put(location, 0);

		ArrayDeque<String> queue = new ArrayDeque<>();
		queue.addLast(location);

		while (queue.size() >= 0) {
			// add nodes...
			location = queue.pollFirst();
			int steps = visited.get(location);

			// if the location i am orbiting has objects orbiting
			// search the orbiting objects
			if (orbits.containsKey(location)) {
				for (String object : orbits.get(location)) {
					if (object.equals("SAN")) {
						return steps;
					}

					if (!visited.containsKey(object)) {
						visited.put(object, steps + 1);
						queue.addLast(object);
					}

				}
			}

			// if the location i am orbiting is orbiting an object
			// search the orbited object
			String centre = findOrbit(orbits, location);
			if (!centre.equals("")) {
				if (!visited.containsKey(centre)) {
					visited.put(centre, steps + 1);
					queue.addLast(centre);
				}
			}
		}
		return -1;
	}

	static String findOrbit(HashMap<String, ArrayList<String>> orbits, String object) {
		for (Map.Entry<String, ArrayList<String>> orbit : orbits.entrySet())
			if (orbit.getValue().contains(object))
				return orbit.getKey();
		return "";
	}
}
