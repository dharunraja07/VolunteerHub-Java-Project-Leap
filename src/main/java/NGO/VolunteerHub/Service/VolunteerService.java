package NGO.VolunteerHub.Service;
import NGO.VolunteerHub.Model.Volunteer;
import NGO.VolunteerHub.Repository.VolunteerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class VolunteerService {
    private final VolunteerRepository volunteerRepository;
    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;}

    // CREATE
    public Volunteer createVolunteer(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);}

    // GET ALL
    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();}

    // GET BY ID
    public Optional<Volunteer> getVolunteerById(Long id) {
        return volunteerRepository.findById(id);}

    // UPDATE
    public Volunteer updateVolunteer(Long id, Volunteer volunteerDetails) {
        Volunteer volunteer = volunteerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Volunteer not found with ID: " + id
                        )
                );

        volunteer.setName(volunteerDetails.getName());
        volunteer.setEmail(volunteerDetails.getEmail());
        volunteer.setPhone(volunteerDetails.getPhone());
        return volunteerRepository.save(volunteer);}

    // DELETE
    public void deleteVolunteer(Long id) {
        if (!volunteerRepository.existsById(id)) {
            throw new RuntimeException(
                    "Volunteer not found with ID: " + id
            );
        }

        volunteerRepository.deleteById(id);
    }
}