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
@Table(name = "test_suite_run_log_tb")
public class TestSuiteRunLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "test_suite_id", nullable = false)
    private int testSuiteId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "start_time", columnDefinition = "TEXT")
    private LocalDateTime startTime;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "end_time", columnDefinition = "TEXT")
    private LocalDateTime endTime;

    private long duration;

    @CreationTimestamp
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "created_at", updatable = false, columnDefinition = "TEXT")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "updated_at", columnDefinition = "TEXT")
    private LocalDateTime updatedAt;
}