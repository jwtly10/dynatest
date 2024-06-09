package dev.jwtly10.dynatest.services;

import dev.jwtly10.dynatest.dta.TestSuiteDataResponseBody;
import dev.jwtly10.dynatest.models.TestSuiteEntity;
import dev.jwtly10.dynatest.models.TestSuiteMetaEntity;
import dev.jwtly10.dynatest.models.TestSuiteRunLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SuiteService {
    private final TestSuiteService testSuiteService;
    private final TestSuiteRunLogService testSuiteRunLogService;
    private final TestSuiteMetaService testSuiteMetaService;

    public SuiteService(TestSuiteService testSuiteService, TestSuiteRunLogService testSuiteRunLogService, TestSuiteMetaService testSuiteMetaService) {
        this.testSuiteService = testSuiteService;
        this.testSuiteRunLogService = testSuiteRunLogService;
        this.testSuiteMetaService = testSuiteMetaService;
    }

    public Optional<TestSuiteDataResponseBody> getAllDataFromTestSuite(int id) {
        log.info("Querying for test suite with id {}", id);
        TestSuiteDataResponseBody res = new TestSuiteDataResponseBody();

        TestSuiteEntity testSuiteEntity = testSuiteService.getTestSuiteById(id).orElse(null);
        if (testSuiteEntity == null) {
            log.info("Could not find test suite with id {}", id);
            return Optional.empty();
        }

        res.setEntity(testSuiteEntity);

        Optional<TestSuiteMetaEntity> testSuiteMetaEntity = testSuiteMetaService.getMetaDataForTestSuiteRun(id);
        testSuiteMetaEntity.ifPresent(res::setMetaData);

        List<TestSuiteRunLogEntity> testSuiteRunLogEntities = testSuiteRunLogService.getRunLogsForTestSuite(id);
        res.setRunLogs(testSuiteRunLogEntities);

        log.info("Found data for a test suite: " + res);

        return Optional.of(res);
    }
}