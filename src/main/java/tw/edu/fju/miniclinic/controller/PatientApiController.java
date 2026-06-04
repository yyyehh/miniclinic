package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;
import java.util.List;

@RestController
public class PatientApiController {

    @Autowired
    private PatientRepository patientRepo;

    @GetMapping(value = "/api/patients", produces = "application/json;charset=UTF-8")
    public List<Patient> getPatients() {
        return patientRepo.findAll();
    }

    @GetMapping(value = "/api/patients/{chartNo}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Patient> getPatient(@PathVariable String chartNo) {
        return patientRepo.findById(chartNo)
            .map(patient -> ResponseEntity.ok(patient))          // 找到病患，回傳 200 OK
            .orElse(ResponseEntity.notFound().build());         // 找不到，回傳 404 Not Found
    }
}
