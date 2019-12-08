import java.util.*;

public class Day_08 {
	public static void main(String[] args) {
		int width = 25;
		int height = 6;
		int layerSize = width * height;

		Scanner sc = new Scanner(System.in);
		String line = sc.nextLine();
		sc.close();

		int layers = line.length() / layerSize;
		char[][][] imageLayers = new char[layers][height][width];

		int min = Integer.MAX_VALUE;
		int layerNum = 1;
		for (int i = 0; i < layers; i++) {
			int count = 0;
			for (int j = 0; j < height; j++) {
				for (int k = 0; k < width; k++) {
					imageLayers[i][j][k] = line.charAt(i * layerSize + j * width + k);
					if (imageLayers[i][j][k] == '0')
						count++;
				}
			}
			if (min > count){
				min = count;
				layerNum = i;
			}
		}

		int ones = 0;
		int twos = 0;
		for (int j = 0; j < height; j++) {
			for (int k = 0; k < width; k++) {
				if (imageLayers[layerNum][j][k] == '1')
					ones++;
				if (imageLayers[layerNum][j][k] == '2')
					twos++;
			}
		}

		char[][] image = new char[height][width];
		for (int i = 0; i < layers; i++) {
			for (int j = 0; j < height; j++) {
				for (int k = 0; k < width; k++) {
					if (image[j][k] == 0) {
						if (imageLayers[i][j][k] == '0') {
							image[j][k] = ' ';
						} else if (imageLayers[i][j][k] == '1') {
							image[j][k] = '*';
						}
					}
				}
			}
		}

		System.out.println("Part 1: " + ones * twos);
		System.out.println("Part 2: ...");
		for (int j = 0; j < height; j++) {
			for (int k = 0; k < width; k++) {
				System.out.print(image[j][k]);
			}
			System.out.println();
		}
	}
}
