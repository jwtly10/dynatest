package dev.jwtly10.dynatest.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class TestSuiteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Lob
    private String configuration;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}