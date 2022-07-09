package ui;

import managers.BrowserController;
import managers.FunctionBufferedWriter;
import models.Game;
import org.apache.commons.lang.StringEscapeUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GamePriceGrabberStarter {
    private static Logger logger = LoggerFactory.getLogger(GamePriceGrabberStarter.class);
    public static final String DELIMITER = ",";
    public static final int SLEEP_EACH_DETAIL_PAGE = 400;
    public static final String FILE_DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss";
    public static final String RAWDATA_FOLDER = "rawdata/";

    public static void main(String[] args) throws InterruptedException {

        BrowserController.use(((driver, wait) -> {

            logger.debug("start function");
            SimpleDateFormat sdf = new SimpleDateFormat(FILE_DATE_FORMAT);

            Date date = new Date();
            String currDateString = sdf.format(date);

            FunctionBufferedWriter.use(RAWDATA_FOLDER + currDateString + ".csv", fileWriter -> {
                logger.debug("start writer");
                int page = 1;
                int breakAtPage = Integer.MAX_VALUE;
                String mainWindowHanlde = driver.getWindowHandle();
                while (true) { // each page
                    logger.debug("====================Page: {}", page);
                    if (page == breakAtPage) {
                        logger.debug("Its page {} , break", breakAtPage);
                        break;
                    }

                    driver.switchTo().window(mainWindowHanlde);
                    String listUrl = "https://store.nintendo.com.hk/digital-games/current-offers?p=" + page + "&product_list_limit=24";
                    logger.debug("going url:{}", listUrl);
                    driver.get(listUrl);

                    String productLinkClassName = "product-item-link";
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(productLinkClassName)));
                    List<WebElement> currPageElements = driver.findElements(By.className(productLinkClassName));

                    if (currPageElements.isEmpty()) { //no more products
                        break;
                    }

                    List<Game> tempGameList = new ArrayList();

                    //get data in list page and put data to list first
                    getDataFromListPage(currPageElements, tempGameList);

                    //for each in list, goto detail page
                    goToDetailPages(driver, tempGameList);

                    getDataFromDetailPages(driver, wait, fileWriter, tempGameList);

                    page++;
                }

                logger.debug("end writer");
            }); // end file writer


            logger.debug("End function");
        }));

    }

    private static void getDataFromDetailPages(WebDriver driver, WebDriverWait wait, BufferedWriter fileWriter, List<Game> tempGameList) throws IOException {
        for (Game g : tempGameList) {
            driver.switchTo().window(g.getWindowHandle());
            List<WebElement> playerElementCheck = driver.findElements(By.className("o_p-product-detail-players"));
            List<WebElement> priceElementCheck = driver.findElements(By.className("o_p-product-detail__price--price"));
            List<WebElement> fixPriceElementCheck = driver.findElements(By.className("o_p-product-detail__fixed-price"));
            //o_c-button-fill
            if (playerElementCheck.isEmpty() && priceElementCheck.isEmpty() && fixPriceElementCheck.isEmpty()) {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("o_c-button-fill")));
                WebElement button = driver.findElement(By.className("o_c-button-fill"));
                button.click();
            }

            String localPlayerStringValue = "";
            int minPlayer = 0;
            int maxPlayer = 0;
            try {
                WebElement localPlayerElement = driver.findElement(By.className("o_p-product-detail-players"));
                WebElement localPlayerValueElement = localPlayerElement.findElement(By.className("o_c-2col-list-border__right"));
                localPlayerStringValue = localPlayerValueElement.getText();
                String[] localPlayerStringArr = localPlayerStringValue
                        .replace(" ", "")
                        .replace("×", "")
                        .split("～");


                if (localPlayerStringArr.length > 0) {
                    minPlayer = Integer.parseInt(localPlayerStringArr[0]);
                }

                if (localPlayerStringArr.length > 1) {
                    maxPlayer = Integer.parseInt(localPlayerStringArr[1]);
                } else {
                    maxPlayer = minPlayer;
                }


            } catch (NoSuchElementException e) {
                logger.debug("No such element for class:" + "o_c-2col-list-border__right");
            }
            String oldPrice = getTextFromOneClass(driver, "o_p-product-detail__fixed-price", "");
            String currentPrice = getTextFromOneClass(driver, "o_p-product-detail__price--price", "");

            String gameName = g.getName();
            if (gameName == null || gameName.isEmpty()) {
                gameName = getTextFromOneClass(driver, "o_c-page-title", "");
            }

            logger.debug(oldPrice);
            logger.debug(currentPrice);
            fileWriter.write(StringEscapeUtils.escapeCsv(gameName) + DELIMITER + g.getUrl() + DELIMITER + minPlayer + DELIMITER + maxPlayer + DELIMITER + oldPrice + DELIMITER + currentPrice);
            fileWriter.newLine();
            fileWriter.flush();

            driver.close();
        }
    }

    private static void getDataFromListPage(List<WebElement> currPageElements, List<Game> tempGameList) {
        for (WebElement e : currPageElements) { //each game
            String gameName = e.getText();
            String gameDetailUrl = e.getAttribute("href");
            logger.debug(gameName);
            logger.debug(gameDetailUrl);
            Game tempGame = new Game();
            tempGame.setName(gameName);
            tempGame.setUrl(gameDetailUrl);
            tempGameList.add(tempGame);
        }
    }

    private static void goToDetailPages(WebDriver driver, List<Game> tempGameList) throws InterruptedException {
        for (Game g : tempGameList) {
            //get detail
            driver.switchTo().newWindow(WindowType.TAB);
            driver.get(g.getUrl());
            g.setWindowHandle(driver.getWindowHandle());
            Thread.sleep(SLEEP_EACH_DETAIL_PAGE);
        }
    }

    private static String getTextFromOneClass(WebDriver driver, String className, String defaultVal) {
        String oldPrice = defaultVal;
        try {
            WebElement oldPriceElement = driver.findElement(By.className(className));
            oldPrice = oldPriceElement.getText();
        } catch (NoSuchElementException e) {
            logger.debug("No such element for class:{}", className);
        }
        return oldPrice;
    }
}
