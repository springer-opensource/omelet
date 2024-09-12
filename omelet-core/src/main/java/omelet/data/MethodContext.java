package omelet.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.IDataProvidable;

import omelet.common.Utils;
import omelet.data.DataProvider.mapStrategy;
import omelet.data.driverconf.IBrowserConf;
import omelet.data.driverconf.PrepareDriverConf;
import omelet.data.googlesheet.GoogleSheetConstant;
import omelet.data.googlesheet.ReadGoogle;
import omelet.data.xml.BrowserXmlParser;
import omelet.data.xml.MappingParserRevisit;
import omelet.data.xml.XmlApplicationData;
import omelet.exception.FrameworkException;
import omelet.testng.support.RetryAnalyzer;

public class MethodContext implements IMethodContext {

	private String methodName;
	private Method method;
	private List<IProperty> testData;
	private List<IBrowserConf> browserConfig;
	private mapStrategy runStrategy;
	private IRetryAnalyzer retryAnalyzer;
	private boolean beforeMethod;
	private boolean afterMethod;
	private DataSource dataSource;
	private static final Logger LOGGER = LogManager.getLogger(MethodContext.class);
	private boolean isDataSourceCalculated = false;
	private boolean isEnabled;
	private String[] groups;
	private static MappingParserRevisit mpr = new MappingParserRevisit();
	private static RefineMappedData refinedMappedData = new RefineMappedData(mpr);

	public MethodContext(Method method) {
		this.method = method;
		this.methodName = Utils.getFullMethodName(method);
		setBeforeAfterMethod();
		setIsEnable();
		setGroups();
	}

	private void setIsEnable() {
		if(null != method.getAnnotation(org.testng.annotations.Test.class))
		isEnabled = method.getAnnotation(org.testng.annotations.Test.class)
				.enabled();
		if(null != method.getAnnotation(org.testng.annotations.Factory.class))
			isEnabled = method.getAnnotation(org.testng.annotations.Factory.class)
			.enabled();
	}
	
	public boolean isEnable(){
		return isEnabled;
	}
	
	private void setGroups(){
		if(null != method.getAnnotation(org.testng.annotations.Test.class))
		groups = method.getAnnotation(org.testng.annotations.Test.class).groups();
	}
	
	public String[] getGroup(){
		return groups;
	}

	public void setRetryAnalyser(ITestAnnotation methodAnnotation) {
		if (methodAnnotation.getRetryAnalyzer() == null) {
			methodAnnotation.setRetryAnalyzer(RetryAnalyzer.class);
			retryAnalyzer = methodAnnotation.getRetryAnalyzer();
		} else {
			retryAnalyzer = methodAnnotation.getRetryAnalyzer();
		}
		LOGGER.debug("Setting Retry Analyzer to "
				+ methodAnnotation.getRetryAnalyzer() + " for Method: "
				+ methodName);
	}

	public void setBrowserConf(List<IBrowserConf> browserConfs) {
		this.browserConfig = browserConfs;
	}

	public void setTestData(List<IProperty> testDataL) {
		this.testData = testDataL;
	}

	public void setRunStrategy(mapStrategy runStrategy) {
		this.runStrategy = runStrategy;
	}

	public void setDataProvider(IDataProvidable methodAnnotation,
			Method testMethod) {
		if (testMethod.getGenericParameterTypes().length == 2
				&& testMethod.getGenericParameterTypes()[0]
						.equals(IBrowserConf.class)
				&& testMethod.getGenericParameterTypes()[1]
						.equals(IProperty.class)) {
			verify_UpdateDataProviderName(methodAnnotation, testMethod);
			verify_UpdateDataProviderClass(methodAnnotation, testMethod);
		} else if (testMethod.getGenericParameterTypes().length == 0) {
			dataSource = DataSource.NoSource;
		}
	}

	private void setBeforeAfterMethod() {
		beforeMethod = checkAnnotation(method.getDeclaringClass(),
				org.testng.annotations.BeforeMethod.class);
		afterMethod = checkAnnotation(method.getDeclaringClass(),
				org.testng.annotations.AfterMethod.class);
	}

	private <T extends Annotation> boolean checkAnnotation(
			Class<?> classToCheck, Class<T> annotationToVerify) {
		if (!classToCheck.getName().contains("java.lang.Object")) {
			for (Method method : classToCheck.getMethods()) {
				if (method.getAnnotation(annotationToVerify) != null) {
					return true;
				}
			}
			return checkAnnotation(classToCheck.getSuperclass(),
					annotationToVerify);
		}
		return false;
	}

	/*
	 * TO DO: This one is inefficient, we have to parse the config file for
	 * every method. Try to move it in the above layer, so that parsing happens
	 * only once.
	 */
	private DataSource getDataSourceParameter() {
		if (!isDataSourceCalculated) {
			Map<String, String> tempMap = new HashMap<String, String>();
			PrepareDriverConf configuration = new PrepareDriverConf(tempMap);
			try {
				// no need for CheckforRules as it only adds confusion
				dataSource = DataSource.valueOf(configuration
						.refineBrowserValues().checkForRules().get()
						.getDataSource());
			} catch (Exception exp) {
				throw new FrameworkException(
						"it seems there is no DatSource provided for the testMethod:"
								+ methodName);
				// dataSource = DataSource.Invalid;
			}
			isDataSourceCalculated = true;
		}
		return dataSource;
	}

	@Override
	public List<IProperty> getMethodTestData() {
		return this.testData;
	}

	@Override
	public List<IBrowserConf> getBrowserConf() {
		return browserConfig;
	}

	@Override
	public mapStrategy getRunStrategy() {
		return runStrategy;
	}

	@Override
	public DataSource getDataProvider() {
		return dataSource;
	}

	public IRetryAnalyzer getRetryAnalyzer() {
		return retryAnalyzer;
	}

	/**
	 * update and verify dataProvider name for method which are already having
	 * dataProviderName
	 * 
	 * @param dataProperty
	 */

	private void validateDataProviderName(String dataProperty) {
		if (dataProperty.equalsIgnoreCase(DataSource.GoogleData.toString())) {
			dataSource = DataSource.GoogleData;
		} else if (dataProperty
				.equalsIgnoreCase((DataSource.XmlData.toString()))) {
			dataSource = DataSource.XmlData;
		} else {
			throw new FrameworkException(
					"Please fix the DataProvider name for data provider in method:"
							+ methodName + " which should be"
							+ " either XmlData or GoogleData");
		}
	}

	private void verify_UpdateDataProviderName(IDataProvidable testAnnotation,
			Method testMethod) {
		if (StringUtils.isNotBlank(testAnnotation.getDataProvider())) {
			validateDataProviderName(testAnnotation.getDataProvider());
		} else {

			testAnnotation.setDataProvider(getDataSourceParameter().name());
			dataSource = getDataSourceParameter();
			LOGGER.debug("Setting Data provider for method: "
					+ testMethod.getName() + " value: "
					+ testAnnotation.getDataProvider());
		}
	}
	
	

	private void verify_UpdateDataProviderClass(IDataProvidable testAnnotation,
			Method testMethod) {

		if (testAnnotation.getDataProviderClass() != null
				&& StringUtils.isNotBlank(testAnnotation.getDataProviderClass()
						.toString())) {
			if (!testAnnotation.getDataProviderClass().equals(
					omelet.data.DataProvider.class)) {
				throw new FrameworkException(
						"please fix data provider class for method:"
								+ testMethod.getName());
			}
		} else {
			testAnnotation.setDataProviderClass(omelet.data.DataProvider.class);
			LOGGER.debug("Setting Data provider class for method: "
					+ testMethod.getName() + " value "
					+ testAnnotation.getDataProviderClass().getName());
		}
	}

	@Override
	public boolean isAfterMethod() {
		return afterMethod;
	}

	@Override
	public boolean isBeforeMethod() {
		return beforeMethod;
	}

	private void updateXml(String environment) {
		IMappingData mapD = refinedMappedData.getMethodData(method);
		if (environment != null && !StringUtils.isBlank(environment)) {
			// get the xml name from MappingParser Static Method
			this.testData = XmlApplicationData.getInstance().getAppData(mapD.getTestData(),environment);
		} else {
			this.testData = XmlApplicationData.getInstance().getAppData(mapD.getTestData());
		}
		BrowserXmlParser bxp = BrowserXmlParser.getInstance();
		//BrowserXmlParser bxp = new BrowserXmlParser(mapD.getClientEnvironment());
		this.browserConfig = bxp.getBrowserConf1(mapD);
		this.runStrategy = mapD.getRunStartegy();
	}

	private void updateGoogleSheet(String environment) {
		checkGoogleUserAccountIdAndP12Key();
		ReadGoogle readGoogle = ReadGoogle.getInstance();
		readGoogle.connect(System.getProperty(GoogleSheetConstant.SERVICE_ACCOUNT_ID),System.getProperty(GoogleSheetConstant.GOOGLE_ACCOUNT_P12_FILE_PATH),
				System.getProperty(GoogleSheetConstant.GOOGLESHEETNAME));
		RefineMappedData refinedData = new RefineMappedData(readGoogle);
		IMappingData mapData = refinedData.getMethodData(method);
		this.browserConfig = readGoogle.getBrowserListForSheet(mapData);
		this.testData = readGoogle.getMethodData(environment, mapData);
		this.runStrategy = mapData.getRunStartegy();

	}

	private void checkGoogleUserAccountIdAndP12Key() {
		if (StringUtils.isBlank(System
				.getProperty(GoogleSheetConstant.SERVICE_ACCOUNT_ID))
				&& StringUtils.isBlank(System
						.getProperty(GoogleSheetConstant.GOOGLE_ACCOUNT_P12_FILE_PATH))
				&& StringUtils.isBlank(System
						.getProperty(GoogleSheetConstant.GOOGLESHEETNAME))) {
			// This is not the solution as TestNG is not logging the exception
			// hence setting it here
			LOGGER.info("Method with name:"
					+ methodName
					+ "required Google Sheet as Test Data , please provide arguments -DserviceAccountId and -DpathOfGoogleP12File");
			throw new FrameworkException(
					"Method with name:"
							+ methodName
							+ "required Google Sheet as Test Data , please provide arguments -DserviceAccountId and -DpathOfGoogleP12File");
		}
	}

	/**
	 * Prepare browserConf and IProperty for the test method
	 */
	public void prepareData() {
		if (isEnabled) {
			switch (dataSource) {
			case XmlData:
				updateXml(System.getProperty("env-type"));
				break;
			case GoogleData:
				updateGoogleSheet(System.getProperty("env-type"));
				break;
			case NoSource:
				// ignore no need to set data
				break;
			default:
				break;
			}
		}else{
			LOGGER.debug("As the method:"+methodName+" is not enabled no need to set data");
		}
	}

}
