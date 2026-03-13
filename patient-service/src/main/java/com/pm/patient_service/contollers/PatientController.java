package com.pm.patient_service.contollers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.patient_service.dto.ApiResponse;
import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.dto.Validators.CreatePatientValidationGroup;
import com.pm.patient_service.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;

@RestController
@RequestMapping("/patients")
@Tag(name="Patient", description = "API design for patients service")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService){
        this.patientService=patientService;
    }

    @GetMapping
    @Operation(summary="Get all patients details")
    public ResponseEntity<Object> getAllPatinets(){
        List<PatientResponseDTO> patientResponseDTO = patientService.getPatients();

        return ResponseEntity.ok().body(ApiResponse.success(patientResponseDTO, "Fetched all patients Successfully"));
    }

    @PostMapping
    @Operation(summary = "Adding new patient")
    public ResponseEntity<Object> addPatient(@Validated({Default.class,CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patient){

        PatientResponseDTO savedPatient = patientService.addPatient(patient);

        return ResponseEntity.ok().body(ApiResponse.success(savedPatient,"Addedd new Patient Successfully"));
    }

    @PutMapping("/{patientId}")
    @Operation(summary = "Update patient")
    public ResponseEntity<Object> updatePatient(@PathVariable UUID patientId, @Validated({Default.class}) @RequestBody PatientRequestDTO patient){
        PatientResponseDTO updatedPatient = patientService.updatePatient(patientId, patient);
        return ResponseEntity.ok().body(ApiResponse.success(updatedPatient, "Updated patient successfully"));
    }

    @DeleteMapping("/{patientId}")
    @Operation(summary = "Delete patient")
    public ResponseEntity<Object> deletePatient(@PathVariable UUID patientId){
        PatientResponseDTO deletedPatient = patientService.deletePatient(patientId);
        return ResponseEntity.ok().body(ApiResponse.success(deletedPatient, "Deleted patient successfully"));
    }
}
