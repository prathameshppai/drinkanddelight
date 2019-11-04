package com.capgemini.dnd.trackProductBDD;


import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;


@RunWith(Cucumber.class) 
@CucumberOptions(
		plugin = {"pretty", "html:target/cucumber"},
		features = {"classpath:com/capgemini/dnd/updateExitDate/updateExitDate.feature"})
public class TrackProductRunner {

	public TrackProductRunner() {
		
	}

}
