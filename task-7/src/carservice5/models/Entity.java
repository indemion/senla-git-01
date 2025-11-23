package carservice5.models;

public abstract class Entity implements IHasId {
    protected int id;

    public Entity(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
