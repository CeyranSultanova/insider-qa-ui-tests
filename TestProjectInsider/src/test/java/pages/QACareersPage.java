package pages;

import base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class QACareersPage extends BasePage {
    private void switchToLeverFrameIfPresent() {
        driver.switchTo().defaultContent();
        try {
            WebElement iframe = driver.findElement(
                    By.cssSelector("iframe[src*='lever'], iframe[src*='jobs.lever.co']")
            );
            driver.switchTo().frame(iframe);
        } catch (NoSuchElementException ignored) {

        }
    }


    private static final String URL = "https://insiderone.com/careers/quality-assurance/";

    /* =======================
       Page Locators
       ======================= */

    private final By seeAllQaJobsBtn =
            By.xpath("//a[contains(text(),'See all QA jobs') or contains(text(),'See All QA Jobs')]");


    private final By locationFilterSelect = By.xpath(
            "//select[contains(@name,'location') or contains(@aria-label,'Location')]"
    );

    private final By departmentFilterSelect = By.xpath(
            "//select[contains(@name,'department') or contains(@aria-label,'Department')]"
    );




    private final By jobsContainer =
            By.cssSelector(".postings, [data-qa='postings'], .posting-cards");

    private final By postingItem =
            By.cssSelector(".posting, [data-qa='posting']");

    private final By postingTitle =
            By.cssSelector(".posting-title, a.posting-title, [data-qa='posting-title']");

    private final By postingDepartment =
            By.cssSelector(".posting-categories .department, .posting-categories .team");

    private final By postingLocation =
            By.cssSelector(".posting-categories .location");

    private final By viewRoleBtnInCard =
            By.xpath(".//a[contains(text(),'View Role') or contains(text(),'View role')]");
    private final By leverIframe = By.cssSelector("iframe[src*='lever'], iframe[src*='jobs.lever.co']");
    private final By postingsInsideIframe = By.cssSelector(".posting, a.posting-title, .postings");
    private final By viewRoleLinks = By.xpath(
            "//a[contains(.,'View Role') or contains(.,'View role') or contains(.,'VIEW ROLE') or " +
                    "contains(.,'Apply') or contains(.,'Apply Now') or contains(.,'Apply now')]"
    );


    public QACareersPage(WebDriver driver) {
        super(driver);
    }



    public QACareersPage openPage() {
        open(URL);
        return this;
    }

    public QACareersPage clickSeeAllQaJobs() {
        clickSafe(seeAllQaJobsBtn);

        // open-positions page
        wait.until(d -> d.getCurrentUrl().contains("/careers/open-positions"));

        try {
            WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(leverIframe));
            driver.switchTo().frame(iframe);


            wait.until(ExpectedConditions.visibilityOfElementLocated(postingsInsideIframe));
        } catch (TimeoutException e) {

            driver.switchTo().defaultContent();
        }

        return this;
    }



    public QACareersPage filterByLocationAndDepartment(String location, String department) {

        try {
            WebElement iframe = driver.findElement(leverIframe);
            driver.switchTo().frame(iframe);
        } catch (Exception ignored) {
            driver.switchTo().defaultContent();
        }

        WebElement locationSelectEl = waitVisible(locationFilterSelect);
        WebElement departmentSelectEl = waitVisible(departmentFilterSelect);

        Select locSelect = new Select(locationSelectEl);
        Select depSelect = new Select(departmentSelectEl);


        selectOptionContains(locSelect, "istanbul"); // turk -> matches turkey / türkiye
        selectOptionContains(depSelect, "quality assurance"); // department

        return this;
    }

    private void selectOptionContains(Select select, String... needles) {
        String bestText = null;

        for (WebElement opt : select.getOptions()) {
            String t = opt.getText() == null ? "" : opt.getText().trim();
            String low = t.toLowerCase();

            boolean ok = true;
            for (String n : needles) {
                if (!low.contains(n.toLowerCase())) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                bestText = t;
                break;
            }
        }

        if (bestText == null) {

            StringBuilder sb = new StringBuilder();
            for (WebElement opt : select.getOptions()) {
                sb.append("[").append(opt.getText().trim()).append("] ");
            }
            throw new NoSuchElementException(
                    "Cannot find option containing: " + String.join(", ", needles) +
                            ". Available options: " + sb
            );
        }

        select.selectByVisibleText(bestText);
    }


    public void assertJobsListPresent() {
        // always re-evaluate iframe context
        switchToLeverFrameIfPresent();
        acceptCookiesIfPresent();

        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(30));
        w.until(d -> d.findElements(viewRoleLinks).size() > 0);

        if (driver.findElements(viewRoleLinks).isEmpty()) {
            throw new AssertionError("No 'View Role/Apply' links found on Open Positions page.");
        }
    }


    public List<WebElement> getPostingElements() {
        return driver.findElements(postingItem);
    }

    /* =======================
       Job Card Handling
       ======================= */

    public JobCard readJobCard(WebElement posting) {
        String title = posting.findElement(postingTitle).getText().trim();

        String department = "";
        String location = "";

        try {
            department = posting.findElement(postingDepartment).getText().trim();
        } catch (NoSuchElementException ignored) {}

        try {
            location = posting.findElement(postingLocation).getText().trim();
        } catch (NoSuchElementException ignored) {}

        return new JobCard(title, department, location);
    }

    public void clickFirstViewRole() {
        switchToLeverFrameIfPresent();
        acceptCookiesIfPresent();

        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(30));
        w.until(d -> d.findElements(viewRoleLinks).size() > 0);

        WebElement first = driver.findElements(viewRoleLinks).get(0);

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", first);

        try {
            first.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", first);
        }

        // after click - usually opens Lever application page (may be new tab)
    }



    /* =======================
       Inner JobCard class
       ======================= */

    public static class JobCard {
        public final String position;
        public final String department;
        public final String location;

        public JobCard(String position, String department, String location) {
            this.position = position;
            this.department = department;
            this.location = location;
        }

        public List<String> validateContains(
                String positionMust,
                String departmentMust,
                String locationMust1,
                String locationMust2
        ) {
            List<String> errors = new ArrayList<>();

            if (!position.toLowerCase().contains(positionMust.toLowerCase())) {
                errors.add("Position mismatch: " + position);
            }

            if (!department.toLowerCase().contains(departmentMust.toLowerCase())) {
                errors.add("Department mismatch: " + department);
            }

            String loc = location.toLowerCase();
            if (!(loc.contains(locationMust1.toLowerCase())
                    && loc.contains(locationMust2.toLowerCase()))) {
                errors.add("Location mismatch: " + location);
            }

            return errors;
        }
    }
}
