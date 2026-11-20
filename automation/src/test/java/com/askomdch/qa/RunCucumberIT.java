package com.askomdch.qa;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "features",
        glue = {"com.askomdch.qa.hooks", "com.askomdch.qa.steps"},
        tags = "not @cart_session and not @account_login and not @account_registration"
                + " and not @account_recovery and not @pending",
        plugin = {"pretty", "summary"})
public class RunCucumberIT {
}
