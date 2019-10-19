package com.capgemini.dnd.updateRMStock;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import junit.framework.Assert;

public class UpdateRMStockStepDefinition {

	public UpdateRMStockStepDefinition() {
		
	}
	
	WebDriver driver;
	
	@Given("user is on update RM Stock page")
	public void user_is_on_update_RM_Stock_page() {
		ChromeOptions options=new ChromeOptions();
		options.addArguments("start-maximized");
		options.addArguments("disable-web-security");
		
		System.setProperty("webdriver.chrome.driver","C:\\Users\\gauragai\\Downloads\\chromedriver.exe");
		driver = new ChromeDriver(options);
		driver.get("http://localhost:4200/update-rawmaterial-stock");
		String currentUrl = driver.getCurrentUrl();
		 Assert.assertEquals("http://localhost:4200/update-rawmaterial-stock", currentUrl);
	}

	@When("User enters the field order ID as {string}")
	public void user_enters_the_field_order_ID_as(String string) {
		driver.findElement(By.name("orderID")).sendKeys(string);
	}

	@When("User enters manufacturing Date as {string}")
	public void user_enters_manufacturing_Date_as(String string) {
		
		WebElement dateInput = driver.findElement(By.xpath("//*[@id=\"manufacturingdate\"]"));
		dateInput.click();
		dateInput.clear();
		dateInput.sendKeys(string);
		WebElement randomClick = driver.findElement(By.xpath("//*[@id=\"navbardrop\"]"));
		JavascriptExecutor jsRandom = (JavascriptExecutor)driver;
		jsRandom.executeScript("arguments[0].click();", randomClick);
	}

	@When("User enters expiry Date as {string}")
	public void user_enters_expiry_Date_as(String string) {
		WebElement dateInput = driver.findElement(By.xpath("//*[@id=\"expirydate\"]"));
		dateInput.click();
		dateInput.clear();
		dateInput.sendKeys(string);
		WebElement randomClick = driver.findElement(By.xpath("//*[@id=\"navbardrop\"]"));
		JavascriptExecutor jsRandom = (JavascriptExecutor)driver;
		jsRandom.executeScript("arguments[0].click();", randomClick);
	}

	@When("user selects Passed from Dropdown menu")
	public void user_selects_Passed_from_Dropdown_menu() {
	    Select drpDownQualityStatus = new Select(driver.findElement(By.xpath("/html/body/app-root/body/app-update-rawmaterial-stock/div[1]/form/div[4]/div/div[2]/select")));
	    drpDownQualityStatus.selectByVisibleText("Passed");
	}

	@When("user clicks on update stock button")
	public void user_clicks_on_update_stock_button() {
		WebElement randomClick = driver.findElement(By.xpath("//*[@id=\"navbardrop\"]"));
		JavascriptExecutor jsRandom = (JavascriptExecutor)driver;
		jsRandom.executeScript("arguments[0].click();", randomClick);
		
		
		WebElement updateBtn = driver.findElement(By.xpath("/html/body/app-root/body/app-update-rawmaterial-stock/div[1]/form/button"));
		JavascriptExecutor jsButton = (JavascriptExecutor)driver;
		jsButton.executeScript("arguments[0].click();", updateBtn);
	}

	@Then("The message {string} is displayed")
	public void the_message_is_displayed(String string) {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			}
		
		WebElement outputElement = driver.findElement(By.name("outputbox"));
		
		String outputValue = outputElement.getText();
		
		Assert.assertEquals(string, outputValue);
	}
	
	@After
	public void tearDown() {
	   driver.quit();
	}

}
