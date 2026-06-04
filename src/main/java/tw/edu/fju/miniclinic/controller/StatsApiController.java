package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // 新增：健康檢查端點，供外部工具查核系統狀態
    @GetMapping(value = "/api/health", produces = "application/json;charset=UTF-8")
    public Map<String, String> getHealth() {
        return Map.of("status", "OK");
    }

    @GetMapping(value = "/api/stats", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getStats() {
        // 準備狀態統計 (byStatus)
        Map<String, Object> byStatus = new LinkedHashMap<>();
        byStatus.put("BOOKED", (int) appointmentRepo.countByStatus("BOOKED"));
        byStatus.put("COMPLETED", (int) appointmentRepo.countByStatus("COMPLETED"));
        byStatus.put("CANCELLED", (int) appointmentRepo.countByStatus("CANCELLED"));

        // 準備總體統計 (result)
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalDoctors", (int) doctorRepo.count());
        result.put("totalPatients", (int) patientRepo.count());
        result.put("totalAppointments", (int) appointmentRepo.count());
        result.put("byStatus", byStatus);

        return ResponseEntity.ok(result);

    }
}
