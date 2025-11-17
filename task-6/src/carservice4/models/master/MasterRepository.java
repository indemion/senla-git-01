package carservice4.models.master;

import carservice4.models.repositories.InMemoryRepository;

public class MasterRepository extends InMemoryRepository<Master> {
    private static MasterRepository instance;

    private MasterRepository() {
    }

    public static MasterRepository instance() {
        if (instance == null) {
            instance = new MasterRepository();
        }

        return instance;
    }
}
