import java.util.*;

public class Day_15 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String[] memory = sc.nextLine().split(",");
		sc.close();

		long[] values = new long[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Long.parseLong(memory[i]);
		IntCode comp = new IntCode(values);

		HashMap<ArrayList<Integer>, ArrayList<Integer>> visited = new HashMap<>();
		ArrayDeque<IntCode> comps = new ArrayDeque<>();
		ArrayDeque<ArrayList<Integer>> positions = new ArrayDeque<>();

		ArrayList<Integer> start = new ArrayList<>(Arrays.asList(0, 0));
		visited.put(start, new ArrayList<>(Arrays.asList(0, 1)));
		comps.add(comp);
		positions.add(start);

		while (comps.size() > 0) {
			IntCode copy = comps.pop();
			ArrayList<Integer> pos = positions.pop();
			int length = visited.get(pos).get(0);
			for (int i = 1; i <= 4; i++) {
				IntCode prog = new IntCode(copy);
				int x = pos.get(0);
				int y = pos.get(1);
				long output = prog.runProgram(i);
				if (output != 0) {
					switch (i) {
						case 1:
							y++;
							break;
						case 2:
							y--;
							break;
						case 3:
							x--;
							break;
						case 4:
							x++;
							break;
					}
					ArrayList<Integer> newPos = new ArrayList<>(Arrays.asList(x, y));
					if (!visited.containsKey(newPos)) {
						visited.put(newPos, new ArrayList<>(Arrays.asList(length + 1, (int) output)));
						comps.add(prog);
						positions.add(newPos);
					}
				}
			}
		}
		ArrayList<Integer> stationPos = null;
		for (Map.Entry<ArrayList<Integer>, ArrayList<Integer>> entry : visited.entrySet()) {
			if (entry.getValue().get(1) == 2) {
				System.out.println("Part 1: " + entry.getValue().get(0));
				stationPos = entry.getKey();
				break;
			}
		}

		HashMap<ArrayList<Integer>, Integer> filled = new HashMap<>();
		filled.put(stationPos, 0);
		positions.add(stationPos);

		int[][] directions = new int[][] {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
		while (positions.size() > 0) {
			ArrayList<Integer> position = positions.pop();
			int time = filled.get(position);
			for (int[] dir : directions) {
				int x = position.get(0) + dir[0];
				int y = position.get(1) + dir[1];
				ArrayList<Integer> newPos = new ArrayList<>(Arrays.asList(x, y));
				if (visited.containsKey(newPos) && !filled.containsKey(newPos)) {
					filled.put(newPos, time + 1);
					positions.add(newPos);
				}
			}
		}
		int maxTime = 0;
		for (int value : filled.values())
			maxTime = Math.max(maxTime, value);
		System.out.println("Part 2: " + maxTime);
	}
}

class IntCode {
	long[] values = new long[1];
	long[] copy = new long[1];
	int pointer = 0;
	int relativeBase = 0;
	// int inIndex = 0;

	public IntCode(long[] mem) {
		values = Arrays.copyOf(mem, mem.length * 2);
		copy = values.clone();
	}

	public IntCode(IntCode ic) {
		values = ic.values.clone();
		copy = ic.copy.clone();
		pointer = ic.pointer;
		relativeBase = ic.relativeBase;
	}

	public int hashCode() {
		return Objects.hash(values, copy, pointer, relativeBase);
	}

	public long runProgram(long input) {
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
					values[(int)parameters[0]] = input;
					pointer += 2;
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
