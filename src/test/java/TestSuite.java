import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestSuite {

    public static WebDriver driver;

    @Before
    public void setUp(){
        //chrome
        System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
        //System.setProperty("webdriver.gecko.driver","./drivers/gecko");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:3000");

        //firefox

        //headless (phantomjs)
        
    }

    @Test
    public void guestUser(){
        String url = driver.getCurrentUrl();
        Assert.assertEquals(url, "http://localhost:3000/");
    }

    @Test
    public void openBookAndReadDetails(){
        List<WebElement> bookDescriptionElements;
        WebElement bookDescriptionElement;
        WebElement bookTitleElement;
        WebElement bookDescElement;
        WebElement bookPriceElement;

        getBook(0);

        bookTitleElement = driver.findElement(By.tagName("h2"));
        bookDescriptionElement = driver.findElement(By.id("product-description"));

        bookDescriptionElements = bookDescriptionElement.findElements(By.tagName("p"));
        bookDescElement = bookDescriptionElements.get(0).findElement(By.tagName("em"));
        bookPriceElement = bookDescriptionElements.get(1);

        Assert.assertEquals(bookTitleElement.getText(), Data.BOOK_TITLE);
        Assert.assertEquals(bookDescElement.getText(), Data.BOOK_DESC);
        Assert.assertTrue(bookPriceElement.getText().contains(Data.BOOK_PRINCE));
    }

    @Test
    public void createUserSuccess() {
        List<WebElement> flashNotices;
        List<WebElement> formItens;

        getAuthItens().get(2).click();

        createUserAction(Data.USER_SIGNUP, Data.EMAIL_SIGNUP);

        flashNotices = driver.findElements(By.id("flash_notice"));
        Assert.assertEquals(flashNotices.get(1).getText(), Data.USER_CREATION_SUCCESS);
        driver.get("http://localhost:3000/db/seed");
    }

    @Test
    public void userLoginSuccess() {
        WebElement form;
        List<WebElement> formItens;
        List<WebElement> flashNotices;

        getAuthItens().get(3).click();
        loginUserAction();

        Assert.assertEquals(getFlashNotice().get(1).getText(), Data.USER_LOGIN_SUCCESS);
    }

    @Test
    public void addBookCart() {
        addBookAction();
        Assert.assertEquals(getFlashNotice().get(1).getText(), Data.ADD_CART_ITEM);
    }

    @Test
    public void removeBookCart(){
        addBookAction();
        removeBookAction();
        Assert.assertEquals(getFlashNotice().get(1).getText(), Data.REMOVE_CART_ITEM);
    }

    @Test
    public void checkoutBookCart() {
        addBookAction();
        WebElement form = driver.findElement(By.tagName("form"));
        List<WebElement> formInputs = form.findElements(By.tagName("input"));
        formInputs.get(2).click();
    }
    @After
    public void tearDown(){
        driver.quit();
    }

    private List<WebElement> getSignUpElements (String id) {
        WebElement form = driver.findElement(By.id(id));
        return form.findElements(By.tagName("p"));
    }

    private WebElement findElementIndexByTag(List<WebElement> element, int index, String tag) {
        return element.get(index).findElement(By.tagName(tag));
    }

    private List<WebElement> getAuthItens() {
        WebElement element = driver.findElement(By.id("auth"));
        return element.findElements(By.tagName("a"));
    }

    private void createUserAction(String user, String email) {
        List<WebElement> formItens = getSignUpElements("new_user");
        findElementIndexByTag(formItens, 0, "input").sendKeys(user);
        findElementIndexByTag(formItens, 1, "input").sendKeys(email);
        findElementIndexByTag(formItens, 2, "input").sendKeys(Data.PASSWORD_SIGNUP);
        findElementIndexByTag(formItens, 3, "input").sendKeys(Data.PASSWORD_SIGNUP);
        findElementIndexByTag(formItens, 4, "input").click();
    }

    private void loginUserAction() {
        WebElement form = driver.findElement(By.tagName("form"));
        List<WebElement> formItens = form.findElements(By.tagName("p"));

        findElementIndexByTag(formItens, 0, "input").sendKeys(Data.USER_LOGIN);
        findElementIndexByTag(formItens, 1, "input").sendKeys(Data.PASSWORD_LOGIN);
        findElementIndexByTag(formItens, 2, "input").click();
    }

    private void getBook(int index) {
        WebElement element = driver.findElement(By.className("product"));
        List<WebElement> childrenElements = element.findElements(By.tagName("a"));
        childrenElements.get(index).click();
    }

    private void addBookAction() {
        getAuthItens().get(3).click();
        loginUserAction();
        getBook(0);
        WebElement bookDescriptionElement = driver.findElement(By.id("product-description"));
        List<WebElement> bookDescriptionElements = bookDescriptionElement.findElements(By.tagName("p"));
        bookDescriptionElements.get(1).findElement(By.tagName("a")).click();
    }

    public void removeBookAction() {
        WebElement table = driver.findElement(By.tagName("tbody"));
        List<WebElement> trs = table.findElements(By.tagName("tr"));
        List<WebElement> tds = trs.get(1).findElements(By.tagName("td"));
        WebElement removeLink = tds.get(2).findElement(By.tagName("a"));
        removeLink.click();
    }

    private List<WebElement> getFlashNotice() {
        return driver.findElements(By.id("flash_notice"));
    }

}