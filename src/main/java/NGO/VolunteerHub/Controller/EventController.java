package NGO.VolunteerHub.Controller;
import NGO.VolunteerHub.Model.Event;
import NGO.VolunteerHub.Service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
@CrossOrigin
public class EventController {
    private final EventService eventService;
    public EventController(EventService eventService) {
        this.eventService = eventService;}

    // CREATE EVENT
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {

        Event savedEvent = eventService.createEvent(event);

        return ResponseEntity.ok(savedEvent);}

    // GET ALL EVENTS
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {

        return ResponseEntity.ok(
                eventService.getAllEvents()
        );}

    // GET EVENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {

        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());}

    // UPDATE EVENT
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody Event event) {
        Event updatedEvent =
                eventService.updateEvent(id, event);

        return ResponseEntity.ok(updatedEvent);
    }

    // DELETE EVENT
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(
            @PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(
                "Event deleted successfully"
        );}
}