package com.capgemini.dnd.updateProductStock;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class) 
@CucumberOptions(
		plugin = {"pretty", "html:target/cucumber"})
public class UpdateProductStockRunner {

	public UpdateProductStockRunner() {
		// TODO Auto-generated constructor stub
	}

}
