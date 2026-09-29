package NGO.VolunteerHub.Service;

import NGO.VolunteerHub.Exception.ResourceInUseException;
import NGO.VolunteerHub.Exception.ResourceNotFoundException;
import NGO.VolunteerHub.Model.Volunteer;
import NGO.VolunteerHub.Repository.SignUpRepository;
import NGO.VolunteerHub.Repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final SignUpRepository signUpRepository;

    public VolunteerService(
            VolunteerRepository volunteerRepository,
            SignUpRepository signUpRepository) {

        this.volunteerRepository = volunteerRepository;
        this.signUpRepository = signUpRepository;
    }

    public Volunteer createVolunteer(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

    public Optional<Volunteer> getVolunteerById(Long id) {
        return volunteerRepository.findById(id);
    }

    public Volunteer updateVolunteer(
            Long id,
            Volunteer volunteerDetails) {

        Volunteer volunteer = volunteerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Volunteer not found with ID: " + id));

        volunteer.setName(volunteerDetails.getName());
        volunteer.setEmail(volunteerDetails.getEmail());
        volunteer.setPhone(volunteerDetails.getPhone());

        return volunteerRepository.save(volunteer);
    }

    public void deleteVolunteer(Long id) {

        if (!volunteerRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Volunteer not found with ID: " + id);
        }

        long signupCount = signUpRepository.countByVolunteerId(id);

        if (signupCount > 0) {
            throw new ResourceInUseException(
                    "Cannot delete this volunteer because "
                            + signupCount
                            + " signup(s) are linked to this volunteer");
        }

        volunteerRepository.deleteById(id);
    }
}
