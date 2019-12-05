import java.util.*;

public class Day_05 {
	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		String[] memory = sc.nextLine().split(",");

		int[] values = new int[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Integer.parseInt(memory[i]);

		int[] copy = values.clone();
		int part1 = runProgram(values, 1);
		values = copy.clone();
		int part2 = runProgram(values, 5);

		System.out.println("Part 1: " + part1);
		System.out.println("Part 2: " + part2);
	}

	static int runProgram(int[] values, int input) {
		int pointer = 0;
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
				/*
					The program halts if opcode 99 is reached.
					However, the value we care about is returned
					when opcode 4 is reached.
					If we haven't reached opcode 4 but see 99,
					then something has gone wrong.
				*/
				return -1;
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
					values[parameters[0]] = input;
					pointer += 2;
					break;
				case 4:
					if (values[parameters[0]] != 0)
						return values[parameters[0]];
					pointer += 2;
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
		return -1;
	}
}
