package NGO.VolunteerHub.Service;

import NGO.VolunteerHub.Exception.EventFullException;
import NGO.VolunteerHub.Exception.ResourceInUseException;
import NGO.VolunteerHub.Exception.ResourceNotFoundException;
import NGO.VolunteerHub.Model.Event;
import NGO.VolunteerHub.Repository.EventRepository;
import NGO.VolunteerHub.Repository.SignUpRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final SignUpRepository signUpRepository;

    public EventService(
            EventRepository eventRepository,
            SignUpRepository signUpRepository) {

        this.eventRepository = eventRepository;
        this.signUpRepository = signUpRepository;
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event updateEvent(Long id, Event eventDetails) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with ID: " + id));

        long currentSignups = signUpRepository.countByEventId(id);

        if (eventDetails.getCapacity() < currentSignups) {
            throw new EventFullException(
                    "Capacity cannot be less than the current signup count of "
                            + currentSignups);
        }

        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setEventDate(eventDetails.getEventDate());
        event.setLocation(eventDetails.getLocation());
        event.setCapacity(eventDetails.getCapacity());

        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {

        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Event not found with ID: " + id);
        }

        long signupCount = signUpRepository.countByEventId(id);

        if (signupCount > 0) {
            throw new ResourceInUseException(
                    "Cannot delete this event because "
                            + signupCount
                            + " volunteer signup(s) are linked to it");
        }

        eventRepository.deleteById(id);
    }
}
