import java.util.*;

public class Day_07 {
	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		String[] memory = sc.nextLine().split(",");

		int[] values = new int[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Integer.parseInt(memory[i]);

		int ampNumber = 5;

		int[] phaseSettings = new int[ampNumber];
		IntCode[] amps1 = new IntCode[ampNumber];
		IntCode[] amps2 = new IntCode[ampNumber];
		for (int i = 0; i < ampNumber; i++) {
			phaseSettings[i] = i;
			amps1[i] = new IntCode(values);
			amps2[i] = new IntCode(values);
		}

		int[] initialSettings = phaseSettings.clone();

		int part1 = 0;
		int part2 = 0;

		do {
			int output1 = 0;
			int output2 = 0;
			boolean halted = false;
			boolean initialRun = true;
			while (!halted) {
				for (int i = 0; i < 5; i++) {
					if (initialRun) {
						int[] input1 = {phaseSettings[i], output1};
						int[] amp1Out = amps1[i].runProgram(input1);
						output1 = amp1Out[0];
						if (i == 4) {
							part1 = Math.max(part1, amp1Out[0]);
							initialRun = false;
						}
					}
					int[] input2 = {phaseSettings[i] + ampNumber, output2};
					int[] amp2Out = amps2[i].runProgram(input2);
					output2 = amp2Out[0];
					if (amp2Out[1] == 1) {
						halted = true;
						output2 = input2[1];
						break;
					}
				}
			}

			part2 = Math.max(part2, output2);

			for (int i = 0; i < 5; i++) {
				amps1[i].reset();
				amps2[i].reset();
			}
			nextPermutation(phaseSettings);
		} while (!Arrays.equals(phaseSettings, initialSettings));

		System.out.println("Part 1: " + part1);
		System.out.println("Part 2: " + part2);
	}

	static void nextPermutation(int[] arr) {
		if (arr.length <= 1)
			return;

		int last = arr.length - 2;
		while (last >= 0) {
			if (arr[last] < arr[last + 1])
				break;
			last--;
		}

		if (last >= 0) {
			int larger = arr.length - 1;
			while (larger >= 0) {
				if (arr[larger] > arr[last])
					break;
				larger--;
			}
			swap(arr, last, larger);
		}

		reverse(arr, last + 1);
	}

	static void swap(int[] arr, int a, int b) {
		int temp = arr[a];
		arr[a] = arr[b];
		arr[b] = temp;
	}

	static void reverse(int[] arr, int a) {
		int b = arr.length - 1;
		while (a < b) {
			swap(arr, a, b);
			a++;
			b--;
		}
	}
}

class IntCode {
	int[] values = new int[1];
	int[] copy = new int[1];
	int pointer = 0;

	public IntCode(int[] mem) {
		values = mem.clone();
		copy = mem.clone();
	}

	public int[] runProgram(int[] input) {
		int inIndex = 0;
		if (pointer != 0)
			inIndex = 1;

		while (pointer < values.length) {
			int instruction = values[pointer];
			int opcode = instruction % 100;
			instruction /= 100;
			int[] modes = new int[3];
			for (int i = 0; i < 3; i++) {
				modes[i] = instruction % 10;
				instruction /= 10;
			}

			int[] parameters = new int[3];
			if (opcode == 3 || opcode == 4) {
				parameters[0] = (modes[0] == 1) ? pointer + 1 : values[pointer + 1];
			} else if (opcode != 99) {
				for (int i = 0; i < 2; i++) {
					parameters[i] = (modes[i] == 1) ? values[pointer + i + 1] : values[values[pointer + i + 1]];
				}
				parameters[2] = values[pointer + 3];
			} else {
				return new int[] {-1, 1};
			}

			switch (opcode) {
				case 1:
					values[parameters[2]] = parameters[0] + parameters[1];
					pointer += 4;
					break;
				case 2:
					values[parameters[2]] = parameters[0] * parameters[1];
					pointer += 4;
					break;
				case 3:
					values[parameters[0]] = input[inIndex];
					pointer += 2;
					inIndex++;
					break;
				case 4:
					pointer += 2;
					if (values[parameters[0]] != 0)
						return new int[] {values[parameters[0]], 0};
					break;
				case 5:
					if (parameters[0] != 0) {
						pointer = parameters[1];
					} else {
						pointer += 3;
					}
					break;
				case 6:
					if (parameters[0] == 0) {
						pointer = parameters[1];
					} else {
						pointer += 3;
					}
					break;
				case 7:
					if (parameters[0] < parameters[1]) {
						values[parameters[2]] = 1;
					} else {
						values[parameters[2]] = 0;
					}
					pointer += 4;
					break;
				case 8:
					if (parameters[0] == parameters[1]) {
						values[parameters[2]] = 1;
					} else {
						values[parameters[2]] = 0;
					}
					pointer += 4;
					break;
			}
		}
		return new int[] {-1, 1};
	}

	public void reset() {
		values = copy.clone();
		pointer = 0;
	}
}
