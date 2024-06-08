package dev.jwtly10.dynatest.respository;

import dev.jwtly10.dynatest.models.TestSuiteMetaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestSuiteMetaRepository extends JpaRepository<TestSuiteMetaEntity, Integer> {
    Optional<TestSuiteMetaEntity> findByTestSuiteId(int testSuiteId);
}