package dev.jwtly10.dynatest.services;

import dev.jwtly10.dynatest.enums.State;
import dev.jwtly10.dynatest.models.TestSuiteEntity;
import dev.jwtly10.dynatest.respository.TestSuiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TestSuiteService {
    private final TestSuiteRepository testSuiteRepository;

    public TestSuiteService(TestSuiteRepository testSuiteRepository) {
        this.testSuiteRepository = testSuiteRepository;
    }

    public TestSuiteEntity saveTestSuite(String name, String testSuiteJson) {
        TestSuiteEntity entity = new TestSuiteEntity();

        entity.setConfiguration(testSuiteJson);
        entity.setName(name);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setState(State.ACTIVE);
        log.info("Saving test suite: " + entity.toString());
        return testSuiteRepository.save(entity);
    }

    public List<TestSuiteEntity> getAllActiveTestSuites() {
        return testSuiteRepository.findAllByState(State.ACTIVE);
    }

    public Optional<TestSuiteEntity> getTestSuiteById(int id) {
        return testSuiteRepository.findById(id);
    }

    public TestSuiteEntity updateTestSuite(int id, String name, String testSuiteJson) {
        TestSuiteEntity entity = testSuiteRepository.findById(id).orElse(null);
        if (entity == null) throw new RuntimeException("Could not find test suite with id " + id);

        entity.setConfiguration(testSuiteJson);
        if (!name.isEmpty()) {
            entity.setName(name);
        }
        entity.setUpdatedAt(LocalDateTime.now());
        return testSuiteRepository.save(entity);
    }

    public void deleteTestSuite(int id) {
        Optional<TestSuiteEntity> foundEntity = testSuiteRepository.findById(id);
        if (foundEntity.isPresent()) {
            TestSuiteEntity entity = foundEntity.get();
            entity.setState(State.DELETED);
            entity.setUpdatedAt(LocalDateTime.now());
            testSuiteRepository.save(entity);
        } else {
            throw new RuntimeException("Could not find test suite with id " + id);
        }
    }
}