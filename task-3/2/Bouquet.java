import java.util.ArrayList;
import java.util.List;

public class Bouquet {
    private final List<Flower> flowerList = new ArrayList<>();

    public void addFlower(Flower flower) {
        flowerList.add(flower);
    }

    public double getPrice() {
        double price = 0;
        for (Flower flower : flowerList) {
            price += flower.getPrice();
        }

        return price;
    }
}