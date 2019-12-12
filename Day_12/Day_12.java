import java.util.*;

public class Day_12 {
	public static void main(String[] args) {
		HashMap<Integer, ArrayList<Integer>> vels = new HashMap<>();
		ArrayList<ArrayList<Integer>> positions = new ArrayList<>();
		ArrayList<ArrayList<Integer>> origpositions = new ArrayList<>();
		Scanner sc = new Scanner(System.in);
		int moonNum = 0;
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			line = line.replace(",", "");
			line = line.replace("=", " ");
			line = line.replace("<", "");
			line = line.replace(">", "");
			String[] params = line.split(" ");
			ArrayList<Integer> moon = new ArrayList<>();
			ArrayList<Integer> moonCopy = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				moon.add(Integer.parseInt(params[2*i+1]));
				moonCopy.add(Integer.parseInt(params[2*i+1]));
			}
			positions.add(moon);
			origpositions.add(moonCopy);
			vels.put(moonNum, new ArrayList<Integer>(Arrays.asList(0, 0, 0)));
			moonNum++;
		}
		sc.close();
		long[] periods = new long[3];
		boolean[] foundPeriod = new boolean[3];
		long steps = 0;
		boolean foundPart1 = false;
		do {
			steps++;
			for (int i = 0; i < 3; i++) {
				if (!foundPeriod[i])
					periods[i]++;
			}
			for (Integer index : vels.keySet()) {
				for (Integer other : vels.keySet()) {
					if (index == other)
						continue;

					ArrayList<Integer> moon = positions.get(index);
					ArrayList<Integer> otherMoon = positions.get(other);

					ArrayList<Integer> vel = vels.get(index);
					for (int i = 0; i < 3; i++) {
						int mypos = moon.get(i);
						int otherpos = otherMoon.get(i);

						if (mypos == otherpos)
							continue;

						if (mypos > otherpos) {
							vel.set(i, vel.get(i) - 1);
						} else {
							vel.set(i, vel.get(i) + 1);
						}
					}
				}
			}

			for (Map.Entry<Integer, ArrayList<Integer>> entry : vels.entrySet()) {
				ArrayList<Integer> pos = positions.get(entry.getKey());
				ArrayList<Integer> vel = entry.getValue();
				for (int i = 0; i < 3; i++) {
					pos.set(i, pos.get(i) + vel.get(i));
				}
			}

			if (steps == 1000) {
				int energy = 0;
				for (Integer index : vels.keySet()) {
					int moonPot = 0;
					int moonKin = 0;
					for (Integer n : positions.get(index))
						moonPot += Math.abs(n);
					for (Integer n : vels.get(index))
						moonKin += Math.abs(n);
					energy += moonPot * moonKin;
				}
				System.out.println("Part 1: " + energy);
				foundPart1 = true;
			}

			for (int i = 0; i < 3; i++) {
				boolean found = true;
				for (int index : vels.keySet()) {
					if (positions.get(index).get(i) != origpositions.get(index).get(i)) {
						found = false;
						break;
					}
					if (vels.get(index).get(i) != 0) {
						found = false;
						break;
					}
				}
				if (found)
					foundPeriod[i] = true;
			}
		} while (!allTrue(foundPeriod) || !foundPart1);

		System.out.println("Part 2: " + lcm(lcm(periods[0], periods[1]), periods[2]));
	}

	static long lcm(long a, long b) {
		return a * b / gcd(a, b);
	}

	static long gcd(long a, long b) {
		if (b == 0)
			return a;
		return gcd(b, a % b);
	}

	static boolean allTrue(boolean[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (!arr[i])
				return false;
		}
		return true;
	}
}
