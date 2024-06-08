package dev.jwtly10.dynatest.dta;

import dev.jwtly10.dynatest.models.TestSuiteEntity;
import dev.jwtly10.dynatest.models.TestSuiteMetaEntity;
import dev.jwtly10.dynatest.models.TestSuiteRunLogEntity;
import lombok.Data;

import java.util.List;

@Data
public class TestSuiteDataResponseBody {
    private TestSuiteEntity entity;
    private TestSuiteMetaEntity metaData;
    private List<TestSuiteRunLogEntity> runLogs;
}