package com.askomdch.qa;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "features",
        glue = {"com.askomdch.qa.hooks", "com.askomdch.qa.steps"},
        plugin = {"pretty", "summary"})
public class RunCucumberIT {
}
