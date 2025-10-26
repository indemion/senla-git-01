// Вариант 1: написать программу содержащую иерархии цветов для цветочного магазина. Собрать букет и определить его стоимость.

public class FlowerShop {
    public static void main(String[] args) {
        Bouquet bouquet = new Bouquet();
        bouquet.addFlower(new Rose(10.99));
        bouquet.addFlower(new Rose(10.99));
        bouquet.addFlower(new Tulip(3.50));
        bouquet.addFlower(new Orchid(15.33));
        bouquet.addFlower(new Orchid(15.33));
        bouquet.addFlower(new Orchid(15.33));

        System.out.println(String.format("Bouquet price = %.2f", bouquet.getPrice()));
    }
}
