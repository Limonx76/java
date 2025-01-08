import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// Люди
abstract class Human implements Serializable {
    private static final AtomicInteger idGenerator = new AtomicInteger(1);
    private final int id;

    public Human() {
        this.id = idGenerator.getAndIncrement() % 100; // Айді від 1 до 99
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(ID: " + id + ")";
    }
}

class Firefighter extends Human {}
class Policeman extends Human {}
class RegularPassenger extends Human {}

// Транспортні засоби
abstract class Transport<T extends Human> {
    private static final AtomicInteger idGenerator = new AtomicInteger(1);
    private final int id;
    private final int maxSeats;
    private final List<T> passengers = new ArrayList<>();

    public Transport(int maxSeats) {
        this.id = idGenerator.getAndIncrement() % 100; // Айді від 1 до 99
        this.maxSeats = maxSeats;
    }

    public int getId() {
        return id;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public int getOccupiedSeats() {
        return passengers.size();
    }

    public void boardPassenger(Human passenger) {
        if (passengers.size() >= maxSeats) {
            throw new IllegalStateException("Немає вільних місць!");
        }
        passengers.add((T) passenger);
    }

    public void disembarkPassenger(Human passenger) {
        if (passengers.isEmpty()) {
            System.out.println("Транспорт порожній.");
            return;
        }
        if (!passengers.remove(passenger)) {
            System.out.println("Пасажир не сидить у цьому транспортному засобі.");
            return;
        }
    }

    public List<T> getPassengers() {
        return passengers;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(ID: " + id + ", Місця: " + getOccupiedSeats() + "/" + getMaxSeats() + ")";
    }

    // Метод для перевірки, чи можуть пасажири сідати в транспортний засіб
    public boolean canBoard(Human passenger) {
        return true; // за замовчуванням всі пасажири можуть сідати в будь-який транспорт
    }

    public boolean isPassengerInTransport(Human passenger) {
        return passengers.stream().anyMatch(p -> p.getId() == passenger.getId());
    }
}

class Bus extends Transport<Human> {
    public Bus() {
        super(12);
    }
}

class Taxi extends Transport<Human> {
    public Taxi() {
        super(3);
    }
}

class FireEngine extends Transport<Firefighter> {
    public FireEngine() {
        super(8);
    }

    @Override
    public boolean canBoard(Human passenger) {
        return passenger instanceof Firefighter;
    }
}

class PoliceCar extends Transport<Policeman> {
    public PoliceCar() {
        super(3);
    }

    @Override
    public boolean canBoard(Human passenger) {
        return passenger instanceof Policeman;
    }
}

// Клас Дорога
class Road {
    private final List<Transport<? extends Human>> carsInRoad = new ArrayList<>();

    public int getCountOfHumans() {
        return carsInRoad.stream().mapToInt(vehicle -> vehicle.getPassengers().size()).sum();
    }

    public void addCarToRoad(Transport<? extends Human> transport) {
        carsInRoad.add(transport);
    }

    public List<Transport<? extends Human>> getCarsInRoad() {
        return carsInRoad;
    }

    public Transport<? extends Human> getTransportById(int id) {
        return carsInRoad.stream()
                .filter(transport -> transport.getId() == id)
                .findFirst()
                .orElse(null);
    }
}

// Збереження та завантаження даних
class FileManager {
    public static void savePassengersToFile(String filePath, List<? extends Human> passengers) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(passengers);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<? extends Human> loadPassengersFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<? extends Human>) ois.readObject();
        }
    }
}

// Головна програма
public class Main {
    private static final Set<Integer> assignedFirefighters = new HashSet<>();
    private static final Set<Integer> assignedPolicemen = new HashSet<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Road road = new Road();
        List<Human> allPassengers = new ArrayList<>();
        boolean running = true;

        while (running) {
            System.out.println("\nМеню:");
            System.out.println("1. Створити пасажира");
            System.out.println("2. Створити транспортний засіб");
            System.out.println("3. Посадка пасажира в т.з.");
            System.out.println("4. Висадка пасажира з т.з.");
            System.out.println("5. Переглянути транспорт на дорозі");
            System.out.println("6. Вийти з програми");
            System.out.print("Оберіть опцію: ");

            String choice = scanner.nextLine();

            if (isValidChoice(choice, 1, 6)) {
                switch (Integer.parseInt(choice)) {
                    case 1 -> createPassenger(scanner, allPassengers);
                    case 2 -> createTransport(scanner, road);
                    case 3 -> boardPassenger(scanner, road, allPassengers);
                    case 4 -> disembarkPassenger(scanner, road);
                    case 5 -> viewTransportOnRoad(road);
                    case 6 -> {
                        System.out.println("Вихід з програми...");
                        running = false;
                    }
                }
            } else {
                System.out.println("Невірний вибір! Спробуйте ще раз.");
            }
        }

        scanner.close();
    }

    private static void createPassenger(Scanner scanner, List<Human> allPassengers) {
        while (true) {
            System.out.println("Оберіть тип пасажира: 1. Звичайний 2. Пожежник 3. Поліцейський 4. Повернутися");
            String type = scanner.nextLine();

            if (type.equals("4")) {
                break; // повернення до меню
            }

            if (isValidChoice(type, 1, 3)) {
                Human passenger = switch (Integer.parseInt(type)) {
                    case 1 -> new RegularPassenger();
                    case 2 -> new Firefighter();
                    case 3 -> new Policeman();
                    default -> null;
                };
                allPassengers.add(passenger);
                System.out.println("Створено: " + passenger);
                break; // повернення до меню після створення
            } else {
                System.out.println("Невірний вибір! Спробуйте ще раз.");
            }
        }
    }

    private static void createTransport(Scanner scanner, Road road) {
        while (true) {
            System.out.println("Оберіть тип транспорту: 1. Автобус 2. Таксі 3. Пожежна машина 4. Поліцейська машина 5. Повернутися");
            String type = scanner.nextLine();

            if (type.equals("5")) {
                break; // повернення до меню
            }

            if (isValidChoice(type, 1, 4)) {
                Transport<?> transport = switch (Integer.parseInt(type)) {
                    case 1 -> new Bus();
                    case 2 -> new Taxi();
                    case 3 -> new FireEngine();
                    case 4 -> new PoliceCar();
                    default -> null;
                };
                road.addCarToRoad(transport);
                System.out.println("Створено та додано на дорогу: " + transport);
                break; // повернення до меню після створення транспорту
            } else {
                System.out.println("Невірний вибір! Спробуйте ще раз.");
            }
        }
    }

    private static void boardPassenger(Scanner scanner, Road road, List<Human> allPassengers) {
        while (true) {
            System.out.println("Доступний транспорт на дорозі (напишіть 'вих', щоб вийти):");
            List<Transport<? extends Human>> cars = road.getCarsInRoad();
            for (Transport<? extends Human> transport : cars) {
                System.out.println(transport);
            }
            System.out.print("Оберіть транспортний засіб (ID): ");
            String transportChoice = scanner.nextLine();
            if (transportChoice.equals("вих")) {
                break; // повернення до меню
            }

            if (isValidChoice(transportChoice, 1, Integer.MAX_VALUE)) {
                int transportId = Integer.parseInt(transportChoice);
                Transport<? extends Human> selectedTransport = road.getTransportById(transportId);

                if (selectedTransport == null) {
                    System.out.println("Транспорт не знайдено! Спробуйте ще раз.");
                    continue;
                }

                List<Human> availablePassengers = new ArrayList<>();
                for (Human passenger : allPassengers) {
                    if (selectedTransport.canBoard(passenger) && !selectedTransport.isPassengerInTransport(passenger)) {
                        if (passenger instanceof Firefighter && selectedTransport instanceof FireEngine &&
                                !assignedFirefighters.contains(passenger.getId())) {
                            availablePassengers.add(passenger);
                        } else if (passenger instanceof Policeman && selectedTransport instanceof PoliceCar &&
                                !assignedPolicemen.contains(passenger.getId())) {
                            availablePassengers.add(passenger);
                        } else if (!(passenger instanceof Firefighter || passenger instanceof Policeman)) {
                            availablePassengers.add(passenger);
                        }
                    }
                }

                if (availablePassengers.isEmpty()) {
                    System.out.println("Для цього транспорту немає вільного пасажира відповідного типу. Спробуйте інший транспорт.");
                    continue; // повернення до вибору транспорту
                }

                System.out.println("Доступні пасажири:");
                for (Human passenger : availablePassengers) {
                    System.out.println(passenger);
                }

                System.out.print("Оберіть пасажира (ID): ");
                String passengerChoice = scanner.nextLine();

                if (passengerChoice.equals("вих")) {
                    break; // повернення до меню
                }

                if (isValidChoice(passengerChoice, 1, Integer.MAX_VALUE)) {
                    int passengerId = Integer.parseInt(passengerChoice);
                    Human selectedPassenger = availablePassengers.stream()
                            .filter(p -> p.getId() == passengerId)
                            .findFirst()
                            .orElse(null);

                    if (selectedPassenger == null) {
                        System.out.println("Невірний вибір пасажира! Спробуйте ще раз.");
                        continue;
                    }

                    try {
                        selectedTransport.boardPassenger(selectedPassenger);
                        if (selectedPassenger instanceof Firefighter && selectedTransport instanceof FireEngine) {
                            assignedFirefighters.add(selectedPassenger.getId());
                        } else if (selectedPassenger instanceof Policeman && selectedTransport instanceof PoliceCar) {
                            assignedPolicemen.add(selectedPassenger.getId());
                        }
                        System.out.println("Пасажир доданий: " + selectedPassenger);
                        break; // повернення до меню після успішної посадки
                    } catch (Exception e) {
                        System.out.println("Помилка: " + e.getMessage());
                    }
                } else {
                    System.out.println("Невірний вибір! Спробуйте ще раз.");
                }
            } else {
                System.out.println("Невірний вибір транспорту! Спробуйте ще раз.");
            }
        }
    }

    private static void disembarkPassenger(Scanner scanner, Road road) {
        while (true) {
            System.out.println("Доступний транспорт на дорозі (напишіть 'вих', щоб вийти):");
            for (Transport<? extends Human> transport : road.getCarsInRoad()) {
                System.out.println(transport);
            }
            System.out.print("Оберіть транспортний засіб (ID): ");
            String transportChoice = scanner.nextLine();
            if (transportChoice.equals("вих")) {
                break; // повернення до меню
            }

            if (isValidChoice(transportChoice, 1, Integer.MAX_VALUE)) {
                int transportId = Integer.parseInt(transportChoice);
                Transport<? extends Human> selectedTransport = road.getTransportById(transportId);

                if (selectedTransport == null) {
                    System.out.println("Транспорт не знайдено! Спробуйте ще раз.");
                    continue;
                }

                if (selectedTransport.getPassengers().isEmpty()) {
                    System.out.println("Транспорт порожній.");
                    continue;
                }

                System.out.println("Доступні пасажири:");
                for (Human passenger : selectedTransport.getPassengers()) {
                    System.out.println(passenger);
                }

                System.out.print("Оберіть пасажира для висадки (ID): ");
                String passengerChoice = scanner.nextLine();

                if (passengerChoice.equals("вих")) {
                    break; // повернення до меню
                }

                if (isValidChoice(passengerChoice, 1, Integer.MAX_VALUE)) {
                    int passengerId = Integer.parseInt(passengerChoice);
                    Human selectedPassenger = selectedTransport.getPassengers().stream()
                            .filter(p -> p.getId() == passengerId)
                            .findFirst()
                            .orElse(null);

                    if (selectedPassenger == null) {
                        System.out.println("Невірний вибір пасажира! Спробуйте ще раз.");
                        continue;
                    }

                    selectedTransport.disembarkPassenger(selectedPassenger);
                    if (selectedPassenger instanceof Firefighter && selectedTransport instanceof FireEngine) {
                        assignedFirefighters.remove(selectedPassenger.getId());
                    } else if (selectedPassenger instanceof Policeman && selectedTransport instanceof PoliceCar) {
                        assignedPolicemen.remove(selectedPassenger.getId());
                    }
                    System.out.println("Пасажир висаджений: " + selectedPassenger);
                    break; // повернення до меню після успішної висадки
                } else {
                    System.out.println("Невірний вибір! Спробуйте ще раз.");
                }
            } else {
                System.out.println("Невірний вибір транспорту! Спробуйте ще раз.");
            }
        }
    }

    private static void viewTransportOnRoad(Road road) {
        System.out.println("Транспорт на дорозі:");
        for (Transport<? extends Human> transport : road.getCarsInRoad()) {
            System.out.println(transport);
        }
    }

    private static boolean isValidChoice(String choice, int min, int max) {
        try {
            int num = Integer.parseInt(choice);
            return num >= min && num <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
