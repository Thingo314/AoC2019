import java.util.*;

public class Day_01 {
	public static void main(String[] args) {
		int baseSum = 0;
		int totalSum = 0;
		
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextInt()) {
			int mass = sc.nextInt();

			int fuel = mass / 3 - 2;
			baseSum += fuel;
			totalSum += fuel;

			int fuelForfuel = fuel / 3 - 2;
			while (fuelForfuel > 0) {
				totalSum += fuelForfuel;
				fuelForfuel = fuelForfuel / 3 - 2;
			}
		}
		sc.close();

		System.out.println("Part 1: " + baseSum);
		System.out.println("Part 2: " + totalSum);
	}
}
