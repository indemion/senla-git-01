package carservice4.models;

public class ID {
    public int value;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ID id = (ID) obj;
        return value == id.value;
    }

    @Override
    public int hashCode() {
        return 31 * value;
    }
}
