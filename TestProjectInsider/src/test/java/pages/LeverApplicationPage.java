package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LeverApplicationPage extends BasePage {

    private final By applicationForm = By.cssSelector("form[action*='lever'], form#application-form, form");

    public LeverApplicationPage(WebDriver driver) {
        super(driver);
    }

    public void assertRedirectedToLeverApplicationForm() {
        String url = currentUrl().toLowerCase();
        if (!(url.contains("lever.co") || url.contains("jobs.lever.co"))) {
            throw new AssertionError("Not redirected to Lever. Current URL: " + url);
        }
        waitVisible(applicationForm);
    }
}
