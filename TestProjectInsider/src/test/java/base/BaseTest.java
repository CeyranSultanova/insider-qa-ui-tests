package base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import java.util.Optional;

@ExtendWith(ScreenshotOnFailExtension.class)
public abstract class BaseTest {


    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

    @BeforeEach
    void setUp() {
        WebDriver driver = DriverFactory.createDriver();
        TL_DRIVER.set(driver);
    }


    @AfterEach
    void tearDown() {
        WebDriver driver = TL_DRIVER.get();
        if (driver != null) {
            driver.quit();
            TL_DRIVER.remove();
        }
    }

    protected WebDriver driver() {
        return TL_DRIVER.get();
    }

    public static Optional<WebDriver> getDriver() {
        return Optional.ofNullable(TL_DRIVER.get());
    }
}
