package tw.edu.fju.miniclinic.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByApptDate(LocalDate apptDate);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPatient(Patient patient);
    List<Appointment> findByDoctorAndApptDate(Doctor doctor, LocalDate apptDate);  // 新加入
    long countByApptDateBetween(LocalDate from, LocalDate to);
    
    // 新增：利用 Spring Data JPA 自動生成 countBy 查詢
    long countByStatus(String status);

    // 改用 Native Query 實作科別統計
    @Query(value = "SELECT d.department, COUNT(a.appt_id) FROM appointment a " +
                   "JOIN doctor d ON a.doctor_id = d.doctor_id " +
                   "GROUP BY d.department", nativeQuery = true)
    List<Object[]> countByDepartment();

    // 新增：依日期和醫師科別篩選掛號
    @Query("SELECT a FROM Appointment a WHERE a.apptDate = :date AND a.doctor.department = :dept")
    List<Appointment> findByDateAndDepartment(
        @Param("date") LocalDate date,
        @Param("dept") String department
    );
}
