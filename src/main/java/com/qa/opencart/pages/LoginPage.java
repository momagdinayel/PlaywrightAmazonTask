package com.qa.opencart.pages;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
public class LoginPage {

	private static Page page;

	private String languagePanel=".icp-nav-flag.icp-nav-flag-eg.icp-nav-flag-lop";
	private String englishLang="(//span[contains(.,'English')])[2]";
	private static String signInPanel="span[class='nav-line-2 ']";
	private static String signInButton="(//a[contains(.,'Sign in')])[1]";
	private static String email="#ap_email";
	private static String continueButton="#continue";
	private static String password="#ap_password";
	private static String signInSubmit="#signInSubmit";

	// 1. String Locators -
	private String allButton = "(//a[contains(.,'All')])[1]";
	private String seeAll = "//div[normalize-space()='See all']";
	private String videoGames = "//a[@data-ref-tag=\"nav_em_1_1_1_21\"]";
	private String allVideoGames = "//a[contains(.,'All Video Games')]";
	private String freeShipping = "(//i[@class='a-icon a-icon-checkbox'])[1]";
	private String newButton = "(//span[normalize-space()='New'])[2]";
	private String sortButton = "#a-autoid-0-announce";
	private String highToLow = "//a[contains(.,'Price: High to Low')]";
	private static String addToCart = "#add-to-cart-button";
	private static String nextButton = "//a[normalize-space()='Next']";
	private static String proceedButton="input[value='Proceed to checkout']";
	private static String noThanksButton="input[aria-labelledby='attachSiNoCoverage-announce']";
	private static String proceedBuy="input[value='Proceed to checkout']";
	private static String cashButton="//span[contains(.,'Cash on Delivery (COD)')]";
	private static String itemsPrice="//td[@xpath='1']";
	private static String usePayMethod="input[name='ppw-widgetEvent:SetPaymentPlanSelectContinueEvent'][type='submit']";


	// 2. page constructor:
	public LoginPage(Page page) {
		LoginPage.page = page;
	}

	// 3. page actions/methods:
	public String getLoginPageTitle() {
		return page.title();
	}

	public void selectLanguage(){
		waitForLocatorVisibility(languagePanel);
		page.hover(languagePanel);
		page.click(englishLang);
	}

	private static List<Double> prices = new ArrayList<>();

	public boolean addToCart() throws InterruptedException {
		selectLanguage();
		navigateToVideoGames();
		processItemsOnPage();
		double sum = calculateSum();
		System.out.println("Sum of prices below 15000.00: " + sum);
		double actualSum=itemsPriceCalculator();
		System.out.println("Actual Sum is: "+actualSum);
//		clickProceedButton();
		clickCash();
		clickUsePayMethod();
        return sum == actualSum;
	}
	private static void clickUsePayMethod(){
		page.click(usePayMethod);
	}
	private double itemsPriceCalculator(){
        return extractNumber(itemsPrice);
	}
	private static void clickSignIn(){
		page.hover(signInPanel);
		page.click(signInButton);
	}
	private static void clickCash(){
		page.click(cashButton);
	}
	private static void enterEmail(String email1){
		page.type(email,email1);
	}
	private static void clickContinue(){
		page.click(continueButton);
	}
	private static void enterPassword(String password1){
		page.type(password,password1);
	}
	private static void clickSignInSubmit(){
		page.click(signInSubmit);
	}

	public void login(String email1,String pass1){
		clickSignIn();
		enterEmail(email1);
		clickContinue();
		enterPassword(pass1);
		clickSignInSubmit();
	}

	private void navigateToVideoGames(){
		waitForLocatorVisibility(allButton);
		page.click(allButton);
		scrollToElement(seeAll);
		waitForLocatorVisibility(seeAll);
		page.click(seeAll);
		scrollToElement(videoGames);
		waitForLocatorVisibility(videoGames);
		page.click(videoGames);
		waitForLocatorVisibility(allVideoGames);
		page.click(allVideoGames);
		waitForLocatorVisibility(freeShipping);
		page.click(freeShipping);
		scrollToElement(newButton);
		waitForLocatorVisibility(newButton);
		page.click(newButton);
		scrollToElement(sortButton);
		waitForLocatorVisibility(sortButton);
		page.click(sortButton);
		waitForLocatorVisibility(highToLow);
		page.click(highToLow);
	}

	private static void processItemsOnPage() throws InterruptedException {
		String items = "//span[@class='a-color-base']";
		List<ElementHandle> elements = page.querySelectorAll(items);

		for (ElementHandle element : elements) {
			handleItem(element);
		}
	}

	private double calculateSum() {
		return prices.stream().filter(price -> price < 15000.00).mapToDouble(Double::doubleValue).sum();
	}

	private static void clickNextPage() {
		scrollToElement(nextButton);
		waitForLocatorVisibility(nextButton);
		page.click(nextButton);
	}
	private static void clickProceedButton(){
		page.click(proceedButton);
	}

	private static void waitForLocatorVisibility(String element) {
		page.waitForSelector(element, new Page.WaitForSelectorOptions().setTimeout(15000));
	}

	private static void scrollToElement(String locator) {
		ElementHandle element = page.querySelector(locator);
		if (element != null) {
			element.scrollIntoViewIfNeeded();
			waitForLocatorVisibility(String.valueOf(element));
			System.out.println("Scrolling to " + locator + "!");
		} else {
			System.out.println("Element not found!");
		}
	}

	private static void handleItem(ElementHandle element) throws InterruptedException {
		String priceText = element.textContent();
		double price = extractNumber(priceText);
		boolean isFirstIteration = true;

// Your loop or condition
		if (price < 15000.00) {
			// Store the price in the list
			prices.add(price);
			waitForLocatorVisibility(String.valueOf(element));
			page.click(String.valueOf(element));
			scrollToElement(addToCart);
			waitForLocatorVisibility(addToCart);
			page.click(addToCart);
			if (page.locator(noThanksButton).isVisible()) {
				waitForLocatorVisibility(noThanksButton);
				page.click(noThanksButton);
			} else {
				System.out.println("Completing...");
			}

			if (isFirstIteration) {
				page.goBack();
			} else {
				waitForLocatorVisibility(proceedBuy);
				page.click(proceedBuy);
			}
			isFirstIteration = !isFirstIteration;
		} else {
			scrollToElement(nextButton);
			waitForLocatorVisibility(nextButton);
			clickNextPage();
			processItemsOnPage();
		}


	}

	// Helper method to extract the numeric value from a string
	private static double extractNumber(String text) {
		String numericText = text.replaceAll("[^0-9.]+", "");
		return Double.parseDouble(numericText);
	}
}
