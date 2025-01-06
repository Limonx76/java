import java.util.*;

class JournalEntry {
    private String lastName;
    private String firstName;
    private String birthDate;
    private String phone;
    private String address;

    // Конструктор
    public JournalEntry(String lastName, String firstName, String birthDate, String phone, String address) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
    }

    // Геттери
    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Прізвище: " + lastName + "\n" +
                "Ім'я: " + firstName + "\n" +
                "Дата народження: " + birthDate + "\n" +
                "Телефон: " + phone + "\n" +
                "Адреса: " + address;
    }

    // Метод для оновлення запису
    public void updateEntry(String lastName, String firstName, String birthDate, String phone, String address) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
    }
}

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<JournalEntry> journalEntries = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1. Додати запис");
            System.out.println("2. Переглянути всі записи");
            System.out.println("3. Змінити запис");
            System.out.println("4. Видалити запис");
            System.out.println("5. Вихід");
            System.out.print("Оберіть опцію: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // споживаємо новий рядок після вибору

            switch (choice) {
                case 1:
                    addEntry();
                    break;
                case 2:
                    displayEntries();
                    break;
                case 3:
                    updateEntry();
                    break;
                case 4:
                    deleteEntry();
                    break;
                case 5:
                    System.out.println("Вихід...");
                    return;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private static void addEntry() {
        String lastName = getValidInput("Прізвище студента: ", "^[а-яА-ЯієїІЄЇ]+$");
        String firstName = getValidInput("Ім'я студента: ", "^[а-яА-ЯієїІЄЇ]+$");
        String birthDate = getValidDate("Дата народження студента (ДД.ММ.РРРР): ");
        String phone = getValidInput("Телефон студента (формат: +380XXXXXXXXX): ", "^\\+380\\d{9}$");
        String address = getValidAddress("Домашня адреса (вулиця, номер будинку, квартира): ");

        JournalEntry entry = new JournalEntry(lastName, firstName, birthDate, phone, address);
        journalEntries.add(entry);

        System.out.println("Запис додано.");
    }

    private static void displayEntries() {
        if (journalEntries.isEmpty()) {
            System.out.println("Журнал порожній.");
        } else {
            System.out.println("\nЗаписи журналу:");
            for (JournalEntry entry : journalEntries) {
                System.out.println(entry);
                System.out.println(); // Пустий рядок після кожного запису для відділення
            }
        }
    }

    private static void updateEntry() {
        if (journalEntries.isEmpty()) {
            System.out.println("Журнал порожній.");
            return;
        }

        System.out.print("Введіть номер запису для зміни: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine(); // споживаємо новий рядок

        if (index < 0 || index >= journalEntries.size()) {
            System.out.println("Невірний номер запису.");
            return;
        }

        JournalEntry entry = journalEntries.get(index);
        System.out.println("Зміна запису для: " + entry);

        String lastName = getValidInput("Прізвище студента: ", "^[а-яА-ЯієїІЄЇ]+$");
        String firstName = getValidInput("Ім'я студента: ", "^[а-яА-ЯієїІЄЇ]+$");
        String birthDate = getValidDate("Дата народження студента (ДД.ММ.РРРР): ");
        String phone = getValidInput("Телефон студента (формат: +380XXXXXXXXX): ", "^\\+380\\d{9}$");
        String address = getValidAddress("Домашня адреса (вулиця, номер будинку, квартира): ");

        entry.updateEntry(lastName, firstName, birthDate, phone, address);
        System.out.println("Запис оновлено.");
    }

    private static void deleteEntry() {
        if (journalEntries.isEmpty()) {
            System.out.println("Журнал порожній.");
            return;
        }

        System.out.print("Введіть номер запису для видалення: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine(); // споживаємо новий рядок

        if (index < 0 || index >= journalEntries.size()) {
            System.out.println("Невірний номер запису.");
            return;
        }

        JournalEntry entry = journalEntries.remove(index);
        System.out.println("Запис видалено: " + entry);
    }

    private static String getValidInput(String prompt, String regex) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.matches(regex)) {
                return input;
            } else {
                System.out.println("Невірний формат. Спробуйте ще раз.");
            }
        }
    }

    private static String getValidDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String date = scanner.nextLine().trim();
            if (isValidDate(date)) {
                return date;
            } else {
                System.out.println("Невірний формат дати. Спробуйте ще раз.");
            }
        }
    }

    private static boolean isValidDate(String date) {
        // Перевірка формату дати (ДД.ММ.РРРР)
        String regex = "^\\d{2}\\.\\d{2}\\.\\d{4}$";
        return date.matches(regex);
    }

    private static String getValidAddress(String prompt) {
        while (true) {
            System.out.print(prompt);
            String address = scanner.nextLine().trim();
            if (isValidAddress(address)) {
                return address;
            } else {
                System.out.println("Невірний формат адреси. Спробуйте ще раз.");
            }
        }
    }

    private static boolean isValidAddress(String address) {
        // Оновлений регулярний вираз для перевірки адреси (вулиця, номер будинку з буквами/символами, квартира)
        String regex = "^[а-яА-ЯієїІЄЇ]+(\\s?[а-яА-ЯієїІЄЇ]+)*,\\s?\\d+[а-яА-Яа-яё\\w-]*,\\s?\\d+$";
        return address.matches(regex);
    }
}
