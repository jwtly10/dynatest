package dev.jwtly10.dynatest.models;

import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class DataTypes {
    private Map<String, Object> types = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
}