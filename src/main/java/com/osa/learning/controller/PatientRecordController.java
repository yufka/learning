package com.osa.learning.controller;

import com.osa.learning.domain.PatientRecord;
import com.osa.learning.queue.JmsProducer;
import com.osa.learning.repository.PatientRecordRepository;
import com.osa.learning.util.InvalidRequestException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author oleksii
 * @since 29 Nov 2022
 */
@RestController
@RequestMapping(value = "/patient")
public class PatientRecordController {

    @Autowired
    private PatientRecordRepository patientRecordRepository;
    
    @Autowired
    private JmsProducer jmsProducer;

    @GetMapping
    public List<PatientRecord> getAllPatientRecords() {
        PatientRecord record = PatientRecord.builder().id(1).age(33).address("Runzstr").name("Oleksii").build();
        jmsProducer.sendMessage(record);
        return patientRecordRepository.findAll();
    }

    @GetMapping(value = "{id}")
    public PatientRecord getPatientRecordById(@PathVariable(value = "id") Long id) {
        return patientRecordRepository.findById(id).get();
    }

    @PostMapping
    public PatientRecord createRecord(@RequestBody PatientRecord patientRecord) {
        return patientRecordRepository.save(patientRecord);
    }

    @PutMapping
    public PatientRecord updatePatientRecord(@RequestBody PatientRecord patientRecord) {
        if (patientRecord == null || patientRecord.getId() == 0) {
            throw new InvalidRequestException("PatientRecord or ID must not be null!");
        }
        Optional<PatientRecord> optionalRecord = patientRecordRepository.findById(patientRecord.getId());
        if (optionalRecord.isEmpty()) {
            throw new InvalidRequestException("Patient with ID " + patientRecord.getId() + " does not exist.");
        }
        PatientRecord existingPatientRecord = optionalRecord.get();

        existingPatientRecord.setName(patientRecord.getName());
        existingPatientRecord.setAge(patientRecord.getAge());
        existingPatientRecord.setAddress(patientRecord.getAddress());

        return patientRecordRepository.save(existingPatientRecord);
    }

    @DeleteMapping(value = "{patientId}")
    public void deletePatientById(@PathVariable(value = "patientId") Long patientId) {
        if (patientRecordRepository.findById(patientId).isEmpty()) {
            throw new InvalidRequestException("Patient with ID " + patientId + " does not exist.");
        }
        patientRecordRepository.deleteById(patientId);
    }
}
