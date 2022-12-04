package com.osa.learning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osa.learning.domain.PatientRecord;
import com.osa.learning.repository.PatientRecordRepository;
import com.osa.learning.util.InvalidRequestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author oleksii
 * @since 29 Nov 2022
 */
@WebMvcTest(PatientRecordController.class)
public class PatientRecordControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    PatientRecordRepository patientRecordRepository;

    PatientRecord RECORD_1 = new PatientRecord(1l, "Rayven Yor", 23, "Cebu Philippines");
    PatientRecord RECORD_2 = new PatientRecord(2l, "David Landup", 27, "New York USA");
    PatientRecord RECORD_3 = new PatientRecord(3l, "Jane Doe", 31, "New York USA");

    @Test
    public void getAllRecords_success() throws Exception {
        List<PatientRecord> records = new ArrayList<>(Arrays.asList(RECORD_1, RECORD_2, RECORD_3));

        Mockito.when(patientRecordRepository.findAll()).thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/patient")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].name", is(RECORD_3.getName())));
    }

    @Test
    public void getPatientById_success() throws Exception {
        Mockito.when(patientRecordRepository.findById(RECORD_1.getId())).thenReturn(java.util.Optional.of(RECORD_1));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/patient/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is(RECORD_1.getName())));
    }
    
    @Test
    public void createRecord_success() throws Exception {
        Mockito.when(patientRecordRepository.save(RECORD_1)).thenReturn(RECORD_1);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(RECORD_1));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is(RECORD_1.getName())));
    }

    @Test
    public void updatePatientRecord_success() throws Exception {
        Mockito.when(patientRecordRepository.findById(RECORD_1.getId())).thenReturn(Optional.of(RECORD_1));
        Mockito.when(patientRecordRepository.save(RECORD_1)).thenReturn(RECORD_1);

        MockHttpServletRequestBuilder mockRequest = getMockedRequestBuilderWithPutMethod(RECORD_1);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is(RECORD_1.getName())));
    }

    @Test
    public void updatePatientRecord_nullId() throws Exception {
        PatientRecord updatedRecord = PatientRecord.builder()
                .name("Sherlock Holmes")
                .age(40)
                .address("221B Baker Street")
                .build();

        MockHttpServletRequestBuilder mockRequest = getMockedRequestBuilderWithPutMethod(updatedRecord);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result
                        -> assertTrue(result.getResolvedException() instanceof InvalidRequestException))
                .andExpect(result
                        -> assertEquals("PatientRecord or ID must not be null!", result.getResolvedException().getMessage()));
    }

    @Test
    public void updatePatientRecord_recordNotFound() throws Exception {
        Mockito.when(patientRecordRepository.findById(RECORD_1.getId())).thenReturn(Optional.ofNullable(null));
        MockHttpServletRequestBuilder mockRequest = getMockedRequestBuilderWithPutMethod(RECORD_1);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result
                        -> assertTrue(result.getResolvedException() instanceof InvalidRequestException))
                .andExpect(result
                        -> assertEquals("Patient with ID " + RECORD_1.getId() + " does not exist.",
                                result.getResolvedException().getMessage()));
    }
    
    MockHttpServletRequestBuilder getMockedRequestBuilderWithPutMethod(PatientRecord record) throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(record));
        return mockRequest;
    }

    @Test
    public void deletePatientById_success() throws Exception {
        Mockito.when(patientRecordRepository.findById(RECORD_2.getId())).thenReturn(Optional.of(RECORD_2));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/patient/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deletePatientById_notFound() throws Exception {
        Mockito.when(patientRecordRepository.findById(5l)).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/patient/5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result
                        -> assertTrue(result.getResolvedException() instanceof InvalidRequestException))
                .andExpect(result
                        -> assertEquals("Patient with ID 5 does not exist.", result.getResolvedException().getMessage()));
    }
}
