import java.util.Scanner;

public class Main {

    // Головна функція для перевірки, чи є число надпростим
    public static boolean isSuperPrime(int number) {
        if (number < 1 || number > 1000) {
            throw new IllegalArgumentException("Введіть натуральне число від 1 до 1000.");
        }
        return isPrime(number) && isPrime(reverseNumber(number));
    }

    // Перевірка, чи є число простим
    private static boolean isPrime(int number) {
        if (number < 2) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    // Функція для обертання цифр числа
    private static int reverseNumber(int number) {
        int reversed = 0;
        while (number > 0) {
            reversed = reversed * 10 + number % 10;
            number /= 10;
        }
        return reversed;
    }

    // Тестування функції
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int number;

            while (true) {
                System.out.print("Введіть натуральне число (не більше 1000): ");
                number = scanner.nextInt();

                if (number > 0 && number <= 1000) {
                    break;
                } else {
                    System.out.println("Некоректне число. Спробуйте ще раз.");
                }
            }

            if (isSuperPrime(number)) {
                System.out.println("Число " + number + " є надпростим.");
            } else {
                System.out.println("Число " + number + " не є надпростим.");
            }

            System.out.print("Введіть \n1 — щоб перевірити ще одне число \n2 — щоб завершити роботу: ");
            int choice = scanner.nextInt();

            if (choice == 2) {
                System.out.println("Дякуємо за використання програми! До побачення.");
                break;
            } else if (choice != 1) {
                System.out.println("Некоректний вибір. Програма завершена.");
                break;
            }
        }

        scanner.close();
    }
}
