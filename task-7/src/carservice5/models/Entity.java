package carservice5.models;

import java.io.Serializable;

public abstract class Entity implements IHasId, Serializable {
    protected int id;

    public Entity(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
