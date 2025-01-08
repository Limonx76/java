import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class  Main {
    public static void main(String[] args) {
        try {
            // Вказуємо URL веб-сторінки
            String urlString = "https://www.wikipedia.com.ua/"; // Заміни на свій URL
            URL url = new URL(urlString);

            // Завантажуємо HTML-сторінку
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuilder htmlContent = new StringBuilder();

            // Читаємо сторінку вміст
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }
            reader.close();

            // Використовуємо регулярний вираз для пошуку HTML тегів
            Pattern pattern = Pattern.compile("<\\s*(\\w+)[^>]*>");
            Matcher matcher = pattern.matcher(htmlContent.toString());

            // Мапа для збереження частоти тегів
            Map<String, Integer> tagFrequency = new HashMap<>();

            // Обробка знайдених тегів
            while (matcher.find()) {
                String tag = matcher.group(1).toLowerCase(); // отримуємо тег, приводимо до нижнього регістру
                tagFrequency.put(tag, tagFrequency.getOrDefault(tag, 0) + 1);
            }

            // Виведення результатів в лексикографічному порядку
            System.out.println("Теги в лексикографічному порядку:");
            tagFrequency.keySet().stream()
                    .sorted()
                    .forEach(tag -> System.out.println(tag + ": " + tagFrequency.get(tag)));

            // Виведення результатів в порядку частоти
            System.out.println("\nТеги в порядку зростання частоти:");
            tagFrequency.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
