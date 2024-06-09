package dev.jwtly10.dynatest.services;

import dev.jwtly10.dynatest.enums.Status;
import dev.jwtly10.dynatest.models.TestSuiteMetaEntity;
import dev.jwtly10.dynatest.respository.TestSuiteMetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class TestSuiteMetaService {
    private final TestSuiteMetaRepository testSuiteMetaRepository;

    public TestSuiteMetaService(TestSuiteMetaRepository testSuiteMetaRepository) {
        this.testSuiteMetaRepository = testSuiteMetaRepository;
    }

    public Optional<TestSuiteMetaEntity> getMetaDataForTestSuiteRun(int testSuiteId) {
        return testSuiteMetaRepository.findByTestSuiteId(testSuiteId);
    }

    public TestSuiteMetaEntity initMetaData(int testSuiteId) {
        TestSuiteMetaEntity testSuiteMetaEntity = testSuiteMetaRepository.findByTestSuiteId(testSuiteId).orElse(null);
        if (testSuiteMetaEntity == null) {
            // If a row doesn't exist, init one with defaults
            testSuiteMetaEntity = new TestSuiteMetaEntity();
            testSuiteMetaEntity.setTestSuiteId(testSuiteId);
            testSuiteMetaEntity.setRuns(0);
            testSuiteMetaEntity.setPassCount(0);
            testSuiteMetaEntity.setFailCount(0);
            testSuiteMetaEntity.setLastRunLog("");
            testSuiteMetaEntity.setCreatedAt(LocalDateTime.now());
            testSuiteMetaEntity.setUpdatedAt(LocalDateTime.now());
        } else {
            throw new RuntimeException("Cant init a meta row when one already exists");
        }

        log.info("Init meta data");
        return testSuiteMetaRepository.save(testSuiteMetaEntity);
    }

    public TestSuiteMetaEntity saveMetaDataForTestSuiteRun(int testSuiteId, Status result, String lastRunLog) {
        // Either update existing row or create new one
        TestSuiteMetaEntity testSuiteMetaEntity = testSuiteMetaRepository.findByTestSuiteId(testSuiteId).orElse(null);
        if (testSuiteMetaEntity == null) {
            // If a row doesn't exist, init one with defaults
            testSuiteMetaEntity = new TestSuiteMetaEntity();
            testSuiteMetaEntity.setTestSuiteId(testSuiteId);
            testSuiteMetaEntity.setRuns(0);
            testSuiteMetaEntity.setPassCount(0);
            testSuiteMetaEntity.setFailCount(0);
            testSuiteMetaEntity.setCreatedAt(LocalDateTime.now());
        }

        int runs = testSuiteMetaEntity.getRuns();
        testSuiteMetaEntity.setRuns(runs + 1);
        switch (result) {
            case SUCCESS:
                int passCount = testSuiteMetaEntity.getPassCount();
                testSuiteMetaEntity.setPassCount(passCount + 1);
                testSuiteMetaEntity.setLastOutcome(Status.SUCCESS);
                break;
            case FAIL:
                int failCount = testSuiteMetaEntity.getFailCount();
                testSuiteMetaEntity.setFailCount(failCount + 1);
                testSuiteMetaEntity.setLastOutcome(Status.FAIL);
                break;
            default:
                throw new RuntimeException("Invalid status used for the outcome result!!! Used: " + result);
        }

        testSuiteMetaEntity.setLastFinishedRunAt(LocalDateTime.now());
        testSuiteMetaEntity.setLastRunLog(lastRunLog);
        testSuiteMetaEntity.setUpdatedAt(LocalDateTime.now());
        log.info("Saving meta run log for test suite: " + testSuiteMetaEntity);
        return testSuiteMetaRepository.save(testSuiteMetaEntity);
    }
}