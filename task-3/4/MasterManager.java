import java.util.ArrayList;
import java.util.List;

public class MasterManager {
    private final List<Master> masters = new ArrayList<>();

    public void addMaster(Master master) {
        masters.add(master);
    }

    public void removeMaster(Master master) {
        masters.remove(master);
    }
}
