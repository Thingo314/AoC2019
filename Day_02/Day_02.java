import java.util.*;

public class Day_02 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String[] memory = sc.nextLine().split(",");
		sc.close();

		int[] values = new int[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Integer.parseInt(memory[i]);

		int[] copy = values.clone();
		int part1 = 0;
		int part2 = 0;
		boolean foundPart1 = false;
		boolean foundPart2 = false;

		for (int i = 0; i <= 99; i++) {
			for (int j = 0; j <= 99; j++) {
				values[1] = i;
				values[2] = j;
				int pointer = 0;

				while (pointer < values.length) {
					int instruction = values[pointer];
					boolean halt = false;
					switch (instruction) {
						case 1:
							values[values[pointer + 3]] = values[values[pointer + 1]] + values[values[pointer + 2]];
							pointer += 4;
							break;
						case 2:
							values[values[pointer + 3]] = values[values[pointer + 1]] * values[values[pointer + 2]];
							pointer += 4;
							break;
						case 99:
							halt = true;
							break;
					}
					if (halt)
						break;
				}

				if (values[0] == 19690720) {
					part2 = 100 * i + j;
					foundPart2 = true;
				}

				if (i == 12 && j == 2) {
					part1 = values[0];
					foundPart1 = true;
				}

				values = copy.clone();
			}

			if (foundPart1 && foundPart2)
				break;
		}

		System.out.println("Part 1: " + part1);
		System.out.println("Part 2: " + part2);
	}
}
