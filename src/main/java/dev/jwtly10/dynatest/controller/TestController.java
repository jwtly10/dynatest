package dev.jwtly10.dynatest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class TestController {

    @PostMapping("/runtest")
    public ResponseEntity<?> runTest(@RequestBody Map<String, Object> payload) {
        log.info(payload.toString());
        try {
            Thread.sleep(3000); // Simulate delay
            String jsonData = (String) payload.get("jsonData");  // Extract JSON string from the payload
            // Perform any necessary logic with jsonData here
            return ResponseEntity.ok(new ResponseMessage("Test completed successfully."));
        } catch (InterruptedException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Error during processing."));
        }
    }

    private static class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}