import java.util.*;

public class Day_16 {
	public static void main(String[] args) {
		int[] basePattern = new int[] {0, 1, 0, -1};
		Scanner sc = new Scanner(System.in);
		String line = sc.nextLine();
		sc.close();

		int[] shortSignal = new int[line.length()];
		for (int i = 0; i < line.length(); i++)
			shortSignal[i] = Integer.parseInt(line.substring(i, i + 1));

		int[] nums = new int[shortSignal.length * 10000];
		for (int i = 0; i < nums.length; i++)
			nums[i] = shortSignal[i % shortSignal.length];

		int offset = 0;
		for (int i = 0; i < 7; i++) {
			offset *= 10;
			offset += nums[i];
		}

		int[] longSignal = new int[nums.length - offset];
		System.arraycopy(nums, offset, longSignal, 0, longSignal.length);

		for (int i = 0; i < 100; i++) {
			shortSignal = fft(shortSignal, basePattern);
			longSignal = fastfft(longSignal);
		}

		String result1 = "";
		String result2 = "";
		for (int i = 0; i < 8; i++) {
			result1 += shortSignal[i];
			result2 += longSignal[i];
		}

		System.out.println("Part 1: " + result1);
		System.out.println("Part 2: " + result2);
	}

	static int[] fastfft(int[] signal) {
		int[] newSignal = new int[signal.length];
		int sum = 0;
		for (int i = newSignal.length - 1; i >= 0; i--) {
			sum += signal[i];
			sum %= 10;
			newSignal[i] = sum;
		}
		return newSignal;
	}

	static int[] fft(int[] signal, int[] pattern) {
		int[] newSignal = new int[signal.length];
		for (int i = 0; i < newSignal.length; i++) {
			int[] patternToUse = new int[signal.length];
			int repeatTimes = i + 1;
			int index = 0;
			int timesUsed = 1;
			for (int j = 0; j < patternToUse.length; j++) {
				if (timesUsed >= repeatTimes) {
					index = (index + 1) % pattern.length;
					timesUsed = 0;
				}
				timesUsed++;
				patternToUse[j] = pattern[index];
			}
			newSignal[i] = elementMultiply(signal, patternToUse);
		}
		return newSignal;
	}

	static int elementMultiply(int[] a, int[] b) {
		int result = 0;
		for (int i = 0; i < a.length; i++) {
			result += a[i] * b[i];
		}
		result = Math.abs(result);
		result %= 10;
		return result;
	}
}
