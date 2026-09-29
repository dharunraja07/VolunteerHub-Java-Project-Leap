package NGO.VolunteerHub.Service;

import NGO.VolunteerHub.Exception.DuplicateSignupException;
import NGO.VolunteerHub.Exception.EventFullException;
import NGO.VolunteerHub.Exception.ResourceInUseException;
import NGO.VolunteerHub.Exception.ResourceNotFoundException;
import NGO.VolunteerHub.Model.Event;
import NGO.VolunteerHub.Model.SignUp;
import NGO.VolunteerHub.Model.Volunteer;
import NGO.VolunteerHub.Repository.AttendanceRepository;
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
    private final AttendanceRepository attendanceRepository;

    public SignUpService(
            SignUpRepository signUpRepository,
            EventRepository eventRepository,
            VolunteerRepository volunteerRepository,
            AttendanceRepository attendanceRepository) {

        this.signUpRepository = signUpRepository;
        this.eventRepository = eventRepository;
        this.volunteerRepository = volunteerRepository;
        this.attendanceRepository = attendanceRepository;
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
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(
                    "Event not found with ID: " + eventId);
        }
        return signUpRepository.findByEventId(eventId);
    }

    public List<SignUp> getSignupsByVolunteer(Long volunteerId) {

        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResourceNotFoundException(
                    "Volunteer not found with ID: " + volunteerId);
        }

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

        if (attendanceRepository.existsBySignupId(id)) {
            throw new ResourceInUseException(
                    "Cannot remove this signup because attendance "
                            + "has already been recorded for it");
        }

        signUpRepository.deleteById(id);
    }
}
