import java.util.*;
import java.io.File;

public class Day_25 {
	public static void main(String[] args) {
		// This program runs with the input in a file as a command line argument
		// So run 'java Day_25 in.txt' rather than ' java Day_25 < in.txt'

		File in = new File(args[0]);
		Scanner sc = null;
		try {
			sc = new Scanner(in);
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}

		String[] memory = sc.nextLine().split(",");
		sc.close();

		long[] values = new long[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Long.parseLong(memory[i]);
		
		IntCode comp = new IntCode(values);

		long output = 0;
		while (output >= 0) {
			output = comp.runProgram();
			if (output == -3) {
				Scanner command = new Scanner(System.in);
				String input = command.nextLine();
				sc.close();
				input += "\n";
				long[] inputList = new long[input.length()];
				for (int i = 0; i < input.length(); i++) {
					inputList[i] = input.charAt(i);
				}
				comp.addInput(inputList);
				output = 0;
			} else {
				System.out.print((char) output);
			}
		}
		// You might be wondering where part 1 is. That would be too easy...
		// Try running the program ;)
		System.out.println("Part 2: Have a happy new year!");
	}
}

class IntCode {
	long[] values = new long[1];
	long[] copy = new long[1];
	int pointer = 0;
	int relativeBase = 0;
	int inIndex = 0;
	ArrayList<Long> input = new ArrayList<>();

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

	public void addInput(long[] inputs) {
		for (long n : inputs)
			input.add(n);
	}

	public int hashCode() {
		return Objects.hash(values, copy, pointer, relativeBase);
	}

	public long runProgram() {
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
					if (inIndex >= input.size())
						return -3;
					values[(int)parameters[0]] = input.get(inIndex);
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
		return -2;
	}

	public void reset() {
		values = copy.clone();
		pointer = 0;
		relativeBase = 0;
		inIndex = 0;
		input = new ArrayList<>();
	}

	public void setValue(int i, long n) {
		values[i] = n;
	}
}
