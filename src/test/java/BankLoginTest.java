import com.codeborne.selenide.Configuration;
import data.DataHelper;
import data.SQLHelper;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;
import page.LoginPage;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.cleanAuthCodes;
import static data.SQLHelper.cleanDatabase;

public class BankLoginTest {
    LoginPage loginPage;

    @AfterEach
    void tearDown(){
        cleanAuthCodes();
    }
    @AfterAll
    static void tearDownAll(){
        cleanDatabase();
    }
    @BeforeEach
    void setUp(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        Map<String, Object> prefs = new HashMap<String , Object>();
        prefs.put("credentials_enabled_service", false);
        prefs.put("password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        Configuration.browserCapabilities = options;

        loginPage = open ( "http://localhost:9999", LoginPage.class);
    }
    @Test
    @DisplayName("Successful login with existing username and password")
    void shouldSuccessfulLogin () {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity() ;
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerity(verificationCode.getCode());
    }
    @Test
    @DisplayName("An error message appears if the user does not exist in the database")
    void shouldGetErrorNotificationIfLoginWithExistUserAndRandomVerificationCode() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! Неверно указан логин или пароль");
    }
    @Test
    @DisplayName("Error notification if the login is with an active user existing in the database and a random verification code")
    void shouldGetErrorNotificationIfFLoginWithExistUserAndRandomVerificationCode() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }
}
