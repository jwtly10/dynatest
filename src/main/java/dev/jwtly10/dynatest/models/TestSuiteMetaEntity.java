package dev.jwtly10.dynatest.models;

import dev.jwtly10.dynatest.converter.LocalDateTimeAttributeConverter;
import dev.jwtly10.dynatest.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "test_suite_meta_tb")
public class TestSuiteMetaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "test_suite_id", unique = true, nullable = false)
    private int testSuiteId;

    private int runs;
    private int passCount;
    private int failCount;

    @Enumerated(EnumType.STRING)
    private Status lastOutcome;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "last_finished_run_at", columnDefinition = "TEXT")
    private LocalDateTime lastFinishedRunAt;

    @CreationTimestamp
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "created_at", updatable = false, columnDefinition = "TEXT")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "updated_at", columnDefinition = "TEXT")
    private LocalDateTime updatedAt;

}