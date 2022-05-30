package neostudy.advices;

public class ScoringException extends Exception {

    private final String message;

    public ScoringException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
