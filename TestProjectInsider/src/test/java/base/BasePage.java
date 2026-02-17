package base;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitClickable(locator).click();
    }

    protected void open(String url) {
        driver.get(url);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    // ---------- cookie helpers ----------
    protected void acceptCookiesIfPresent() {
        By acceptBtn = By.id("wt-cli-accept-btn");
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement btn = shortWait.until(ExpectedConditions.visibilityOfElementLocated(acceptBtn));
            shortWait.until(ExpectedConditions.elementToBeClickable(acceptBtn));
            btn.click();
            shortWait.until(ExpectedConditions.invisibilityOfElementLocated(acceptBtn));
        } catch (Exception ignored) {
        }
    }

    protected void scrollIntoView(By locator) {
        WebElement el = waitVisible(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    protected void clickSafe(By locator) {
        acceptCookiesIfPresent();
        scrollIntoView(locator);
        try {
            click(locator);
        } catch (ElementClickInterceptedException e) {
            acceptCookiesIfPresent();
            WebElement el = waitClickable(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }
}
