package de.cloud.core.web.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TestEntity {
    private String test;

    public TestEntity(String test) {
        this.test = test;
    }
}
