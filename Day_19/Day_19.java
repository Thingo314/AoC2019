import java.util.*;

public class Day_19 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String[] memory = sc.nextLine().split(",");
		sc.close();

		long[] values = new long[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Long.parseLong(memory[i]);
		IntCode comp = new IntCode(values);

		int count = 0;
		int dim = 50;
		ArrayList<ArrayList<Boolean>> inBeam = new ArrayList<>();
		for (int y = 0; y < dim; y++) {
			inBeam.add(new ArrayList<>());
			for (int x = 0; x < dim; x++) {
				long[] input = new long[]{x, y};
				long output = comp.runProgram(input);
				comp.reset();
				if (output == 1)
					count++;
				inBeam.get(y).add(output == 1);
			}
		}

		System.out.println("Part 1: " + count);

		ArrayList<Integer> pos = findSquare(inBeam, 100);
		while (pos.size() != 2) {
			expand(inBeam, comp);
			pos = findSquare(inBeam, 100);
		}

		int answer = pos.get(0) * 10000 + pos.get(1);
		System.out.println("Part 2: " + answer);
	}

	static ArrayList<Integer> findSquare(ArrayList<ArrayList<Boolean>> inBeam, int size) {
		ArrayList<Integer> pos = new ArrayList<>();
		int closestX = Integer.MAX_VALUE / 2;
		int closestY = Integer.MAX_VALUE / 2;
		boolean foundSquare = false;

		for (int i = 0; i < inBeam.size() - size; i++) {
			for (int j = 0; j < inBeam.get(0).size() - size; j++) {
				if (!inBeam.get(i).get(j))
					continue;
				if (!inBeam.get(i + size - 1).get(j))
					continue;
				if (!inBeam.get(i).get(j + size - 1))
					continue;
				
				if (closestX + closestY > i + j) {
					closestX = j;
					closestY = i;
					foundSquare = true;
				}
			}
		}
		if (!foundSquare)
			return pos;

		pos.add(closestX);
		pos.add(closestY);
		return pos;
	}

	static int expand(ArrayList<ArrayList<Boolean>> inBeam, IntCode comp) {
		int newInBeam = 0;
		for (int y = 0; y < inBeam.size(); y++) {
			int x = inBeam.get(y).size();
			long output = comp.runProgram(new long[]{x, y});
			comp.reset();
			if (output == 1)
				newInBeam++;
			inBeam.get(y).add(output == 1);
		}

		inBeam.add(new ArrayList<>());
		int y = inBeam.size() - 1;
		for (int x = 0; x <= y; x++) {
			long output = comp.runProgram(new long[]{x, y});
			comp.reset();
			if (output == 1)
				newInBeam++;
			inBeam.get(y).add(output == 1);
		}
		
		return newInBeam;
	}
}

class IntCode {
	long[] values = new long[1];
	long[] copy = new long[1];
	int pointer = 0;
	int relativeBase = 0;
	int inIndex = 0;

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

	public long runProgram(long[] input) {
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
					inIndex++;
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
		inIndex = 0;
	}

	public void setValue(int i, long n) {
		values[i] = n;
	}
}
