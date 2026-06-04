package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PasswordForm;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AuthApiController {

    @Autowired
    private DoctorRepository doctorRepo;

    @PutMapping("/api/auth/password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody PasswordForm form,
            BindingResult result,
            HttpSession session) {

        String doctorId = (String) session.getAttribute("loggedInDoctorId");
        if (doctorId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "使用者未登入"));
        }

        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "兩次密碼不相符"));
        }

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null || !BCrypt.checkpw(form.getOldPassword(), doctor.getPasswordHash())) {
            return ResponseEntity.badRequest().body(Map.of("error", "舊密碼錯誤"));
        }

        doctor.setPasswordHash(BCrypt.hashpw(form.getNewPassword(), BCrypt.gensalt()));
        doctorRepo.save(doctor);
        session.invalidate();

        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
