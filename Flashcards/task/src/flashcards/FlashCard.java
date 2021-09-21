package flashcards;

public class FlashCard {
    private final String term;
    private final String description;
    private int failureCounter = 0;

    public FlashCard(String term, String description) {
        this(term,description,0);
    }

    public FlashCard(String term, String description, int failureCounter) {
        this.term = term;
        this.description = description;
        this.failureCounter = failureCounter;
    }

    public void incrementFailureCounter() {
        this.failureCounter++;
    }

    public void resetFailureCounter() {
        this.failureCounter = 0;
    }


    public String getTerm() {
        return term;
    }

    public String getDescription() {
        return description;
    }

    public int getFailureCounter() {
        return failureCounter;
    }
}
