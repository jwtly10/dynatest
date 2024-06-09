package dev.jwtly10.dynatest.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSuiteRunLogRepository extends JpaRepository<TestSuiteRunLogEntity, Integer> {
    @Query("SELECT log FROM TestSuiteRunLogEntity log WHERE log.testSuiteId = :testSuiteId ORDER BY log.id DESC")
    Optional<TestSuiteRunLogEntity> findLastLogRunForTestSuite(@Param("testSuiteId") int testSuiteId);

    List<TestSuiteRunLogEntity> findAllByTestSuiteId(int testSuiteId);

    List<TestSuiteRunLogEntity> findAllByTestSuiteIdOrderByIdDesc(int testSuiteId);

}