package carservice.models.master;

import java.util.Set;

public record FilterParams(Set<Integer> excludedIds) {
}
