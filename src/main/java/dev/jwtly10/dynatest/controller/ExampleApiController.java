package dev.jwtly10.dynatest.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/demo")
public class ExampleApiController {

    private final Map<Integer, Profile> profileStorage = new HashMap<>();
    private final Map<Integer, Survey> surveyStorage = new HashMap<>();
    private final AtomicInteger profileIdCounter = new AtomicInteger(1);
    private final AtomicInteger surveyIdCounter = new AtomicInteger(1);
    private final String apiKey = "71b80106-be79-41a9-bc79-ed0b1ecfe0b7";

    /**
     * This controller is an example demo server for testing and demoing the dynatest test framework
     * It has a few endpoints to simulate a real application:
     * 1. /create-member - POST - Create a new member, with profile data, returns the user's id
     * 2. /get-member - GET - Get a member profile by id
     * 3. /create-survey - POST - Create a new survey submission against a user, returns the survey id
     * 4. /get-survey - GET - Get a survey submission by id
     */

    @PostMapping("/create-member")
    public ResponseEntity<Map<String, Integer>> createMember(
            @RequestHeader("X-API-Key") String apiKeyHeader,
            @RequestBody Profile profile) {
        if (!authenticate(apiKeyHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        System.out.println(profile);
        int id = profileIdCounter.getAndIncrement();
        profile.setId(id);
        profileStorage.put(id, profile);
        Map<String, Integer> response = new HashMap<>();
        response.put("userId", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-member")
    public ResponseEntity<Profile> getMember(
            @RequestHeader("X-API-Key") String apiKeyHeader,
            @RequestParam int id) {
        if (!authenticate(apiKeyHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Profile profile = profileStorage.get(id);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create-survey")
    public ResponseEntity<Map<String, Integer>> createSurvey(
            @RequestHeader("X-API-Key") String apiKeyHeader,
            @RequestBody Survey survey) {
        if (!authenticate(apiKeyHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        int id = surveyIdCounter.getAndIncrement();
        survey.setId(id);
        surveyStorage.put(id, survey);
        Map<String, Integer> response = new HashMap<>();
        response.put("surveyId", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-survey")
    public ResponseEntity<Survey> getSurvey(
            @RequestHeader("X-API-Key") String apiKeyHeader,
            @RequestParam int id) {
        if (!authenticate(apiKeyHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Survey survey = surveyStorage.get(id);
        if (survey != null) {
            return ResponseEntity.ok(survey);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean authenticate(String apiKeyHeader) {
        return apiKey.equals(apiKeyHeader);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Profile {
        private int id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private LocalDate dob;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Survey {
        private int id;
        private int profileId;
        private String surveyData;
    }
}