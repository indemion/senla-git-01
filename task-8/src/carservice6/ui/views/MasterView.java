package carservice6.ui.views;

import carservice6.models.master.Master;

import java.util.List;

public class MasterView {
    public void index(List<Master> masters) {
        for (Master master : masters) {
            System.out.println(master);
        }
    }

    public void show(Master master) {
        System.out.println(master);
    }
}
