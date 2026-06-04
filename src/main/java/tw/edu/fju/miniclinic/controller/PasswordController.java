package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PasswordForm;

@Controller
public class PasswordController {

    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/password")
    public String showPasswordForm(HttpSession session, Model model) {
        String doctorName = (String) session.getAttribute("loggedInDoctorName");
        model.addAttribute("loggedInDoctorName", doctorName);
        
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordForm());
        }
        return "password";
    }

    @PostMapping("/password")
    public String processPasswordChange(
            @Valid @ModelAttribute("passwordForm") PasswordForm form,
            BindingResult result,
            HttpSession session,
            Model model) {
            
        String doctorId = (String) session.getAttribute("loggedInDoctorId");
        String doctorName = (String) session.getAttribute("loggedInDoctorName");
        model.addAttribute("loggedInDoctorName", doctorName);
        
        if (result.hasErrors()) {
            return "password";
        }
        
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("errorMessage", "兩次密碼不相符");
            return "password";
        }
        
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null || !BCrypt.checkpw(form.getOldPassword(), doctor.getPasswordHash())) {
            model.addAttribute("errorMessage", "舊密碼錯誤");
            return "password";
        }
        
        // 更新密碼
        String newHash = BCrypt.hashpw(form.getNewPassword(), BCrypt.gensalt());
        doctor.setPasswordHash(newHash);
        doctorRepo.save(doctor);
        
        session.invalidate(); // 清除 Session 達成強制登出
        return "redirect:/login?pwdChanged"; // 重新導向至登入頁面，並帶上參數
    }
}