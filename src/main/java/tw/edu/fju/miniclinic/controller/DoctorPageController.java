package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import tw.edu.fju.miniclinic.model.DoctorRepository;

@Controller
public class DoctorPageController {

    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        model.addAttribute("doctors", doctorRepo.findAll());
        return "doctors";
    }

    @GetMapping("/doctors/{doctorId}")
    public String showDoctor(@PathVariable String doctorId, Model model) {
        model.addAttribute("doctor", doctorRepo.findById(doctorId).orElse(null));
        return "doctor-detail";
    }
}
