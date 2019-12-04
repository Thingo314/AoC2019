import java.util.*;

public class Day_04 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String[] data = sc.nextLine().split("-");
		sc.close();
		int min = Integer.parseInt(data[0]);
		int max = Integer.parseInt(data[1]);

		int count1 = 0;
		int count2 = 0;
		for (int pass = min; pass <= max; pass++) {
			if (digitsIncrease(pass) && containsDouble(pass)) {
				count1++;
				if (!containsLargerGroup(pass)) {
					count2++;
				}
			}
		}

		System.out.println("Part 1: " + count1);
		System.out.println("Part 2: " + count2);
	}

	static boolean digitsIncrease(int n) {
		int rightDigit = 9;
		while (n > 0) {
			if (n % 10 <= rightDigit) {
				rightDigit = n % 10;
				n /= 10;
			} else {
				return false;
			}
		}
		return true;
	}

	static boolean containsDouble(int n) {
		int rightDigit = n % 10;
		n /= 10;
		while (n > 0) {
			if (n % 10 == rightDigit) {
				return true;
			}
			rightDigit = n % 10;
			n /= 10;
		}
		return false;
	}

	static boolean containsLargerGroup(int n) {
		while (n > 0) {
			int rightDigit = n % 10;
			int count = 1;
			n /= 10;
			while (n % 10 == rightDigit) {
				count++;
				n /= 10;
			}
			if (count == 2)
				return false;
			if (count > 2) {
				if (n == 0)
					return true;
				if (!containsDouble(n))
					return true;
			}
		}
		return false;
	}
}
