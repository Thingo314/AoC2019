import java.util.*;
import java.math.BigInteger;

public class Day_22 {
	static long cardNum = 10007;
	static BigInteger bigCardNum = BigInteger.valueOf(119315717514047l);

	public static void main(String[] args) {
		ArrayList<ArrayList<String>> instructions = new ArrayList<>();
		long position = 2019;

		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split(" ");
			ArrayList<String> instruction = new ArrayList<>();
			for (String str : line)
				instruction.add(str);
			instructions.add(instruction);
			String lastString = instruction.get(instruction.size() - 1);
			if (instruction.get(0).equals("deal")) {
				if (lastString.equals("stack")) {
					position = reverseStack(position);
				} else {
					position = dealIncrement(position, Integer.parseInt(lastString));
				}
			} else {
				position = cut(position, Integer.parseInt(lastString));
			}
		}
		sc.close();
		
		System.out.println("Part 1: " + position);

		cardNum = bigCardNum.longValue();
		long[] pos = new long[3];
		pos[0] = 2020;
		for (int iterate = 0; iterate < 2; iterate++) {
			long next = pos[iterate];
			for (int i = instructions.size() - 1; i >= 0; i--) {
				ArrayList<String> instruction = instructions.get(i);
				String lastString = instruction.get(instruction.size() - 1);
				if (instruction.get(0).equals("deal")) {
					if (lastString.equals("stack")) {
						next = reverseStack(next);
					} else {
						next = reverseDealIncrement(next, Integer.parseInt(lastString));
					}
				} else {
					next = reverseCut(next, Integer.parseInt(lastString));
				}
			}
			pos[iterate + 1] = next;
		}

		BigInteger pos0 = BigInteger.valueOf(pos[0]);
		BigInteger pos1 = BigInteger.valueOf(pos[1]);
		BigInteger pos2 = BigInteger.valueOf(pos[2]);
		BigInteger times = BigInteger.valueOf(101741582076661l);

		BigInteger a = pos1.subtract(pos2).multiply(pos0.subtract(pos1).modInverse(bigCardNum)).mod(bigCardNum);
		BigInteger b = pos1.subtract(a.multiply(pos0)).mod(bigCardNum);

		BigInteger result = a.modPow(times, bigCardNum).multiply(pos0);
		BigInteger geomSum = b.multiply(a.modPow(times, bigCardNum).subtract(BigInteger.ONE))
								.multiply(a.subtract(BigInteger.ONE).modInverse(bigCardNum));
		result = result.add(geomSum).mod(bigCardNum);
		System.out.println("Part 2: " + result);
	}

	static long reverseStack(long pos) {
		return cardNum - pos - 1;
	}

	static long dealIncrement(long pos, long n) {
		return (pos * n) % cardNum;
	}

	static long reverseDealIncrement(long pos, long n) {
		BigInteger result = BigInteger.valueOf(n).modInverse(bigCardNum)
							.multiply(BigInteger.valueOf(pos))
							.mod(bigCardNum);
		return result.longValue();
	}

	static long cut(long pos, long n) {
		return (pos + cardNum - n) % cardNum;
	}

	static long reverseCut(long pos, long n) {
		return cut(pos, -n);
	}
}
