package com.gestiondossier.api.patient;

import com.gestiondossier.api.patient.models.entity.Patient;
import com.gestiondossier.api.patient.models.error.PatientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public List<Patient> findAll() {
        return patientService.findAll();
    }

    @GetMapping("/{id}")
    public Patient findById(@PathVariable("id") Integer patientId) {
        return Optional.ofNullable(patientService.findById(patientId)).orElseThrow(PatientNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable("id") Integer id, @RequestBody Patient patient) {
        return Optional.ofNullable(patientService.updatePatient(id, patient)).orElseThrow(PatientNotFoundException::new);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable("id") Integer patientId) {
        patientService.deletePatient(patientId);
    }

}
