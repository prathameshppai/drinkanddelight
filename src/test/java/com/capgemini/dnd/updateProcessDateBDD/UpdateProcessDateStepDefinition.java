package com.capgemini.dnd.updateProcessDateBDD;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import junit.framework.Assert;

public class UpdateProcessDateStepDefinition {

	public UpdateProcessDateStepDefinition() {
	}
	
	WebDriver driver;
	
	@Given("User is on update process date page")
	public void user_is_on_update_process_date_page() {
		ChromeOptions options=new ChromeOptions();
		options.addArguments("start-maximized");
		
		options.addArguments("disable-web-security");
		
		
		System.setProperty("webdriver.chrome.driver","C:\\Users\\gauragai\\Downloads\\chromedriver.exe");
		driver = new ChromeDriver(options);
		driver.get("http://localhost:4200/set-process-date");
		String currentUrl = driver.getCurrentUrl();
		 Assert.assertEquals("http://localhost:4200/set-process-date", currentUrl);
	}

	@When("User enters a number in order ID as {string}")
	public void user_enters_a_number_in_order_ID_as(String string) {
		driver.findElement(By.name("orderID")).sendKeys(string);
	}

	@When("User enters Process Date as {string}")
	public void user_enters_Process_Date_as(String string) {
		WebElement dateInput = driver.findElement(By.xpath("//*[@id=\"processdate\"]"));
		dateInput.click();
		dateInput.clear();
		dateInput.sendKeys(string);
		WebElement randomClick = driver.findElement(By.xpath("//*[@id=\"navbardrop\"]"));
		JavascriptExecutor jsRandom = (JavascriptExecutor)driver;
		jsRandom.executeScript("arguments[0].click();", randomClick);
		
	}

	@When("user clicks on update button")
	public void user_clicks_on_update_button() {
		WebElement randomClick = driver.findElement(By.xpath("//*[@id=\"navbardrop\"]"));
		JavascriptExecutor jsRandom = (JavascriptExecutor)driver;
		jsRandom.executeScript("arguments[0].click();", randomClick);
		
		
		WebElement updateBtn = driver.findElement(By.xpath("/html/body/app-root/body/app-set-process-date/div[1]/form/button"));
		JavascriptExecutor jsButton = (JavascriptExecutor)driver;
		jsButton.executeScript("arguments[0].click();", updateBtn);
	}

	@Then("The message such as {string} is displayed")
	public void the_message_such_as_is_displayed(String string) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			}
		
		WebElement outputElement = driver.findElement(By.name("outputbox"));
		
		String outputValue = outputElement.getText();
		
		Assert.assertEquals(string,outputValue);
	}
	
	@After
	public void tearDown() {
		   driver.quit();
		}

}
