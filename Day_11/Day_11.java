import java.util.*;

public class Day_11 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String[] memory = sc.nextLine().split(",");
		sc.close();

		long[] values = new long[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Long.parseLong(memory[i]);
		IntCode comp = new IntCode(values);

		HashMap<ArrayList<Integer>, Integer> part1 = paint(comp, 0);
		comp.reset();
		HashMap<ArrayList<Integer>, Integer> part2 = paint(comp, 1);

		int[] dimx = {Integer.MAX_VALUE, Integer.MIN_VALUE};
		int[] dimy = {Integer.MAX_VALUE, Integer.MIN_VALUE};
		for (Map.Entry<ArrayList<Integer>, Integer> entry : part2.entrySet()) {
			ArrayList<Integer> coord = entry.getKey();
			int value = entry.getValue();
			if (value == 1) {
				dimx[0] = Math.min(coord.get(0), dimx[0]);
				dimx[1] = Math.max(coord.get(0), dimx[1]);
				dimy[0] = Math.min(coord.get(1), dimy[0]);
				dimy[1] = Math.max(coord.get(1), dimy[1]);
			}
		}

		char[][] reg = new char[dimy[1] - dimy[0] + 1][dimx[1] - dimx[0] + 1];
		for (char[] row : reg)
			Arrays.fill(row, ' ');

		for (Map.Entry<ArrayList<Integer>, Integer> entry : part2.entrySet()) {
			ArrayList<Integer> coord = entry.getKey();
			int value = entry.getValue();
			if (value == 1)
				reg[dimy[1] - coord.get(1)][coord.get(0) - dimx[0]] = '#';
		}

		System.out.println("Part 1: " + part1.size());
		System.out.println("Part 2: ...");
		for (char[] row : reg) {
			for (char c : row)
				System.out.print(c);
			System.out.println();
		}
	}

	static HashMap<ArrayList<Integer>, Integer> paint(IntCode comp, int start) {
		HashMap<ArrayList<Integer>, Integer> coords = new HashMap<>();
		int facing = 0;
		int x = 0;
		int y = 0;
		int input = 0;
		boolean firstStep = true;

		ArrayList<Integer> results = new ArrayList<>();
		ArrayList<Integer> pos = new ArrayList<>(Arrays.asList(x, y));
		do {
			if (firstStep) {
				input = start;
				firstStep = false;
			} else {
				input = coords.getOrDefault(pos, 0);
			}
			results = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				long result = comp.runProgram(new long[]{input});
				results.add((int) result);
			}
			coords.put(pos, results.get(0));

			if (results.get(1) == 0) {
				facing -= 1;
				facing = (facing + 4) % 4;
			} else {
				facing += 1;
				facing = (facing) % 4;
			}
			switch (facing) {
				case 0:
					y++;
					break;
				case 1:
					x++;
					break;
				case 2:
					y--;
					break;
				case 3:
					x--;
					break;
			}
			pos = new ArrayList<>(Arrays.asList(x, y));
		} while (!results.contains(-1));
		return coords;
	}
}

class IntCode {
	long[] values = new long[1];
	long[] copy = new long[1];
	int pointer = 0;
	int relativeBase = 0;

	public IntCode(long[] mem) {
		values = Arrays.copyOf(mem, mem.length * 3);
		copy = values.clone();
	}

	public long runProgram(long[] input) {
		int inIndex = 0;

		while (pointer < values.length) {
			int instruction = (int) values[pointer];
			int opcode = instruction % 100;
			instruction /= 100;

			int[] modes = new int[3];
			for (int i = 0; i < 3; i++) {
				modes[i] = instruction % 10;
				instruction /= 10;
			}

			long[] parameters = new long[3];
			if (opcode == 3 || opcode == 4 || opcode == 9) {
				if (modes[0] == 0) {
					parameters[0] = values[pointer + 1];
				} else if (modes[0] == 1) {
					parameters[0] = pointer + 1;
				} else if (modes[0] == 2) {
					parameters[0] = values[pointer + 1] + relativeBase;
				}
			} else if (opcode != 99) {
				for (int i = 0; i < 2; i++) {
					if (modes[i] == 0) {
						parameters[i] = values[(int)values[pointer + i + 1]];
					} else if (modes[i] == 1) {
						parameters[i] = values[pointer + i + 1];
					} else if (modes[i] == 2) {
						parameters[i] = values[(int)values[pointer + i + 1] + relativeBase];
					}
				}
				if (modes[2] == 0) {
					parameters[2] = (int)values[pointer + 3];
				} else if (modes[2] == 2) {
					parameters[2] = (int)values[pointer + 3] + relativeBase;
				}
			} else {
				return -1;
			}

			switch (opcode) {
				case 1:
					values[(int)parameters[2]] = parameters[0] + parameters[1];
					pointer += 4;
					break;
				case 2:
					values[(int)parameters[2]] = parameters[0] * parameters[1];
					pointer += 4;
					break;
				case 3:
					values[(int)parameters[0]] = input[inIndex];
					pointer += 2;
					inIndex++;
					break;
				case 4:
					pointer += 2;
					return values[(int)parameters[0]];
				case 5:
					if (parameters[0] != 0) {
						pointer = (int)parameters[1];
					} else {
						pointer += 3;
					}
					break;
				case 6:
					if (parameters[0] == 0) {
						pointer = (int)parameters[1];
					} else {
						pointer += 3;
					}
					break;
				case 7:
					if (parameters[0] < parameters[1]) {
						values[(int)parameters[2]] = 1;
					} else {
						values[(int)parameters[2]] = 0;
					}
					pointer += 4;
					break;
				case 8:
					if (parameters[0] == parameters[1]) {
						values[(int)parameters[2]] = 1;
					} else {
						values[(int)parameters[2]] = 0;
					}
					pointer += 4;
					break;
				case 9:
					relativeBase += values[(int)parameters[0]];
					pointer += 2;
					break;
			}
		}
		return -1;
	}

	public void reset() {
		values = copy.clone();
		pointer = 0;
		relativeBase = 0;
	}
}
