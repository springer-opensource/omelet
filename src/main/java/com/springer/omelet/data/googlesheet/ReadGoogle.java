package com.springer.omelet.data.googlesheet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.springer.omelet.data.BrowserConfiguration;
import com.springer.omelet.data.BrowserConstant;
import com.springer.omelet.data.IBrowserConf;
import com.springer.omelet.data.IDataSource;
import com.springer.omelet.data.IMappingData;
import com.springer.omelet.data.IProperty;
import com.springer.omelet.data.ImplementIMap;
import com.springer.omelet.data.PropertyMapping;

public class ReadGoogle implements IDataSource {

	private String googleUserName;
	private String googlePasswd;
	private static final String MAP_SHEET_NAME = "Mapping";
	private static final String DELIMITTER = ";";
	private URL SPREADSHEET_FEED_URL = null;
	private SpreadsheetEntry spreadSheet;
	private SpreadsheetService service = null;
	private String sheetName = null;
	private static final Logger LOGGER = Logger.getLogger(ReadGoogle.class);

	public ReadGoogle(String googleUserName, String googlePasswd,
			String sheetName) {
		this.googleUserName = googleUserName;
		this.googlePasswd = googlePasswd;
		this.sheetName = sheetName;
		spreadSheet = connect();
	}

	private SpreadsheetEntry connect() {
		service = new SpreadsheetService("MySpreadsheetIntegration-v1");
		try {
			service.setUserCredentials(googleUserName, googlePasswd);
			SPREADSHEET_FEED_URL = new URL(
					"https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
					SpreadsheetFeed.class);
			List<com.google.gdata.data.spreadsheet.SpreadsheetEntry> spreadsheets = feed
					.getEntries();
			for (SpreadsheetEntry sheet : spreadsheets) {
			//	System.out.println(sheet.getTitle().getPlainText());
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
	 * Get the primary data from the Mapping sheet in Google sheet,
	 * This would be fed to MappedValue for refinement
	 */
	@Override
	public Map<String, IMappingData> getPrimaryData() {
		Map<String, IMappingData> returnedMap = new HashMap<String, IMappingData>();
		URL listFeedURL;
		try {
			listFeedURL = getWorkSheet(MAP_SHEET_NAME).getListFeedUrl();
			ListFeed listFeed = service.getFeed(listFeedURL, ListFeed.class);
			for (ListEntry row : listFeed.getEntries()) {
				/*System.out.println(row.getCustomElements().getValue(
						"methodname"));
				System.out.println(row.getCustomElements().getValue(
						"methodnameadsasd"));*/
				returnedMap.put(row.getCustomElements().getValue("methodname"),
						getMap(row));
			}
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (ServiceException e) {
			LOGGER.error(e);
		}

		return returnedMap;
	}

	/**
	 * get ImplementMap frmo the Mapping sheet for single row
	 * @param row
	 * @return
	 */
	private ImplementIMap getMap(ListEntry row) {
		/*System.out.println("In preparing the Map:");
		System.out.println();*/
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
			if (commaSepratedList.contains(DELIMITTER)) {
				String array[] = commaSepratedList.split(DELIMITTER);
				for (int i = 0; i < array.length; i++)
					returnedList.add(array[i]);
			} else {
				returnedList.add(commaSepratedList);
			}
		}
		return returnedList;
	}

	/***
	 * Get the map having key as methodName of {@link IBrowserConf} for the refined data
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public List<IBrowserConf> getBrowserListForSheet(
			IMappingData data)  {
		// Preferabbly send refined list to it
		IMappingData methodData = data;
		List<IBrowserConf> browserConfList = new ArrayList<IBrowserConf>();
		URL browserSheetURL;
		String sheetNameHolder = null;
		ListFeed browserFeed;

			// get the browser sheet name
			for (String browserSheet : methodData.getClientEnvironment()) {
				sheetNameHolder = browserSheet;
				try {
					browserSheetURL = getWorkSheet(browserSheet)
							.getListFeedUrl();
					browserFeed = service.getFeed(browserSheetURL,
							ListFeed.class);
					for (ListEntry row : browserFeed.getEntries()) {
						browserConfList.add(getBrowserConfFromRow(row));

					}
				} catch (NullPointerException ex) {
					System.out.println("Not able to find sheet:"
							+ sheetNameHolder);
					LOGGER.error(ex);
				} catch (IOException e) {
					LOGGER.error(e);
				} catch (ServiceException e) {
					LOGGER.error(e);
				}
			}
		return browserConfList;
	}

	/**
	 * Get single Entry for {@link IBrowserConf} for single row in the Google sheet
	 * @param row
	 * @return
	 */
	private IBrowserConf getBrowserConfFromRow(ListEntry row) {
		Map<String, String> browserMap = new HashMap<String, String>();
		for (BrowserConstant colValue : BrowserConstant.values()) {
			browserMap.put(colValue.toString(), row.getCustomElements()
					.getValue(colValue.toString().toLowerCase().replace("_", "")));
		}
		return new BrowserConfiguration(browserMap);
	}

	/**
	 * get the map with key as methodName and List of {@link IProperty}
	 * 
	 * @param environment
	 *            : which should be System.getProperty() it can be null or empty
	 *            as well
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public List<IProperty> getMethodData(String environment,
			IMappingData data) {
		IMappingData mData = data;
	
		URL testDataSheetURL;
		ListFeed testDataFeed = null;
		// Here reading the method name and the WorkSheetName
//			System.out.println(mData.getTestData());
			try {
				testDataSheetURL = getWorkSheet(mData.getTestData())
						.getListFeedUrl();
				testDataFeed = service.getFeed(testDataSheetURL, ListFeed.class);
			} catch (IOException e) {
				LOGGER.error(e);
			} catch (ServiceException e) {
				LOGGER.error(e);
			}
			
			return getSingleMethodtData(environment, testDataFeed);
	}

	/**
	 * return the property list for single methods
	 * @param env
	 * @param rows
	 * @return
	 */
	private List<IProperty> getSingleMethodtData(String env, ListFeed rows) {
		List<TestEnvironmentMap> testEnvironmentMap = new ArrayList<ReadGoogle.TestEnvironmentMap>();
		Map<String, String> keyValuePair = new HashMap<String, String>();
		TestEnvironmentMap testEnvHolder = null;
		for (ListEntry row : rows.getEntries()) {
		/*	System.out.println("Row is:"
					+ row.getCustomElements().getValue("key") + "value:"
					+ row.getCustomElements().getValue("value"));
*/
			if (row.getCustomElements().getValue("key").contains("Environment")) {
				if (testEnvHolder != null) {
					testEnvironmentMap.add(testEnvHolder);
				}
				testEnvHolder = new TestEnvironmentMap(row.getCustomElements()
						.getValue("value"));
				/*
				 * if (!keyValuePair.isEmpty()) { testEnvironmentMap.add(new
				 * TestEnvironmentMap(row
				 * .getCustomElements().getValue("value"), new
				 * PropertyMapping(keyValuePair))); keyValuePair = new
				 * HashMap<String, String>(); }
				 */
			} else {
				testEnvHolder.setProperty(
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
			return getConcatentedList(env, testEnvironmentMap);
		} else {
			return getConcatentedList(testEnvironmentMap);
		}
	}

	/**
	 * Returns list of {@link IProperty} for particular env
	 * 
	 * @param environment
	 *            : Name of the environment like staging , live etc
	 * @param testEnvList
	 *            :TestEnvironment list prepared prepared by reading the Google
	 *            sheet from top to bottom
	 * @return
	 */
	private List<IProperty> getConcatentedList(String environment,
			List<TestEnvironmentMap> testEnvList) {
		List<IProperty> filteredEnvIPropList = new ArrayList<IProperty>();
		for (TestEnvironmentMap testEnv : testEnvList) {
			if (testEnv.getEnvironment().equalsIgnoreCase(environment)) {
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
	private List<IProperty> getConcatentedList(
			List<TestEnvironmentMap> testEnvList) {
		List<IProperty> fullEnvList = new ArrayList<IProperty>();
		for (TestEnvironmentMap testMap : testEnvList) {
			fullEnvList.add(testMap.getTestData());
		}
		return fullEnvList;
	}

	/**
	 * Hold the test data for the particular environment 
	 * @author kapil
	 *
	 */
	private class TestEnvironmentMap {
		String environmentName;
		IProperty prop;
		Map<String, String> testDataForSingelEnvEntry = new HashMap<String, String>();

		public TestEnvironmentMap(String environmentName) {
//			System.out.println("Environment for TestENvu:" + environmentName);
			/* System.out.println("Prop value:"+prop.getValue("Key1")); */
			this.environmentName = environmentName;
		}

		public String getEnvironment() {
			return environmentName;
		}

		public void setProperty(String key, String value) {
			testDataForSingelEnvEntry.put(key, value);
		}

		public IProperty getTestData() {
			prop = new PropertyMapping(testDataForSingelEnvEntry);
			return prop;
		}
	}

	/*
	 * private List<IBrowserConf> getBrowserConf(String workSheetName) throws
	 * IOException, ServiceException{ URL browserListFeedURL; browserListFeedURL
	 * = getWorkSheet(workSheetName).getListFeedUrl(); ListFeed browserListFeed
	 * = service.getFeed(browserListFeedURL, ListFeed.class); for(ListEntry
	 * row:browserListFeed.getEntries()){
	 * row.getCustomElements().getValue(columnHeader) } return null;
	 * 
	 * 
	 * }
	 */
}
