import java.util.*;

public class Day_17 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String[] memory = sc.nextLine().split(",");
		sc.close();

		long[] values = new long[memory.length];
		for (int i = 0; i < memory.length; i++)
			values[i] = Long.parseLong(memory[i]);
		IntCode comp = new IntCode(values);

		ArrayList<ArrayList<Character>> cameraView = new ArrayList<>();
		cameraView.add(new ArrayList<>());
		int row = 0;
		long output = 0;

		while (output != -1) {
			output = comp.runProgram(new long[]{0});
			if (output == 10) {
				row++;
				cameraView.add(new ArrayList<>());
			} else {
				cameraView.get(row).add((char)output);
			}
		}

		int scanlineLength = cameraView.get(0).size();
		for (int i = 0; i < cameraView.size(); i++) {
			if (cameraView.get(i).size() != scanlineLength) {
				cameraView.remove(i);
				i--;
			}
		}

		int sum = 0;
		int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
		for (int i = 1; i < cameraView.size() - 1; i++) {
			for (int j = 1; j < cameraView.get(0).size() - 1; j++) {
				boolean isIntersection = true;
				for (int[] dir : directions) {
					int neighbourI = i + dir[0];
					int neighbourJ = j + dir[1];
					if (cameraView.get(neighbourI).get(neighbourJ) != '#') {
						isIntersection = false;
						break;
					}
				}
				if (isIntersection)
					sum += i * j;
			}
		}
		System.out.println("Part 1: " + sum);

		comp.reset();
		comp.setValue(0, 2);

		// NOTE: This is the solution for my input, to solve
		// use some paper to figure out the movements;) 
		// {A,B,A,C,A,B,C,B,C,A}
		long[] mainRoutine = new long[]
			{65, 44, 66, 44, 65, 44,
			 67, 44, 65, 44, 66, 44,
			 67, 44, 66, 44, 67, 44, 65, 10};
		// {L,6,6,R,4,R,4,L,6}
		long[] a = new long[]
			{76, 44, 54, 44, 54, 44,
			 82, 44, 52, 44, 82, 44,
			 52, 44, 76, 44, 54, 10};
		// {L,6,6,R,4,R,4,R,6,6}
		long[] b = new long[]
			{76, 44, 54, 44, 54, 44,
			 82, 44, 52, 44, 82, 44,
			 52, 44, 82, 44, 54, 44, 54, 10};
		long[] c = new long[]
			{76, 44, 53, 44, 53, 44,
			 76, 44, 54, 44, 82, 44, 52, 10};
		long[] video = new long[]
			// dont visualise
			{110, 10};
			// visualise
			// {121, 10};

		int length1 = mainRoutine.length;
		int length2 = length1 + a.length;
		int length3 = length2 + b.length;
		int length4 = length3 + c.length;
		int inputLength = length4 + video.length;
		long[] input = new long[inputLength];

		System.arraycopy(mainRoutine, 0, input, 0, length1);
		System.arraycopy(a, 0, input, length1, a.length);
		System.arraycopy(b, 0, input, length2, b.length);
		System.arraycopy(c, 0, input, length3, c.length);
		System.arraycopy(video, 0, input, length4, video.length);

		output = 0;
		long dustCollected = 0;
		while (output != -1) {
			dustCollected = output;
			output = comp.runProgram(input);
		}
		System.out.println("Part 2: " + dustCollected);
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
