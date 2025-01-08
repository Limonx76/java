import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final int THRESHOLD = 20;  // поріг для поділу масиву
    private static int[] array;
    private static int[] leftPart;   // Ліва частина масиву
    private static int[] rightPart;  // Права частина масиву

    public static void main(String[] args) {
        // Створення та ініціалізація масиву розміром 1,000,000 елементів
        array = new int[1000000];
        Random rand = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(101);  // значення від 0 до 100
        }

        // Запис масиву у файл
        writeArrayToFile(array, "array_data.txt");

        // Поділяємо масив на дві частини
        int mid = array.length / 2;
        leftPart = new int[mid];
        rightPart = new int[array.length - mid];
        System.arraycopy(array, 0, leftPart, 0, mid);
        System.arraycopy(array, mid, rightPart, 0, array.length - mid);
        System.out.println("Массив поділено на дві частини");
        // Меню для вибору дії
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nМеню:");
            System.out.println("1. Ділити ліву частину масиву");
            System.out.println("2. Ділити праву частину масиву");
            System.out.println("3. Вихід");

            // Додано надпис "Виберіть"
            System.out.print("Виберіть опцію: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Ділимо ліву частину масиву
                    System.out.println("Ділимо ліву частину масиву...");
                    divideLeft(0, leftPart.length);
                    break;
                case 2:
                    // Ділимо праву частину масиву
                    System.out.println("Ділимо праву частину масиву...");
                    divideRight(0, rightPart.length);
                    break;
                case 3:
                    running = false;
                    System.out.println("Вихід з програми.");
                    break;
                default:
                    System.out.println("Невірний вибір! Спробуйте ще раз.");
            }
        }

        scanner.close();
    }

    // Ділимо ліву частину масиву
    public static void divideLeft(int start, int end) {
        if (end - start <= THRESHOLD) {
            // Якщо кількість елементів менше або дорівнює порогу, вивести частину масиву
            System.out.println("Ліва частина масиву:");
            int sum = 0;
            for (int i = start; i < end; i++) {
                System.out.print(leftPart[i] + " ");
                sum += leftPart[i];  // додаємо елемент до суми
                if ((i + 1) % 20 == 0) {
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println("Сума лівої частини масиву: " + sum);  // виводимо суму
        } else {
            // Якщо кількість елементів більша порогу, ділимо далі
            int mid = (start + end) / 2;
            divideLeft(start, mid); // рекурсивно ділимо ліву частину
        }
    }

    // Ділимо праву частину масиву
    public static void divideRight(int start, int end) {
        if (end - start <= THRESHOLD) {
            // Якщо кількість елементів менше або дорівнює порогу, вивести частину масиву
            System.out.println("Права частина масиву:");
            int sum = 0;
            for (int i = start; i < end; i++) {
                System.out.print(rightPart[i] + " ");
                sum += rightPart[i];  // додаємо елемент до суми
                if ((i + 1) % 20 == 0) {
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println("Сума правої частини масиву: " + sum);  // виводимо суму
        } else {
            // Якщо кількість елементів більша порогу, ділимо далі
            int mid = (start + end) / 2;
            divideRight(mid, end); // рекурсивно ділимо праву частину
        }
    }

    // Метод для запису масиву в файл
    public static void writeArrayToFile(int[] array, String filename) {
        File file = new File(filename);
        try (FileWriter writer = new FileWriter(file)) {
            for (int num : array) {
                writer.write(num + " ");
            }
            System.out.println("Масив записано у файл: " + filename);
        } catch (IOException e) {
            System.err.println("Помилка запису у файл: " + e.getMessage());
        }
    }
}
