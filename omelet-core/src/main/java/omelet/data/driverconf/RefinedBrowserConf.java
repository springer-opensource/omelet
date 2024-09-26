package omelet.data.driverconf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import omelet.common.Utils;
import omelet.data.PropertyValueMin;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Refine Browser data based on the Hierarchy
 * default -- Framework -- ClientEnv -- CommandLine
 * 
 * @author kapil
 * 
 */
public class RefinedBrowserConf {

	private final Map<String, String> clientBrowserData;
	private boolean isFrameworkProperties;
	private PropertyValueMin frameworkPropData = null;
	private static final Logger LOGGER = Logger.getLogger(RefinedBrowserConf.class);
	private final String fileName;

	public RefinedBrowserConf(Map<String, String> clientBrowserData,
			String standBylookUpFileName) {
		this.clientBrowserData = clientBrowserData;
		this.fileName = standBylookUpFileName;
		setFrameworkProperties(standBylookUpFileName);

	}

	private void setFrameworkProperties(String standBylookUpFileName) {
		String pathOfFile = Utils.getResources(RefinedBrowserConf.class,
				standBylookUpFileName);
		if (null == pathOfFile) {
			isFrameworkProperties = false;
		} else {
			isFrameworkProperties = true;
			frameworkPropData = new PropertyValueMin(Utils.getResources(this,
					standBylookUpFileName));
		}
	}

	public String get(String key, String defaultValue) {
		String refinedValue = defaultValue;
		refinedValue = getFromFrameworkProp(key, refinedValue);
		refinedValue = getFromClientEnv(key, refinedValue);
		refinedValue = getFromJvmArgs(key, refinedValue);
		return refinedValue;
	}

	private String getFromFrameworkProp(String key, String value) {
		if (isFrameworkProperties) {
			String tempValue = frameworkPropData.getValue(key);
			if (isNotBlank(tempValue)) {
				return tempValue;
			}
		}
		return value;
	}

	public DesiredCapabilities getDesiredCapabilities() {
		DesiredCapabilities dc = new DesiredCapabilities();
		if(isFrameworkProperties){
			dc.merge(getDCFrameworkProp());
		}
		dc.merge(getDCClient());
		dc.merge(getDCJvm());
		return dc;
	}
	
	private DesiredCapabilities getDCFrameworkProp(){
		if(isFrameworkProperties){
			if (isFrameworkProperties) {
				Properties prop = new Properties();
				try {
					prop.load(new FileInputStream(new File(Utils.getResources(this,
							fileName))));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					LOGGER.error(e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOGGER.error(e);
				}
				PrepareDesiredCapability pdcFrameworkProperties = new PrepareDesiredCapability(
						prop);
				return pdcFrameworkProperties.get();
			}
		}
		return null;
	}
	
	private DesiredCapabilities getDCJvm(){
		PrepareDesiredCapability systemCapa = new PrepareDesiredCapability(
				System.getProperties());
		return systemCapa.get();
	}
	
	private DesiredCapabilities getDCClient(){
		PrepareDesiredCapability clientBrowserCap = new PrepareDesiredCapability(
				clientBrowserData);
		return clientBrowserCap.get();
	}

	private String getFromClientEnv(String key, String value) {
		String tempValue = clientBrowserData.get(key);
		if (isNotBlank(tempValue)) {
			return tempValue;
		}
		return value;
	}

	private boolean isNotBlank(String value) {
		return StringUtils.isNotBlank(value);
	}

	private String getFromJvmArgs(String key, String value) {
		String tempValue = System.getProperty(key);
		return isNotBlank(tempValue) ? tempValue : value;
	}

}
