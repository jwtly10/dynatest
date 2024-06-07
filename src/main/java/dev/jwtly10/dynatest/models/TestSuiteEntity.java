package dev.jwtly10.dynatest.models;

import dev.jwtly10.dynatest.converter.LocalDateTimeAttributeConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "test_suite_tb")
public class TestSuiteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Lob
    private String configuration;

    @CreationTimestamp
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "created_at", updatable = false, columnDefinition = "TEXT")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "updated_at", columnDefinition = "TEXT")
    private LocalDateTime updatedAt;
}