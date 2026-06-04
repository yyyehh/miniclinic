package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StatsApiController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/api/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalDoctors", doctorRepo.count());
        result.put("totalPatients", patientRepo.count());

        List<Appointment> allAppts = appointmentRepo.findAll();
        result.put("totalAppointments", (long) allAppts.size());

        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("BOOKED", allAppts.stream().filter(a -> "BOOKED".equalsIgnoreCase(a.getStatus())).count());
        byStatus.put("COMPLETED", allAppts.stream().filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus())).count());
        byStatus.put("CANCELLED", allAppts.stream().filter(a -> "CANCELLED".equalsIgnoreCase(a.getStatus())).count());

        result.put("byStatus", byStatus);
        return result;
    }
}
