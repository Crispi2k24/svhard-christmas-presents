package pl.svhard.christmas.presents.reward;

public final class CollectionResult {

    private final CollectionStatus status;
    private final int collectedCount;
    private final int totalCount;
    private final boolean completedAll;

    private CollectionResult(CollectionStatus status, int collectedCount, int totalCount, boolean completedAll) {
        this.status = status;
        this.collectedCount = collectedCount;
        this.totalCount = totalCount;
        this.completedAll = completedAll;
    }

    public static CollectionResult success(int collectedCount, int totalCount, boolean completedAll) {
        return new CollectionResult(CollectionStatus.SUCCESS, collectedCount, totalCount, completedAll);
    }

    public static CollectionResult alreadyCollected() {
        return new CollectionResult(CollectionStatus.ALREADY_COLLECTED, 0, 0, false);
    }

    public CollectionStatus getStatus() {
        return this.status;
    }

    public int getCollectedCount() {
        return this.collectedCount;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public boolean isCompletedAll() {
        return this.completedAll;
    }

    public boolean isSuccess() {
        return this.status == CollectionStatus.SUCCESS;
    }

    public enum CollectionStatus {
        SUCCESS,
        ALREADY_COLLECTED
    }
}
