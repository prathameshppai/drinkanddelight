package com.capgemini.dnd.trackRawMaterialBDD;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import junit.framework.Assert;

public class TrackRMStepDefinition {

	public TrackRMStepDefinition() {
		
	}
	
	WebDriver driver;
	
	@Given("User is on track raw material order page")
	public void user_is_on_track_raw_material_order_page() {
		ChromeOptions options=new ChromeOptions();
		options.addArguments("start-maximized");
		
		options.addArguments("disable-web-security");
		
		
		System.setProperty("webdriver.chrome.driver","C:\\Users\\gauragai\\Downloads\\chromedriver.exe");
		driver = new ChromeDriver(options);
		driver.get("http://localhost:4200/track-rawmaterial-order");
		String currentUrl = driver.getCurrentUrl();
		 Assert.assertEquals("http://localhost:4200/track-rawmaterial-order", currentUrl);
	}

	@When("User enters number in the order ID field as {string}")
	public void user_enters_number_in_the_order_ID_field_as(String string) {
		driver.findElement(By.name("orderID")).sendKeys(string);
	}

	@When("user clicks on track order button present below")
	public void user_clicks_on_track_order_button_present_below() {
		WebElement randomClick = driver.findElement(By.xpath("//*[@id=\"navbardrop\"]"));
		JavascriptExecutor jsRandom = (JavascriptExecutor)driver;
		jsRandom.executeScript("arguments[0].click();", randomClick);
		
		
		WebElement loginBtn = driver.findElement(By.xpath("/html/body/app-root/body/app-track-rawmaterial-order/div[1]/form/button"));
		JavascriptExecutor jsButton = (JavascriptExecutor)driver;
		jsButton.executeScript("arguments[0].click();", loginBtn);
	}

	@Then("The shelf life of the given order Id {int} is displayed below")
	public void the_shelf_life_of_the_given_order_Id_is_displayed_below(Integer int1) {

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			}
		
		WebElement outputElement = driver.findElement(By.name("outputbox"));
		
		String outputValue = outputElement.getText();
		
		Assert.assertEquals("The order ID had been in the warehouse with warehouseID = w02 from 2019-07-17 to 2019-10-22(97 days)",outputValue);
	}

	@Then("A message {string} is displayed in the blank space below")
	public void a_message_is_displayed_in_the_blank_space_below(String string) {
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
