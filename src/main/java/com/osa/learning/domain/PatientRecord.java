package com.osa.learning.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 *
 * @author oleksii
 * @since 29 Nov 2022
 */
@Entity
@Table(name = "patient_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @NonNull
    private String name;
    
    @NonNull
    private Integer age;
    
    @NonNull
    private String address;
}
