package com.empire.employeefinder.kafka.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SelectionMessage {

    private String companyName;

    private String regNumber;

    private List<String> candidates;
}
