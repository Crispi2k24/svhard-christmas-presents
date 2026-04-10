package pl.svhard.christmas.presents.present;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PresentIdMapper {

    Optional<Present> getPresentByNumericId(int numericId);

    Optional<Integer> getNumericId(PresentId presentId);

    void updateMapping(Collection<Present> presents);

    List<Present> getAllPresentsSortedByNumericId();
}
