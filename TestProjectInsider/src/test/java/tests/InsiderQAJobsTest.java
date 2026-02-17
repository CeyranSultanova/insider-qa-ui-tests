package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.HomePage;
import pages.LeverApplicationPage;
import pages.QACareersPage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class
InsiderQAJobsTest extends BaseTest {

    @Test
    void insiderQaJobsFlow() {


        // 1) Home
        HomePage home = new HomePage(driver());
        home.openHome();
        home.assertHomeOpenedAndMainBlocksLoaded();

        // 2) See all QA jobs
        QACareersPage qa = new QACareersPage(driver());
        qa.openPage()
                .clickSeeAllQaJobs();

        System.out.println("URL after See all QA jobs: " + driver().getCurrentUrl());


        qa.assertJobsListPresent();

        // 2.1) Filter
        qa.filterByLocationAndDepartment("Istanbul, Turkey", "Quality Assurance");


        List<WebElement> postings = qa.getPostingElements();

        if (postings.isEmpty()) {
            throw new AssertionError(
                    "No postings found after filtering. Either filter UI didn't apply or there are no QA jobs in Istanbul right now."
            );
        }



        // 3) Validate each job
        List<String> errors = new ArrayList<>();
        int idx = 1;

        for (WebElement posting : postings) {
            QACareersPage.JobCard card = qa.readJobCard(posting);
            List<String> cardErrors = card.validateContains(
                    "Quality Assurance", // Position contains
                    "Quality Assurance", // Department contains
                    "Istanbul",          // Location contains
                    "Turkey"             // Location contains
            );

            for (String e : cardErrors) {
                errors.add("[" + idx + "] " + e);
            }
            idx++;
        }

        if (!errors.isEmpty()) {
            throw new AssertionError("Job cards validation failed:\n" + String.join("\n", errors));
        }

        // 4) View role
        String original = driver().getWindowHandle();

        qa.clickFirstViewRole();

        // wait possible new tab
        new WebDriverWait(driver(), Duration.ofSeconds(10))
                .until(d -> d.getWindowHandles().size() > 1);

        for (String h : driver().getWindowHandles()) {
            if (!h.equals(original)) {
                driver().switchTo().window(h);
                break;
            }
        }


        new WebDriverWait(driver(), Duration.ofSeconds(20))
                .until(d -> d.getCurrentUrl().contains("lever.co"));

        new LeverApplicationPage(driver()).assertRedirectedToLeverApplicationForm();
        {
            throw new AssertionError("Force fail to test screenshot");
        }

    }
}
