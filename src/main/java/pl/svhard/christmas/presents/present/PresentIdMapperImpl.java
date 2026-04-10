package pl.svhard.christmas.presents.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class PresentIdMapperImpl implements PresentIdMapper {

    private final Map<Integer, PresentId> numericToUuid = new ConcurrentHashMap<>();
    private final Map<PresentId, Integer> uuidToNumeric = new ConcurrentHashMap<>();
    private final List<Present> sortedPresents = new ArrayList<>();

    @Override
    public Optional<Present> getPresentByNumericId(int numericId) {
        if (numericId < 1 || numericId > this.sortedPresents.size()) {
            return Optional.empty();
        }

        return Optional.of(this.sortedPresents.get(numericId - 1));
    }

    @Override
    public Optional<Integer> getNumericId(PresentId presentId) {
        return Optional.ofNullable(this.uuidToNumeric.get(presentId));
    }

    @Override
    public void updateMapping(Collection<Present> presents) {
        this.numericToUuid.clear();
        this.uuidToNumeric.clear();
        this.sortedPresents.clear();

        List<Present> sorted = new ArrayList<>(presents);
        sorted.sort(Comparator.comparing(present -> present.getId().toUUID()));

        this.sortedPresents.addAll(sorted);

        for (int i = 0; i < sorted.size(); i++) {
            Present present = sorted.get(i);
            int numericId = i + 1;

            this.numericToUuid.put(numericId, present.getId());
            this.uuidToNumeric.put(present.getId(), numericId);
        }
    }

    @Override
    public List<Present> getAllPresentsSortedByNumericId() {
        return List.copyOf(this.sortedPresents);
    }
}
