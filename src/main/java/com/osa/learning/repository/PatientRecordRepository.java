package com.osa.learning.repository;

import com.osa.learning.domain.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author oleksii
 * @since 29 Nov 2022
 */
@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {

}
