package NGO.VolunteerHub.Repository;
import NGO.VolunteerHub.Model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AttendanceRepository
        extends JpaRepository<AttendanceRecord, Long> {

    boolean existsBySignupId(Long signupId);
}