package com.gmail.sge.serejka;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.*;


public class App {

    static EntityManager manager;
    static EntityManagerFactory managerFactory;
    static final Random random = new Random();

    public static void main(String[] args) {
        managerFactory = Persistence.createEntityManagerFactory("JPARest");
        manager = managerFactory.createEntityManager();
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("The restaurant");
                System.out.println("1 - Add new dish to Menu");
                System.out.println("2 - Add random dishes");
                System.out.println("3 - Delete dish from Menu");
                System.out.println("4 - Show all dished in Menu");
                System.out.println("5 - Show all dishes with discount");
                System.out.println("6 - Show set less then 1 kg");
                System.out.println("7 - Show dishes filtered by price");

                String x = scanner.nextLine();
                switch (x) {
                    case "1":
                        addDish(scanner);
                        break;
                    case "2":
                        addRandomDishes(scanner);
                        break;
                    case "3":
                        deleteDish(scanner);
                        break;
                    case "4":
                        showAllDishes(false);
                        break;
                    case "5":
                        showWithDisc();
                        break;
                    case "6":
                        showAllDishes(true);
                        break;
                    case "7":
                        showFilteredDishes(scanner);
                        break;
                    default:
                        return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            scanner.close();
            manager.close();
            managerFactory.close();
        }
    }

    public static void addDish(Scanner scanner) {
        System.out.println("Enter the dish name");
        String name = scanner.nextLine();
        System.out.println("Enter the dish price");
        int price = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the dish weight");
        int weight = Integer.parseInt(scanner.nextLine());
        System.out.println("Does it have a discount? Yes or no");
        String s = scanner.nextLine();
        int discount;
        if (s.toLowerCase(Locale.ROOT).equals("yes")) {
            discount = 1;
        } else {
            discount = 0;
        }
        manager.getTransaction().begin();
        try {
            Dish d = new Dish(name, price, weight, discount);
            manager.persist(d);
            manager.getTransaction().commit();

            System.out.println(d.getId());
        } catch (Exception e) {
            manager.getTransaction().rollback();
        }
    }

    public static void addRandomDishes(Scanner scanner) {

        System.out.println("Enter the amount of dishes you want to add");
        int amount = Integer.parseInt(scanner.nextLine());
        String[] names = {"Pizza", "Kebab", "Hamburger", "Salad", "Spaghetti"};
        for (int i = 0; i < amount; i++) {
            manager.getTransaction().begin();
            try {
                Dish d = new Dish(names[random.nextInt(5)], random.nextInt(100),
                        random.nextInt(500), random.nextInt(2));
                manager.persist(d);
                manager.getTransaction().commit();
            } catch (Exception e) {
                manager.getTransaction().rollback();
            }
        }
    }

    public static void deleteDish(Scanner scanner) {
        System.out.println("Enter id of the dish you want to delete");
        Long id = Long.parseLong(scanner.nextLine());
        Dish d = manager.getReference(Dish.class, id);
        if (d == null) {
            System.out.println("Dish not found!");
            return;
        }
        manager.getTransaction().begin();
        try {
            manager.remove(d);
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
        }
    }

    public static void showAllDishes(boolean kg) {
        Query query = manager.createQuery("select d from Dish d", Dish.class);
        List<Dish> dishList = (List<Dish>) query.getResultList();
        if (kg) {
            int weight = 0;
            List<Dish> resultSet = new ArrayList<>();
            for (Dish d : dishList) {
                if ((weight + d.getWeight()) <= 1000) {
                    weight += d.getWeight();
                    resultSet.add(d);
                }
            }
            for (Dish d : resultSet) {
                System.out.println(d);
            }
        } else {
            for (Dish d : dishList) {
                System.out.println(d);
            }
        }
    }

    public static void showWithDisc() {
        Query query = manager.createQuery("select d from Dish d where discount = 1", Dish.class);
        List<Dish> dishList = (List<Dish>) query.getResultList();
        for (Dish d : dishList) {
            System.out.println(d);
        }
    }

    public static void showFilteredDishes(Scanner scanner) {
        System.out.println("Enter the minimum price");
        int min = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the maximun price");
        int max = Integer.parseInt(scanner.nextLine());
        Query query = manager.createQuery("select d from Dish d where price > " + min + " and price < " + max,Dish.class);
        List<Dish> result = (List<Dish>) query.getResultList();
        for (Dish d : result) {
            System.out.println(d);
        }
    }
}