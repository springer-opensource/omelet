package omelet.data.googlesheet;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import omelet.data.IDataSource;
import omelet.data.IMappingData;
import omelet.data.IProperty;
import omelet.data.ImplementIMap;
import omelet.data.PropertyMapping;
import omelet.data.driverconf.IBrowserConf;
import omelet.data.driverconf.PrepareDriverConf;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

public class ReadGoogle implements IDataSource {

    private String googleAccountId;
    private String googleFilePathOfP12Key;
    private SpreadsheetEntry spreadSheet;
    private SpreadsheetService service = null;
    private String sheetName = null;
    private static final Logger LOGGER = Logger.getLogger(ReadGoogle.class);
    private static Map<String, List<IBrowserConf>> browserBucket = new HashMap<String, List<IBrowserConf>>();
    private static Map<String, List<IProperty>> dataBucket = new HashMap<String, List<IProperty>>();
    private static Map<String, IMappingData> mappingBucket = new HashMap<String, IMappingData>();
    private static ReadGoogle instance = null;
    private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static Set<String> SCOPES = SheetsScopes.all();

    private ReadGoogle() {
        /*
         * this.googleUserName = googleUserName; this.googlePasswd =
		 * googlePasswd; this.sheetName = sheetName; spreadSheet = connect();
		 */
    }

    public static ReadGoogle getInstance() {
        if (null == instance) {
            synchronized (ReadGoogle.class) {
                if (null == instance) {
                    instance = new ReadGoogle();
                }
            }
        }
        return instance;
    }

    /**
     * Call this method to connect with desired Google Sheet
     *
     * @param googleAccountId string
     * @param googleFilePathOfP12Key   string
     * @param googleSheetKey      string
     * @return this
     */

    public Credential authorize() {
        Credential credential = new GoogleCredential.Builder().build();
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId(googleAccountId)
                    .setServiceAccountPrivateKeyFromP12File(new File(googleFilePathOfP12Key))
                    .setServiceAccountScopes(SheetsScopes.all())
                    .build();
            return credential;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return credential;
    }

    public ReadGoogle connect(String googleAccountId, String pathToP12Key,
                              String sheetName) {
        this.googleAccountId = googleAccountId;
        this.googleFilePathOfP12Key = pathToP12Key;
        this.sheetName = sheetName;
        spreadSheet = connect();
        return this;
    }

    private SpreadsheetEntry connect() {
        service = new SpreadsheetService("Omlete");
        try {
            service.setOAuth2Credentials(authorize());
            URL spreadSheetFeedUrl = new URL(
                    "https://spreadsheets.google.com/feeds/spreadsheets/private/full");
            SpreadsheetFeed feed = service.getFeed(spreadSheetFeedUrl,
                    SpreadsheetFeed.class);
            List<com.google.gdata.data.spreadsheet.SpreadsheetEntry> spreadsheets = feed
                    .getEntries();
            for (SpreadsheetEntry sheet : spreadsheets) {
                LOGGER.debug("Sheet title plain text: "
                        + sheet.getTitle().getPlainText());
                if (sheet.getTitle().getPlainText().equalsIgnoreCase(sheetName)) {
                    return sheet;
                }
            }
        } catch (AuthenticationException e) {
            LOGGER.error(e);
        } catch (MalformedURLException e) {
            LOGGER.error(e);
        } catch (IOException e) {
            LOGGER.error(e);
        } catch (ServiceException e) {
            LOGGER.error(e);
        }
        return null;

    }

    private WorksheetEntry getWorkSheet(String sheetName) throws IOException,
            ServiceException {
        for (WorksheetEntry workSheet : spreadSheet.getWorksheets()) {
            if (workSheet.getTitle().getPlainText().equalsIgnoreCase(sheetName)) {
                return workSheet;
            }
        }
        return null;
    }

    /***
     * Get the primary data from the Mapping sheet in Google sheet, This would
     * be fed to MappedValue for refinement
     */
    @Override
    public Map<String, IMappingData> getPrimaryData() {
        if (mappingBucket.isEmpty()) {
            URL listFeedURL;
            try {
                listFeedURL = getWorkSheet(
                        GoogleSheetConstant.GOOGLE_MAP_SHEET_NAME)
                        .getListFeedUrl();
                ListFeed listFeed = service
                        .getFeed(listFeedURL, ListFeed.class);
                for (ListEntry row : listFeed.getEntries()) {
                    mappingBucket.put(
                            row.getCustomElements().getValue("methodname"),
                            getMap(row));
                }
            } catch (IOException e) {
                LOGGER.error(e);
            } catch (ServiceException e) {
                LOGGER.error(e);
            }
        }
        return mappingBucket;
    }

    /**
     * get ImplementMap frmo the Mapping sheet for single row
     *
     * @param row
     * @return
     */
    private ImplementIMap getMap(ListEntry row) {
        LOGGER.debug("In preparing the Map:");
        return new ImplementIMap.Builder()
                .withClientEnvironment(
                        getList(row.getCustomElements()
                                .getValue("browsersheet")))
                .withTestData(row.getCustomElements().getValue("testdatasheet"))
                .withRunStartegy(
                        row.getCustomElements().getValue("runstrategy"))
                .build();
    }

    /**
     * Helper method to get List from "," seprated strings
     *
     * @param commaSepratedList
     * @return
     */
    private List<String> getList(String commaSepratedList) {

        List<String> returnedList = new ArrayList<String>();
        if (StringUtils.isNotBlank(commaSepratedList)) {
            if (commaSepratedList
                    .contains(GoogleSheetConstant.GOOGLE_BROWSERSHEET_DELIMITER)) {
                String array[] = commaSepratedList
                        .split(GoogleSheetConstant.GOOGLE_BROWSERSHEET_DELIMITER);
                Collections.addAll(returnedList, array);
            } else {
                returnedList.add(commaSepratedList);
            }
        }
        return returnedList;
    }

    /***
     * Get the map having key as methodName of {@link IBrowserConf} for the
     * refined data
     *
     * @param data
     *  IMappingData
     * @return list
     */
    public List<IBrowserConf> getBrowserListForSheet(IMappingData data) {
        // Preferabbly send refined list to it
        List<IBrowserConf> returnList = new ArrayList<IBrowserConf>();

        URL browserSheetURL;
        String sheetNameHolder;
        ListFeed browserFeed;

        // get the browser sheet name
        for (String browserSheet : data.getClientEnvironment()) {
            sheetNameHolder = browserSheet;
            if (!browserBucket.containsKey(browserSheet)) {
                try {
                    List<IBrowserConf> browserConfLForSingleSheet = new ArrayList<IBrowserConf>();
                    browserSheetURL = getWorkSheet(browserSheet)
                            .getListFeedUrl();
                    browserFeed = service.getFeed(browserSheetURL,
                            ListFeed.class);
                    for (ListEntry row : browserFeed.getEntries()) {
                        browserConfLForSingleSheet
                                .add(getBrowserConfFromRow(row));
                    }
                    browserBucket.put(browserSheet, browserConfLForSingleSheet);
                } catch (NullPointerException ex) {
                    LOGGER.error("Not able to find sheet:" + sheetNameHolder);
                    LOGGER.error(ex);
                } catch (IOException e) {
                    LOGGER.error(e);
                } catch (ServiceException e) {
                    LOGGER.error(e);
                }
            }
            returnList.addAll(browserBucket.get(browserSheet));
        }

        return returnList;
    }

    /**
     * Get single Entry for {@link IBrowserConf} for single row in the Google
     * sheet
     *
     * @param row
     * @return
     */
    private IBrowserConf getBrowserConfFromRow(ListEntry row) {
        Map<String, String> browserMap = new HashMap<String, String>();
        for (String tag : row.getCustomElements().getTags()) {
            browserMap.put(tag, row.getCustomElements().getValue(tag));
        }
        return new PrepareDriverConf(browserMap).refineBrowserValues()
                .checkForRules().get();
    }

    /**
     * get the map with key as methodName and List of {@link IProperty}
     *
     * @param environment : which should be System.getProperty() it can be null or empty
     *                    as well
     * @param data
     * @return list
     */
    public List<IProperty> getMethodData(String environment, IMappingData data) {
        if (!dataBucket.containsKey(data.getTestData())) {
            URL testDataSheetURL;
            ListFeed testDataFeed = null;
            // Here reading the method name and the WorkSheetName
            LOGGER.debug("Get testdata: " + data.getTestData());
            try {
                testDataSheetURL = getWorkSheet(data.getTestData())
                        .getListFeedUrl();
                testDataFeed = service
                        .getFeed(testDataSheetURL, ListFeed.class);
            } catch (IOException e) {
                LOGGER.error(e);
            } catch (ServiceException e) {
                LOGGER.error(e);
            }
            dataBucket.put(data.getTestData(), getSingleMethodtData(environment, testDataFeed));
        }

        return dataBucket.get(data.getTestData());
    }

    /**
     * return the property list for single methods
     *
     * @param env
     * @param rows
     * @return
     */
    private List<IProperty> getSingleMethodtData(String env, ListFeed rows) {
        List<DataPerEnvironment> testEnvironmentMap = new ArrayList<ReadGoogle.DataPerEnvironment>();
        Map<String, String> keyValuePair = new HashMap<String, String>();
        DataPerEnvironment testEnvHolder = null;
        for (ListEntry row : rows.getEntries()) {
            LOGGER.debug("Row is: " + row.getCustomElements().getValue("key")
                    + "value: " + row.getCustomElements().getValue("value"));
            if (row.getCustomElements().getValue("key").contains("Environment")) {
                if (testEnvHolder != null) {
                    testEnvironmentMap.add(testEnvHolder);
                }
                testEnvHolder = new DataPerEnvironment(row.getCustomElements()
                        .getValue("value"));
                /*
                 * if (!keyValuePair.isEmpty()) { testEnvironmentMap.add(new
				 * TestEnvironmentMap(row
				 * .getCustomElements().getValue("value"), new
				 * PropertyMapping(keyValuePair))); keyValuePair = new
				 * HashMap<String, String>(); }
				 */
            } else {
                testEnvHolder.setTestData(
                        row.getCustomElements().getValue("key"), row
                                .getCustomElements().getValue("value"));
                keyValuePair.put(row.getCustomElements().getValue("key"), row
                        .getCustomElements().getValue("value"));
            }

        }
        // update the last entry in the sheet
        testEnvironmentMap.add(testEnvHolder);
        // check if environment was present
        if (StringUtils.isNotBlank(env)) {
            return getConcatenatedDataList(env, testEnvironmentMap);
        } else {
            return getConcatentedDataList(testEnvironmentMap);
        }
    }

    /**
     * Returns list of {@link IProperty} for particular env
     *
     * @param environment : Name of the environment like staging , live etc
     * @param testEnvList :TestEnvironment list prepared prepared by reading the Google
     *                    sheet from top to bottom
     * @return
     */
    private List<IProperty> getConcatenatedDataList(String environment,
                                                    List<DataPerEnvironment> testEnvList) {
        List<IProperty> filteredEnvIPropList = new ArrayList<IProperty>();
        for (DataPerEnvironment testEnv : testEnvList) {
            if (testEnv.getEnvName().equalsIgnoreCase(environment)) {
                filteredEnvIPropList.add(testEnv.getTestData());
            }
        }
        return filteredEnvIPropList;
    }

    /**
     * Return list of {@link IProperty} for all the environments mentioned in
     * the Google sheet
     *
     * @param testEnvList
     * @return
     */
    private List<IProperty> getConcatentedDataList(
            List<DataPerEnvironment> testEnvList) {
        List<IProperty> fullEnvList = new ArrayList<IProperty>();
        for (DataPerEnvironment testMap : testEnvList) {
            fullEnvList.add(testMap.getTestData());
        }
        return fullEnvList;
    }

    /**
     * Hold the test data for the particular environment
     *
     * @author kapil
     */
    private class DataPerEnvironment {
        String environmentName;
        IProperty prop;
        Map<String, String> testDataForSingelEnvEntry = new HashMap<String, String>();

        public DataPerEnvironment(String environmentName) {
            LOGGER.debug("Environment for TestEnv: " + environmentName);
            this.environmentName = environmentName;
        }

        public String getEnvName() {
            return environmentName;
        }

        public void setTestData(String key, String value) {
            testDataForSingelEnvEntry.put(key, value);
        }

        public IProperty getTestData() {
            prop = new PropertyMapping(testDataForSingelEnvEntry);
            return prop;
        }
    }
}
