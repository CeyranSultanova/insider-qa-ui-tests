package base;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ScreenshotOnFailExtension implements TestWatcher {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");


    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {

        Optional<WebDriver> driverOpt = BaseTest.getDriver();
        if (driverOpt.isEmpty()) return;

        WebDriver driver = driverOpt.get();

        try {
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);

            String testName = context.getDisplayName().replaceAll("[^a-zA-Z0-9-_\\.]", "_");
            String ts = LocalDateTime.now().format(FMT);

            Path target = dir.resolve(testName + "_" + ts + ".png");

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), target);

            System.out.println("📸 Screenshot saved: " + target.toAbsolutePath());
        } catch (Exception ignored) { }

        
    }
}
