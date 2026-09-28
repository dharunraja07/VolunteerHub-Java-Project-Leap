package NGO.VolunteerHub.Repository;

import NGO.VolunteerHub.Model.SignUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignUpRepository extends JpaRepository<SignUp, Long> {

    boolean existsByEventIdAndVolunteerId(Long eventId, Long volunteerId);

    long countByEventId(Long eventId);
}