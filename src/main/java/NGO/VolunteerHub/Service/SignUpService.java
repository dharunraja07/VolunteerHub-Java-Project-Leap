package NGO.VolunteerHub.Service;
import NGO.VolunteerHub.Exception.DuplicateSignupException;
import NGO.VolunteerHub.Exception.EventFullException;
import NGO.VolunteerHub.Exception.ResourceNotFoundException;
import NGO.VolunteerHub.Model.Event;
import NGO.VolunteerHub.Model.SignUp;
import NGO.VolunteerHub.Model.Volunteer;
import NGO.VolunteerHub.Repository.EventRepository;
import NGO.VolunteerHub.Repository.SignUpRepository;
import NGO.VolunteerHub.Repository.VolunteerRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class SignUpService {
    private final SignUpRepository signUpRepository;
    private final EventRepository eventRepository;
    private final VolunteerRepository volunteerRepository;

    public SignUpService(
            SignUpRepository signUpRepository,
            EventRepository eventRepository,
            VolunteerRepository volunteerRepository) {
        this.signUpRepository = signUpRepository;
        this.eventRepository = eventRepository;
        this.volunteerRepository = volunteerRepository;
    }

    public SignUp createSignup(Long eventId, Long volunteerId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with ID: " + eventId));

        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Volunteer not found with ID: " + volunteerId));

        if (signUpRepository.existsByEventIdAndVolunteerId(
                eventId, volunteerId)) {

            throw new DuplicateSignupException(
                    "Volunteer is already signed up for this event");
        }

        long signupCount = signUpRepository.countByEventId(eventId);

        if (signupCount >= event.getCapacity()) {

            throw new EventFullException(
                    "Event is full. No more volunteers can sign up");
        }

        SignUp signup = new SignUp();

        signup.setEvent(event);
        signup.setVolunteer(volunteer);
        signup.setSignupDate(LocalDate.now());

        return signUpRepository.save(signup);
    }

    public List<SignUp> getAllSignups() {
        return signUpRepository.findAll();
    }

    public List<SignUp> getSignupsByEvent(Long eventId) {

        return signUpRepository.findAll()
                .stream()
                .filter(signup ->
                        signup.getEvent().getId().equals(eventId))
                .toList();
    }

    public List<SignUp> getSignupsByVolunteer(Long volunteerId) {

        return signUpRepository.findAll()
                .stream()
                .filter(signup ->
                        signup.getVolunteer().getId().equals(volunteerId))
                .toList();
    }

    public void deleteSignup(Long id) {

        if (!signUpRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Signup not found with ID: " + id);
        }

        signUpRepository.deleteById(id);
    }
}