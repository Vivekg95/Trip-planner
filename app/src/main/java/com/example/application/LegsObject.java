package com.example.application;

import java.util.List;

/**
 * Created by ADMIN on 9/23/2017.
 */


public class LegsObject {
    private List<StepsObject> steps;
    public LegsObject(List<StepsObject> steps) {
        this.steps = steps;
    }
    public List<StepsObject> getSteps() {
        return steps;
    }
}
