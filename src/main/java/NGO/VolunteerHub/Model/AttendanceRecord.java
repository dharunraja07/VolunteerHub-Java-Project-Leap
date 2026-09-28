package NGO.VolunteerHub.Model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(
        name = "attendance_records",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"signup_id"}
        )
)
@Data
@NoArgsConstructor
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "signup_id", nullable = false)
    private SignUp signup;

    private boolean attended;

    private double hours;
}