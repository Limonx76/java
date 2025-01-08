import java.io.*;
import java.util.StringTokenizer;
import java.util.Scanner;

// Клас для фільтрування введених символів
class MyFilterReader extends FilterReader {
    protected MyFilterReader(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int c = super.read();
        if (c == -1) {
            return -1;  // до кінця файлу
        }
        // Можна додати логіку фільтрації символів, наприклад, видаляти пробіли або щось інше.
        return c;
    }
}

// Клас для фільтрування виведених символів
class MyFilterWriter extends FilterWriter {
    protected MyFilterWriter(Writer out) {
        super(out);
    }

    @Override
    public void write(int c) throws IOException {
        // Можна додати логіку фільтрації виведених символів
        super.write(c);  // Лише записуємо символ без змін
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        super.write(cbuf, off, len);  // Лише записуємо символи без змін
    }
}

public class Main {
    public static void main(String[] args) {
        String fileName = "src/text.txt"; // Ім'я файлу
        int keyShift = 3; // Ключове зміщення для шифрування (наприклад, 3 для "Caesar Cipher")

        String lineWithMaxWords = "";
        int maxWordCount = 0;

        // Зчитування файлу і пошук рядка з найбільшою кількістю слів
        try (MyFilterReader reader = new MyFilterReader(new FileReader(fileName))) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int wordCount = countWords(line);
                if (wordCount > maxWordCount) {
                    maxWordCount = wordCount;
                    lineWithMaxWords = line;
                }
            }
        } catch (IOException e) {
            System.err.println("Помилка при читанні файлу: " + e.getMessage());
            return;
        }

        // Виведення рядка з найбільшою кількістю слів
        System.out.println("Рядок з найбільшою кількістю слів:");
        System.out.println(lineWithMaxWords);
        System.out.println("Кількість слів: " + maxWordCount);

        // Створення сканера для введення користувача
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Виведення першого меню
            System.out.println("\nМеню:");
            System.out.println("1. Зашифрувати");
            System.out.println("2. Вийти");

            // Читання вибору користувача
            System.out.print("Вибери: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Зашифрування знайдений рядок
                    String encryptedLine = encrypt(lineWithMaxWords, keyShift);
                    System.out.println("Зашифрований рядок:");
                    System.out.println(encryptedLine);

                    // Запис зашифрованого рядка в файл за допомогою FilterWriter
                    try (MyFilterWriter writer = new MyFilterWriter(new BufferedWriter(new FileWriter("out_text.txt")))) {
                        writer.write(encryptedLine);
                    } catch (IOException e) {
                        System.err.println("Помилка при записі в файл: " + e.getMessage());
                    }

                    // Показуємо нове меню після шифрування
                    while (true) {
                        System.out.println("\nМеню:");
                        System.out.println("1. Розшифрувати");
                        System.out.println("2. Вийти");

                        // Читання вибору користувача
                        System.out.print("Вибери: ");
                        int secondChoice = scanner.nextInt();

                        switch (secondChoice) {
                            case 1:
                                // Розшифровка рядка
                                String decryptedLine = decrypt(encryptedLine, keyShift);
                                System.out.println("Розшифрований рядок:");
                                System.out.println(decryptedLine);
                                break;
                            case 2:
                                // Вийти з програми
                                System.out.println("До побачення!");
                                return;
                            default:
                                System.out.println("Невірний вибір, спробуйте ще раз.");
                        }

                        // Після розшифрування повертаємося до меню шифрування
                        System.out.println("\nМеню:");
                        System.out.println("1. Зашифрувати");
                        System.out.println("2. Вийти");

                        // Читання вибору користувача
                        System.out.print("Вибери: ");
                        int restartChoice = scanner.nextInt();

                        if (restartChoice == 1) {
                            // Якщо вибір знову "1", зашифруємо повторно
                            encryptedLine = encrypt(lineWithMaxWords, keyShift);
                            System.out.println("Зашифрований рядок:");
                            System.out.println(encryptedLine);
                        } else if (restartChoice == 2) {
                            // Вийти з програми
                            System.out.println("До побачення!");
                            return;
                        }
                    }

                case 2:
                    // Вийти з програми
                    System.out.println("До побачення!");
                    return;
                default:
                    System.out.println("Невірний вибір, спробуйте ще раз.");
            }
        }
    }

    // Метод для підрахунку слів у рядку
    private static int countWords(String line) {
        if (line == null || line.isEmpty()) {
            return 0;
        }
        StringTokenizer tokenizer = new StringTokenizer(line);
        return tokenizer.countTokens();
    }

    // Метод для шифрування рядка
    private static String encrypt(String input, int keyShift) {
        StringBuilder encrypted = new StringBuilder();

        for (char ch : input.toCharArray()) {
            // Перевірка, чи є символ буквою
            if (Character.isLetter(ch)) {
                // Визначення базового символу (для збереження регістру)
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                // Обчислення зміщеного символу
                char newChar = (char) ((ch - base + keyShift) % 26 + base);
                encrypted.append(newChar);
            } else {
                // Якщо не літера, залишаємо символ без змін
                encrypted.append(ch);
            }
        }

        return encrypted.toString();
    }

    // Метод для розшифрування рядка
    private static String decrypt(String input, int keyShift) {
        StringBuilder decrypted = new StringBuilder();

        for (char ch : input.toCharArray()) {
            // Перевірка, чи є символ буквою
            if (Character.isLetter(ch)) {
                // Визначення базового символу (для збереження регістру)
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                // Обчислення зміщеного символу для розшифровки
                char newChar = (char) ((ch - base - keyShift + 26) % 26 + base);
                decrypted.append(newChar);
            } else {
                // Якщо не літера, залишаємо символ без змін
                decrypted.append(ch);
            }
        }

        return decrypted.toString();
    }
}
