package com.pm.patient_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.exception.EmailAlreadyExistsException;
import com.pm.patient_service.exception.PatientNotFoundException;
import com.pm.patient_service.grpc.BillingServiceGrpcClient;
import com.pm.patient_service.kafka.KafkaProducer;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;
    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer){
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> patientResponseDTOs = patients.stream()
        .map(PatientMapper::toDto).toList();

        
        return patientResponseDTOs;
    }

    public PatientResponseDTO addPatient(PatientRequestDTO patientRequestDTO){

        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A patient with this email"+patientRequestDTO.getEmail()+" already exists");
        }

        Patient patient = PatientMapper.DTOtoModelPatient(patientRequestDTO);
        Patient newPatient = patientRepository.save(patient);

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(), newPatient.getName(), newPatient.getEmail());

        kafkaProducer.sendEvent(patient);
        return PatientMapper.toDto(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        
     Patient patient = patientRepository.findById(id).orElseThrow(()-> new PatientNotFoundException("Patient not found for id: "+id));

     if( patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)){
            throw new EmailAlreadyExistsException("A patient with this email"+patientRequestDTO.getEmail()+" already exists");
     }

     patient.setName(patientRequestDTO.getName() == null ? patient.getName() :patientRequestDTO.getName());
     patient.setEmail(patientRequestDTO.getEmail() == null ? patient.getEmail() : patientRequestDTO.getEmail());
     patient.setAddress(patientRequestDTO.getAddress() == null ? patient.getAddress() : patientRequestDTO.getAddress());
     patient.setDateOfBirth( patientRequestDTO.getDateOfBirth() == null ? patient.getDateOfBirth() : LocalDate.parse(patientRequestDTO.getDateOfBirth()));
     patient.setRegisteredDate(patientRequestDTO.getRegisteredDate() == null ? patient.getRegisteredDate() : LocalDate.parse(patientRequestDTO.getRegisteredDate()));
     
     Patient updatedPatient = patientRepository.save(patient);
     return PatientMapper.toDto(updatedPatient);
    }

    public PatientResponseDTO deletePatient(UUID id){
             Patient patient =   patientRepository.findById(id).orElseThrow(()-> new PatientNotFoundException("Patient not found for id: "+id));

             patientRepository.deleteById(id);

             return PatientMapper.toDto(patient);

    }
}
