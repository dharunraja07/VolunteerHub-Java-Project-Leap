package NGO.VolunteerHub.Exception;

public class DuplicateSignupException extends RuntimeException {

    public DuplicateSignupException(String message) {
        super(message);
    }
}