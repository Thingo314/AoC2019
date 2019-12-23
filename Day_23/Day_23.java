import java.util.*;

public class Day_23 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String[] memory = sc.nextLine().split(",");
		sc.close();

		long[] values = new long[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Long.parseLong(memory[i]);
		IntCode[] network = new IntCode[50];
		for (int i = 0; i < network.length; i++){
			network[i] = new IntCode(values);
			network[i].addInput(new long[]{i});
			network[i].runProgram();
		}

		HashMap<Long, ArrayList<Long>> inputs = new HashMap<>();
		ArrayList<Long> prevNatOut = new ArrayList<>();
		ArrayList<Long> natOut = new ArrayList<>();

		boolean[] found = new boolean[2];
		long[] answers = new long[2];

		while (!found[0] || !found[1]) {
			for (int address = 0; address < network.length; address++) {
				ArrayList<Long> inputList = new ArrayList<>();
				if (inputs.containsKey((long) address)) {
					ArrayList<Long> queuedPackets = inputs.get((long) address);
					inputList.add(queuedPackets.remove(0));
					inputList.add(queuedPackets.remove(0));
					if (queuedPackets.size() == 0)
						inputs.remove((long) address);
				} else {
					inputList.add(-1l);
				}

				long[] input = new long[inputList.size()];
				for (int i = 0; i < input.length; i++)
					input[i] = inputList.get(i);
				network[address].addInput(input);

				ArrayList<Long> packet = new ArrayList<>();
				long output = 0;
				for (int i = 0; i < 3; i++) {
					output = network[address].runProgram();
					if (output < 0 && i == 0)
						break;
					packet.add(output);
				}

				if (packet.size() != 0) {
					long destination = packet.remove(0);
					if (destination == 255) {
						if (!found[0]) {
							answers[0] = packet.get(1);
							found[0] = true;
						}
						natOut = packet;
					} else {
						if (!inputs.containsKey(destination))
							inputs.put(destination, new ArrayList<>());
						inputs.get(destination).addAll(packet);

					}
				}
			}

			if (inputs.size() == 0) {
				if (prevNatOut.size() != 0 && prevNatOut.get(1).equals(natOut.get(1))) {
					found[1] = true;
					answers[1] = natOut.get(1);
				}
				prevNatOut = new ArrayList<>(natOut);
				inputs.put(0l, natOut);
			}
		}

		System.out.println("Part 1: " + answers[0]);
		System.out.println("Part 2: " + answers[1]);
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
	}

	public void setValue(int i, long n) {
		values[i] = n;
	}
}
