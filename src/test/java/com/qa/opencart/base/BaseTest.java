package com.qa.opencart.base;

import java.util.Properties;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.microsoft.playwright.Page;
import com.qa.opencart.factory.PlaywrightFactory;
import com.qa.opencart.pages.LoginPage;

public class BaseTest {

	PlaywrightFactory pf;
	Page page;
	protected Properties prop;
	protected LoginPage loginPage;

	@Parameters({ "browser" })
	@BeforeTest
	public void setup(String browserName) {
		pf = new PlaywrightFactory();

		// Initialize prop before modifying it based on parameters
		prop = pf.init_prop();

		if (browserName != null) {
			prop.setProperty("browser", browserName);
		}

		page = pf.initBrowser(prop);
		this.loginPage = new LoginPage(page);
	}

	@AfterTest
	public void tearDown() {
		page.context().browser().close();
	}

}
