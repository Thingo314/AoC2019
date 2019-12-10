import java.util.*;

public class Day_10 {
	static ArrayList<Integer> station = null;

	static Comparator<ArrayList<Integer>> angleSort = new Comparator<ArrayList<Integer>>() {
		double halfpi = Math.PI / 2;
		public int compare(ArrayList<Integer> l1, ArrayList<Integer> l2) {
			double l1ATan = Math.atan2(station.get(1) - l1.get(1), l1.get(0) - station.get(0));
			if (l1ATan > halfpi) {
				l1ATan -= Math.PI * 2;
			}
			l1ATan -= halfpi;
			double l2ATan = Math.atan2(station.get(1) - l2.get(1), l2.get(0) - station.get(0));
			if (l2ATan > halfpi) {
				l2ATan -= Math.PI * 2;
			}
			l2ATan -= halfpi;
			if (l1ATan > l2ATan) {
				return -1;
			} else if (l1ATan < l2ATan) {
				return 1;
			}
			return 0;
		}
	};

	public static void main(String[] args) {
		int lineNum = 0;
		ArrayList<ArrayList<Integer>> asteroids = new ArrayList<>();

		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) == '#')
					asteroids.add(new ArrayList<Integer>(Arrays.asList(i, lineNum)));
			}
			lineNum++;
		}
		sc.close();

		HashMap<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> inSight = new HashMap<>();
		for (int i = 0; i < asteroids.size(); i++) {
			ArrayList<Integer> asteroid = asteroids.get(i);
			for (int j = 0; j < asteroids.size(); j++) {
				if (i == j)
					continue;
				ArrayList<Integer> other = asteroids.get(j);
				int dX = other.get(0) - asteroid.get(0);
				int dY = other.get(1) - asteroid.get(1);
				int gcd = gcd(dX, dY);
				dX /= gcd;
				dY /= gcd;
				boolean blocked = false;
				ArrayList<Integer> candidate = new ArrayList<>(Arrays.asList(other.get(0) - dX, other.get(1) - dY));
				while (!asteroid.equals(candidate)) {
					if (asteroids.contains(candidate)) {
						blocked = true;
						break;
					}
					candidate.set(0, candidate.get(0) - dX);
					candidate.set(1, candidate.get(1) - dY);
				}
				if (!blocked) {
					if (!inSight.containsKey(asteroid))
						inSight.put(asteroid, new ArrayList<>());
					inSight.get(asteroid).add(other);
				}
			}
		}

		ArrayList<ArrayList<Integer>> otherAsteroids = new ArrayList<>();
		for (Map.Entry<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> entry : inSight.entrySet()) {
			ArrayList<Integer> asteroid = entry.getKey();
			ArrayList<ArrayList<Integer>> others = entry.getValue();
			if (otherAsteroids.size() < others.size()) {
				otherAsteroids = others;
				station = asteroid;
			}
		}

		otherAsteroids.sort(angleSort);
		ArrayList<Integer> predicted = otherAsteroids.get(199);
		int part2 = 100 * predicted.get(0) + predicted.get(1);

		System.out.println("Part 1: " + otherAsteroids.size());
		System.out.println("Part 2: " + part2);
	}

	static int gcd(int a, int b) {
		a = Math.abs(a);
		b = Math.abs(b);
		if (b == 0)
			return a;
		return gcd(b, a % b);
	}
}
