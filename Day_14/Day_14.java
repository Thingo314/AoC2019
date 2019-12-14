import java.util.*;

public class Day_14 {
	public static void main(String[] args) {
		HashMap<String, HashMap<String, Integer>> reactions = new HashMap<>();
		HashMap<String, HashSet<String>> dependencies = new HashMap<>();
		HashMap<String, Integer> numProduced = new HashMap<>();

		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {
			String line = sc.nextLine().replace(",", "");
			String[] components = line.split(" ");

			int num = (components.length - 3) / 2;
			HashMap<String, Integer> reactants = new HashMap<>();
			String product = components[components.length - 1];

			if (!dependencies.containsKey(product))
				dependencies.put(product, new HashSet<>());
			for (int i = 0; i < num; i++) {
				reactants.put(components[2 * i + 1], Integer.parseInt(components[2 * i]));
				if (!dependencies.containsKey(components[2 * i + 1]))
					dependencies.put(components[2 * i + 1], new HashSet<>());
				dependencies.get(components[2 * i + 1]).add(product);
			}

			reactions.put(product, reactants);
			numProduced.put(product, Integer.parseInt(components[components.length - 2]));
		}
		sc.close();

		ArrayList<String> sort = new ArrayList<>();
		while (dependencies.size() > 0) {
			ArrayList<String> free = new ArrayList<>();
			for (Map.Entry<String, HashSet<String>> entry : dependencies.entrySet())
				if (entry.getValue().size() == 0)
					free.add(entry.getKey());

			sort.addAll(free);
			dependencies.keySet().removeAll(free);

			for (Map.Entry<String, HashSet<String>> entry : dependencies.entrySet())
				entry.getValue().removeAll(free);
		}

		System.out.println("Part 1: " + getOre(reactions, numProduced, sort, 1));

		long low = 1;
		long high = 1;
		long threshold = (long) Math.pow(10, 12);

		while (getOre(reactions, numProduced, sort, high) <= threshold)
			high *= 2;

		while (high - low > 1) {
			long mid = (low + high) / 2;
			long result = getOre(reactions, numProduced, sort, mid);
			if (result < threshold) {
				low = mid;
			} else if (result > threshold) {
				high = mid;
			} else {
				low = mid;
				break;
			}
		}

		System.out.println("Part 2: " + low);
	}

	static long getOre(HashMap<String, HashMap<String, Integer>> r,
					   HashMap<String, Integer> p,
					   ArrayList<String> d,
					   long fuel) {
		HashMap<String, Long> inv = new HashMap<>();
		inv.put("FUEL", fuel);
		for (int i = 0; i < d.size() - 1; i++) {
			String product = d.get(i);
			HashMap<String, Integer> reactants = r.get(product);
			long requiredNum = inv.get(product);
			long producedNum = p.get(product);
			long reactionNum = (long) Math.ceil(requiredNum / (double) producedNum);

			inv.remove(product);
			for (Map.Entry<String, Integer> reactant : reactants.entrySet()) {
				String name = reactant.getKey();
				long quantity = reactant.getValue() * reactionNum;
				inv.put(name, inv.getOrDefault(name, 0l) + quantity);
			}
		}
		return inv.get("ORE");
	}
}
