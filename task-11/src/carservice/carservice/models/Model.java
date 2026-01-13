package carservice.models;

import java.io.Serializable;

public abstract class Model implements IHasId, Serializable {
    protected int id;

    public Model(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
