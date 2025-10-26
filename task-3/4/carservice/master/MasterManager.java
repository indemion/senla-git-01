package carservice.master;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MasterManager {
    private final List<Master> masters = new ArrayList<>();

    public Master createMaster(String fullname, String phone) {
        Master master = new Master(fullname, phone);
        masters.add(master);
        System.out.printf("Добавлен мастер \"%s\"%n", master.getFullname());

        return master;
    }

    public void removeMaster(int id) {
        Optional<Master> master = masters.stream().filter(m -> m.getId() == id).findFirst();
        if (master.isEmpty()) {
            return;
        }
        masters.remove(master.get());
        System.out.printf("Удалён мастер \"%s\"%n", master.get().getFullname());
    }
}
