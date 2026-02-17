package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private static final String URL = "https://insiderone.com/";

    private final By header = By.cssSelector("header");
    private final By main = By.cssSelector("main, [role='main']");
    private final By footer = By.cssSelector("footer");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage openHome() {
        open(URL);
        return this;
    }

    public void assertHomeOpenedAndMainBlocksLoaded() {
        waitVisible(header);
        waitVisible(main);
        waitVisible(footer);
    }
}
