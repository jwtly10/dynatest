package dev.jwtly10.dynatest.services;

import dev.jwtly10.dynatest.enums.Status;
import dev.jwtly10.dynatest.models.TestSuiteRunLogEntity;
import dev.jwtly10.dynatest.models.TestSuiteRunLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TestSuiteRunLogService {
    private final TestSuiteRunLogRepository testSuiteRunLogRepository;

    public TestSuiteRunLogService(TestSuiteRunLogRepository testSuiteRunLogRepository) {
        this.testSuiteRunLogRepository = testSuiteRunLogRepository;
    }

    public TestSuiteRunLogEntity startRunForTestSuite(int testSuiteId) {
        TestSuiteRunLogEntity entity = new TestSuiteRunLogEntity();
        entity.setTestSuiteId(testSuiteId);
        entity.setStatus(Status.RUNNING);
        entity.setStartTime(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        log.info("Saving new test run log" + entity.toString());
        return testSuiteRunLogRepository.save(entity);
    }

    public TestSuiteRunLogEntity finishRun(int runLogId) {
        TestSuiteRunLogEntity entity = testSuiteRunLogRepository.findById(runLogId).orElse(null);
        if (entity == null) throw new RuntimeException("Cant finish a run that doesnt exist");

        entity.setStatus(Status.SUCCESS);

        entity.setEndTime(LocalDateTime.now());
        Duration duration = Duration.between(entity.getStartTime(), entity.getEndTime());
        entity.setDuration(duration.getSeconds());
        entity.setUpdatedAt(LocalDateTime.now());
        log.info("Saving finished test run log" + entity.toString());
        return testSuiteRunLogRepository.save(entity);
    }

    public TestSuiteRunLogEntity failRun(int runLogId, String errorMessage) {
        TestSuiteRunLogEntity entity = testSuiteRunLogRepository.findById(runLogId).orElse(null);
        if (entity == null) throw new RuntimeException("Cant fail a run that doesnt exist");

        entity.setStatus(Status.FAIL);

        entity.setEndTime(LocalDateTime.now());
        Duration duration = Duration.between(entity.getStartTime(), entity.getEndTime());
        entity.setDuration(duration.getSeconds());
        entity.setErrorMessage(errorMessage);
        entity.setUpdatedAt(LocalDateTime.now());
        log.info("Saving failed test run log" + entity.toString());
        return testSuiteRunLogRepository.save(entity);
    }

    public List<TestSuiteRunLogEntity> getRunLogsForTestSuite(int testSuiteId) {
        return testSuiteRunLogRepository.findAllByTestSuiteId(testSuiteId);
    }

    public Optional<TestSuiteRunLogEntity> getLastRunLogForTestSuite(int testSuiteId) {
        return testSuiteRunLogRepository.findLastLogRunForTestSuite(testSuiteId);
    }
}