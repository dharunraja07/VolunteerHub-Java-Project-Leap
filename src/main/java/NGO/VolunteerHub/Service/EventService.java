package NGO.VolunteerHub.Service;
import NGO.VolunteerHub.Model.Event;
import NGO.VolunteerHub.Repository.EventRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class EventService {
    private final EventRepository eventRepository;
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;}

    // Creating the Event
    public Event createEvent(Event event) {
        return eventRepository.save(event);}

    // Get all the Events
    public List<Event> getAllEvents() {
        return eventRepository.findAll();}

    // Getting Event By ID
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);}

    // UPDATE
    public Event updateEvent(Long id, Event eventDetails) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found with ID: " + id)
                );
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setEventDate(eventDetails.getEventDate());
        event.setLocation(eventDetails.getLocation());
        event.setCapacity(eventDetails.getCapacity());
        return eventRepository.save(event);
    }

    // DELETE
    public void deleteEvent(Long id) {

        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found with ID: " + id);
        }

        eventRepository.deleteById(id);
    }
}