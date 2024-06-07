package dev.jwtly10.dynatest.respository;

import dev.jwtly10.dynatest.enums.State;
import dev.jwtly10.dynatest.models.TestSuiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSuiteRepository extends JpaRepository<TestSuiteEntity, Integer> {
    List<TestSuiteEntity> findAllByState(State state);
}