package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import tw.edu.fju.miniclinic.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class AppointmentApiController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/api/appointments/count")
    public Map<String, Long> getAppointmentCount() {
        return Map.of("count", appointmentRepo.count()); // 會回傳 {"count": X}
    }

    @GetMapping("/api/appointments")
    public List<Appointment> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {
        
        // 情境 1：只傳日期 (date != null)
        if (date != null && !date.isBlank()) {
            return appointmentRepo.findByApptDate(LocalDate.parse(date));
        }
        
        // 情境 2：只傳醫師 ID (doctorId != null)
        if (doctorId != null && !doctorId.isBlank()) {
            return doctorRepo.findById(doctorId)
                    .map(doctor -> appointmentRepo.findByDoctor(doctor))
                    .orElse(List.of());
        }
        
        // 情境 3：都不傳，回傳全部掛號
        return appointmentRepo.findAll();
    }

    @PutMapping("/api/appointments/{apptId}/status")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long apptId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");

        Appointment appt = appointmentRepo.findById(apptId).orElse(null);
        if (appt == null) {
            return ResponseEntity.notFound().build();
        }

        // 只能修改自己的掛號
        if (!appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) {
            return ResponseEntity.status(403).build();
        }

        String newStatus = payload.get("status");
        if (!List.of("BOOKED", "COMPLETED", "CANCELLED").contains(newStatus)) {
            return ResponseEntity.badRequest().build();
        }

        appt.setStatus(newStatus);
        return ResponseEntity.ok(appointmentRepo.save(appt));
    }
}
