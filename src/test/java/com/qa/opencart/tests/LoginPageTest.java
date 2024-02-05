package com.qa.opencart.tests;

import com.qa.opencart.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.qa.opencart.base.BaseTest;
import com.qa.opencart.constants.AppConstants;

public class LoginPageTest extends BaseTest {



	@Test(priority = 1)
	public void loginPageNavigationTest() throws InterruptedException {
		this.loginPage.addToCart();
	}

}
