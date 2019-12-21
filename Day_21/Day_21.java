import java.util.*;

public class Day_21 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String[] memory = sc.nextLine().split(",");
		sc.close();

		long[] values = new long[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Long.parseLong(memory[i]);
		IntCode comp = new IntCode(values);

		// part 1 - if there is a space between me and the end of my jump,
		//			and i can safely land, jump
		// i.e. J = (/A + /B + /C) . D
		String instructions = "NOT A J\n" +
							  "NOT B T\n" +
							  "OR T J\n" +
							  "NOT C T\n" +
							  "OR T J\n" +
							  "AND D J\n" +
							  "WALK\n";
		long[] input = new long[instructions.length()];
		for (int i = 0; i < instructions.length(); i++)
			input[i] = instructions.charAt(i);

		long output = 0;
		long answer = 0;
		while (output != -1) {
			answer = output;
			output = comp.runProgram(input);
		}
		System.out.println("Part 1: " + answer);
		comp.reset();

		// part 2 - if i jump, i must be able to walk or jump
		// i.e. J = (part 1) . (E + H)
		instructions = "NOT A J\n" +
					   "NOT B T\n" +
					   "OR T J\n" +
					   "NOT C T\n" +
					   "OR T J\n" +
					   "AND D J\n" +
					   // to set an editable register to the value of a read only,
					   // get the not of the read only and not that again.
					   "NOT E T\n" +
					   "NOT T T\n" +
					   "OR H T\n" +
					   "AND T J\n" +
					   "RUN\n";
		input = new long[instructions.length()];
		for (int i = 0; i < instructions.length(); i++)
			input[i] = instructions.charAt(i);

		output = 0;
		while (output != -1) {
			answer = output;
			output = comp.runProgram(input);
		}
		System.out.println("Part 2: " + answer);
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
