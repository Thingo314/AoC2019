import java.util.*;

public class Day_13 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String[] memory = sc.nextLine().split(",");
		sc.close();

		long[] values = new long[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Long.parseLong(memory[i]);
		IntCode comp = new IntCode(values);
		comp.setValue(0, 2);
		boolean needsInputs = false;
		boolean initialising = true;
		int count = 0;

		ArrayList<Long> score = new ArrayList<>();
		ArrayList<Long> results = new ArrayList<>();
		ArrayList<Long> halt = new ArrayList<>(Arrays.asList(-1l, -1l, -1l));
		ArrayList<Long> ballpos = new ArrayList<>(Arrays.asList(0l, 0l, 0l));
		ArrayList<Long> paddlepos = new ArrayList<>(Arrays.asList(0l, 0l, 0l));
		ArrayList<ArrayList<Long>> grid = new ArrayList<>();

		while (true) {
			results = new ArrayList<>();
			long[] input = new long[]{0};
			if (needsInputs) {
				if (ballpos.get(0) > paddlepos.get(0)) {
					input[0] = 1;
				} else if (ballpos.get(0) < paddlepos.get(0)) {
					input[0] = -1;
				}
			}

			for (int i = 0; i < 3; i++)
				results.add(comp.runProgram(input));

			if (results.equals(halt))
				break;
			if (results.get(2) == 4) {
				ballpos = results;
			} else if (results.get(2) == 3) {
				paddlepos = results;
			}
			if (results.get(0) == -1 && results.get(1) == 0) {
				if (initialising) {
					for (ArrayList<Long> coord : grid)
						if (coord.get(2) == 2)
							count++;
					initialising = false;
				}
				score = results;
				needsInputs = true;
			}
			if (initialising)
				grid.add(results);
		}

		System.out.println("Part 1: " + count);
		System.out.println("Part 2: " + score.get(2));
	}
}

class IntCode {
	long[] values = new long[1];
	long[] copy = new long[1];
	int pointer = 0;
	int relativeBase = 0;

	public IntCode(long[] mem) {
		values = Arrays.copyOf(mem, mem.length * 2);
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

	public void setValue(int i, long n) {
		values[i] = n;
	}
}
