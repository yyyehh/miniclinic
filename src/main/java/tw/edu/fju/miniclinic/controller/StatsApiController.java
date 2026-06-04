package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.util.LinkedHashMap;
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
        // 使用 LinkedHashMap 確保 JSON 欄位順序與規格範例一致
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalDoctors", (int) doctorRepo.count());
        result.put("totalPatients", (int) patientRepo.count());
        result.put("totalAppointments", (int) appointmentRepo.count());

        Map<String, Integer> byStatus = new LinkedHashMap<>();
        // 強制轉型為 int 以符合規格要求，並使用 countByStatus 進行統計
        byStatus.put("BOOKED", (int) appointmentRepo.countByStatus("BOOKED"));
        byStatus.put("COMPLETED", (int) appointmentRepo.countByStatus("COMPLETED"));
        byStatus.put("CANCELLED", (int) appointmentRepo.countByStatus("CANCELLED"));

        result.put("byStatus", byStatus);
        return result;
    }
}
