import java.io.*;
import java.util.*;

// Модель
class Card {
    private final String cardType;
    private String validity;
    private double balance;
    private final int id;

    public Card(String cardType, String validity) {
        this.cardType = cardType;
        this.validity = validity;
        this.balance = 0.00;
        this.id = generateUniqueId();
    }

    public Card(String cardType, String validity, double balance) {
        this.cardType = cardType;
        this.validity = validity;
        this.balance = balance;
        this.id = generateUniqueId();
    }

    private int generateUniqueId() {
        Random random = new Random();
        return 100 + random.nextInt(900); // Генеруємо унікальний ID
    }

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public String getCardDetails() {
        if (cardType.contains("накопичувальна")) {
            return "Карта: " + cardType + ", Баланс: " + String.format("%.2f", balance) + ", ID: " + id;
        }
        String[] validityParts = validity.split(", ");
        String validityTerm = validityParts[0];
        String trips = validityParts.length > 1 ? validityParts[1] : "";
        return "Карта: " + cardType + ", Термін: " + validityTerm + ", Кількість поїздок: " + trips + ", ID: " + id;
    }

    public String toFileFormat() {
        return cardType + "," + validity + "," + String.format("%.2f", balance) + "," + id;
    }

    public static Card fromFileFormat(String line) {
        String[] parts = line.split(",");
        String cardType = parts[0];
        String validity = parts[1];

        double balance = 0.0;
        try {
            balance = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            System.out.println("Невірний формат балансу: " + parts[2]);
        }

        return new Card(cardType, validity, balance);
    }

    public void setValidity(String newValidity) {
        this.validity = newValidity;
    }

    public String getValidity() {
        return validity;
    }

    public double getBalance() {
        return balance;
    }

    public int getId() {
        return id;
    }

    public String getCardType() {
        return cardType;
    }
}

// Вигляд
class TurnstileView {
    public void displayMenu() {
        System.out.println("Оберіть дію:");
        System.out.println("1. Випустити проїзну картку");
        System.out.println("2. Зчитати картку");
        System.out.println("3. Переглянути сумарні дані");
        System.out.println("4. Переглянути дані розбиті по типах проїзних квитків");
        System.out.println("5. Вихід");
    }

    public void displayCardTypeMenu() {
        System.out.println("Оберіть тип картки:");
        System.out.println("1. Учнівська");
        System.out.println("2. Студентська");
        System.out.println("3. Звичайна");
    }

    public void displayValidityMenu() {
        System.out.println("Оберіть термін дії:");
        System.out.println("1. На місяць");
        System.out.println("2. На 10 днів");
    }

    public void displayTripsMenu() {
        System.out.println("Оберіть кількість поїздок:");
        System.out.println("1. 5 поїздок");
        System.out.println("2. 10 поїздок");
    }

    public void displayCardDetails(String cardDetails) {
        System.out.println("Створено картку: " + cardDetails);
    }

    public void displayInvalidChoice() {
        System.out.println("Неправильний вибір. Спробуйте ще раз.");
    }

    public void displayBalancePrompt() {
        System.out.println("Чи хочете поповнити баланс? (так/ні): ");
    }

    public void displayBalanceInput() {
        System.out.println("Введіть суму поповнення (не більше двох цифр після коми): ");
    }

    public void displayCardsByType(Map<String, int[]> cardStats) {
        for (Map.Entry<String, int[]> entry : cardStats.entrySet()) {
            String cardType = entry.getKey();
            int[] stats = entry.getValue();
            System.out.println("Тип карток: " + cardType);
            System.out.println("Прийняті операції: " + (stats[0]-1));
            System.out.println("Відхилені операції: " + stats[1]);
        }
    }

    public void displaySummary(int acceptedOperations, int rejectedOperations) {
        System.out.println("Сумарні дані:");
        System.out.println("Прийнятих операцій: " + acceptedOperations);
        System.out.println("Відхилених операцій: " + rejectedOperations);
    }
}

// Контролер
class TurnstileController {
    private final TurnstileView view;
    private final List<Card> cards;
    private final File dataFile;
    private int acceptedOperations = 0;
    private int rejectedOperations = 0;
    private final Map<String, int[]> cardStats = new HashMap<>(); // Статистика по типах карток

    public TurnstileController(TurnstileView view, String fileName) {
        this.view = view;
        this.cards = new ArrayList<>();
        this.dataFile = new File(fileName);
        loadCardsFromFile();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            view.displayMenu();
            System.out.print("Ваш вибір: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Очистити буфер

            switch (choice) {
                case 1 -> createCard(scanner);
                case 2 -> readCard(scanner);
                case 3 -> view.displaySummary(acceptedOperations, rejectedOperations);
                case 4 -> displayCardsByType(); // Тепер показуємо статистику по типах карток
                case 5 -> isRunning = false;
                default -> view.displayInvalidChoice();
            }
        }
        scanner.close();
        saveCardsToFile();
        System.out.println("Програма завершена.");
    }

    private void createCard(Scanner scanner) {
        view.displayCardTypeMenu();
        System.out.print("Введіть тип картки (1-3): ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine(); // Очистити буфер

        String cardType;
        switch (typeChoice) {
            case 1 -> cardType = "Учнівська";
            case 2 -> cardType = "Студентська";
            case 3 -> {
                System.out.print("Чи хочете створити накопичувальну картку? (так/ні): ");
                String accumulativeChoice = scanner.nextLine().toLowerCase();
                if (accumulativeChoice.equals("так")) {
                    cardType = "Звичайна (накопичувальна)";
                    Card card = new Card(cardType, "Баланс");
                    view.displayBalancePrompt();
                    String topUpChoice = scanner.nextLine().toLowerCase();
                    if (topUpChoice.equals("так")) {
                        view.displayBalanceInput();
                        String input = scanner.nextLine().replace(",", ".");
                        try {
                            double amount = Double.parseDouble(input);
                            if (amount > 0) {
                                card.addBalance(amount);
                                System.out.println("Баланс поповнено на " + amount + " грн.");
                            } else {
                                System.out.println("Введена сума має бути більшою за 0.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Неправильний формат суми. Спробуйте ще раз.");
                        }
                    }
                    cards.add(card);
                    updateStats(cardType, true);
                    view.displayCardDetails(card.getCardDetails());
                    return;
                } else {
                    cardType = "Звичайна";
                }
            }
            default -> {
                view.displayInvalidChoice();
                return;
            }
        }

        view.displayValidityMenu();
        System.out.print("Введіть термін (1/2): ");
        int validityChoice = scanner.nextInt();
        scanner.nextLine();

        String validity;
        switch (validityChoice) {
            case 1 -> validity = "На місяць";
            case 2 -> validity = "На 10 днів";
            default -> {
                view.displayInvalidChoice();
                return;
            }
        }

        view.displayTripsMenu();
        System.out.print("Введіть кількість поїздок (1/2): ");
        int tripsChoice = scanner.nextInt();
        scanner.nextLine();

        switch (tripsChoice) {
            case 1 -> validity += ", 5 поїздок";
            case 2 -> validity += ", 10 поїздок";
            default -> {
                view.displayInvalidChoice();
                return;
            }
        }

        Card card = new Card(cardType, validity);
        cards.add(card);
        updateStats(cardType, true);
        view.displayCardDetails(card.getCardDetails());
    }

    private void readCard(Scanner scanner) {
        System.out.print("Введіть ID картки: ");
        int cardId = scanner.nextInt();
        scanner.nextLine(); // Очистити буфер

        Optional<Card> cardOptional = cards.stream().filter(card -> card.getId() == cardId).findFirst();
        if (cardOptional.isPresent()) {
            Card card = cardOptional.get();

            String cardType = card.getCardDetails().split(",")[0].split(":")[1].trim();

            if (card.getCardDetails().contains("накопичувальна")) {
                if (card.getBalance() >= 8.00) {
                    card.addBalance(-8.00);
                    updateStats(cardType, true);
                    acceptedOperations++;
                    System.out.println("Операція успішна. З картки знято 8 грн.");
                    System.out.println("Залишок: " + String.format("%.2f", card.getBalance()) + " грн.");
                } else {
                    rejectedOperations++;
                    updateStats(cardType, false);
                    System.out.println("Вхід неможливий, недостатньо коштів на картці. Поповніть картку.");
                }
            } else {
                String[] validityParts = card.getValidity().split(", ");
                boolean hasTrips = false;
                for (String validity : validityParts) {
                    if (validity.contains("поїздок")) {
                        int trips = Integer.parseInt(validity.split(" ")[0]);
                        if (trips > 0) {
                            hasTrips = true;
                            card.setValidity(validity.replace(trips + " поїздок", (trips - 1) + " поїздок"));
                            updateStats(cardType, true);
                            acceptedOperations++;
                            System.out.println("Операція успішна. З картки знято одну поїздку.");
                            break;
                        }
                    }
                }

                if (!hasTrips) {
                    rejectedOperations++;
                    updateStats(cardType, false);
                    System.out.println("Вхід неможливий, недостатньо поїздок. Поповніть картку.");
                }
            }
        } else {
            System.out.println("Картку з таким ID не знайдено.");
        }
    }

    private void displayCardsByType() {
        view.displayCardsByType(cardStats);
    }

    private void updateStats(String cardType, boolean isAccepted) {
        cardStats.putIfAbsent(cardType, new int[]{0, 0});
        int[] stats = cardStats.get(cardType);
        if (isAccepted) {
            stats[0]++; // Прийняті операції
        } else {
            stats[1]++; // Відхилені операції
        }
    }

    private void loadCardsFromFile() {
        if (!dataFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Card card = Card.fromFileFormat(line);
                cards.add(card);
            }
        } catch (IOException e) {
            System.out.println("Помилка при завантаженні карток з файлу: " + e.getMessage());
        }
    }

    private void saveCardsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Card card : cards) {
                writer.write(card.toFileFormat());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Помилка при збереженні карток у файл: " + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        TurnstileView view = new TurnstileView();
        TurnstileController controller = new TurnstileController(view, "cards.txt");
        controller.run();
    }
}
