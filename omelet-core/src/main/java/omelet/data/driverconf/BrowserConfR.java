package omelet.data.driverconf;

import java.util.Map;

import org.openqa.selenium.MutableCapabilities;

import omelet.data.DriverConfigurations;

public class BrowserConfR implements IBrowserConf {

	private Map<String, String> mappedValues;
	private MutableCapabilities mutableCapabilities;

	public BrowserConfR(Map<String, String> completeBrowserMap) {
		this(completeBrowserMap,new MutableCapabilities());
	}
	
	public BrowserConfR(Map<String, String> completeBrowserMap,MutableCapabilities dc){
		mappedValues = completeBrowserMap;
		this.mutableCapabilities = dc;
	}

	public String getBrowser() {
		return mappedValues
				.get(DriverConfigurations.LocalEnvironmentConfig.browsername
						.toString());
	}


	public String getuserName() {
		return mappedValues
				.get(DriverConfigurations.CloudConfig.username
						.toString());
	}

	public String getKey() {
		return mappedValues.get(DriverConfigurations.CloudConfig.key
				.toString());
	}

	public boolean isRemoteFlag() {
		return Boolean
				.valueOf(mappedValues
						.get(DriverConfigurations.FrameworkConfig.remoteflag
								.toString()));
	}

	public String getRemoteURL() {
		return mappedValues.get(DriverConfigurations.HubConfig.host
				.toString());
	}

	public Integer getDriverTimeOut() {
		return Integer.valueOf(mappedValues
				.get(DriverConfigurations.FrameworkConfig.drivertimeOut
						.toString()));
	}

	public Integer getRetryFailedTestCaseCount() {
		return Integer.valueOf(mappedValues
				.get(DriverConfigurations.FrameworkConfig.retryfailedtestcase
						.toString()));
	}


	/*public List<String> getBsURLs() {
		List<String> bsURLs = new ArrayList<String>();
		if (bsURLs.isEmpty()) {
			String url = mappedValues
					.get(DriverConfigurations.CloudConfig.bs_urls
							.toString());
			if (url.contains(";")) {
				String[] urls = url.split(";");
				for (int i = 0; i < urls.length; i++) {
					bsURLs.add(urls[i].trim());
				}
			} else {
				bsURLs.add(url);
			}
		}
		return bsURLs;
	}
*/

	public String getLocalIEServerPath() {
		return mappedValues
				.get(DriverConfigurations.LocalEnvironmentConfig.iedriverpath
						.toString());
	}

	public String getLocalChromeServerPath() {
		return mappedValues
				.get(DriverConfigurations.LocalEnvironmentConfig.chromedriverpath
						.toString());
	}
	
	
	public String getLocalPhantomServerPath() {
		return  mappedValues
				.get(DriverConfigurations.LocalEnvironmentConfig.phantomdriverpath
						.toString());
	}
	public String getLocalFirefoxServerPath() {
		return  mappedValues
				.get(DriverConfigurations.LocalEnvironmentConfig.firefoxdriverpath
						.toString());
	}

	public boolean isHighLightElementFlag() {
		return Boolean.valueOf(mappedValues
				.get(DriverConfigurations.FrameworkConfig.highlightelementflag
						.toString()));
	}

	public boolean isScreenShotFlag() {
		return Boolean.valueOf(mappedValues
				.get(DriverConfigurations.FrameworkConfig.screenshotflag
						.toString()));
	}

	

	/***
	 * Implementing toString as Html reports will look good
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Browser:" + "<span style='font-weight:normal'>").append(getBrowser()).append("</span>");
		sb.append(", Capabilities:" + "<span style='font-weight:normal'>").append(this.getCapabilities().toString())
		  .append("</span>");
	/*	if (isRemoteFlag()) {
			if (isBrowserStackSwitch()) {
				sb.append(",BrowserVersion:"
						+ "<span style='font-weight:normal'>"
						+ getBrowserVersion() + "</span>");
				sb.append(",OsName:" + "<span style='font-weight:normal'>"
						+ getOsName() + "</span>");
				sb.append(",OsVersion:" + "<span style='font-weight:normal'>"
						+ getOsVersion() + "</span>");
				sb.append(",BrowserStackKey:"
						+ "<span style='font-weight:normal'>" + getBsPassword()
						+ "</span>");
				sb.append(",BrowerStackSwitch:"
						+ "<span style='font-weight:normal'>"
						+ isBrowserStackSwitch() + "</span>");
				if (isBsLocalTesting()) {
					sb.append(",BrowserStack LocalTesting:"
							+ "<span style='font-weight:normal'>"
							+ isBsLocalTesting() + "</span>");
				}
			}
			sb.append(",RemoteFlag:" + "<span style='font-weight:normal'>"
					+ isRemoteFlag() + "</span>");
			sb.append(",RemoteURL:" + "span style='font-weight:normal'>"
					+ getRemoteURL() + "</span>");
		}*/
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int hash = 7;

		if (this.isRemoteFlag()) {
			
				hash = 31 * hash + this.getBrowser().hashCode();
				hash = 31 * hash + this.getCapabilities().hashCode();
		} else {
			hash = 31 * hash + this.getBrowser().hashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		//Check if remoteFlag=false then comparison of BrowserName
		//else if true then if DesiredCapability are same and BrowserName is same if 
		if(this == obj){
			return true;
		}
		if(obj == null || obj.getClass() != this.getClass()){
			return false;
		}
		BrowserConfR secondObj = (BrowserConfR)obj;
		if(this.isRemoteFlag() == secondObj.isRemoteFlag()){
			if(this.isRemoteFlag()) {
				return this.getBrowser().equals(secondObj.getBrowser()) &&
						this.getCapabilities().equals(secondObj.getCapabilities());
			}else{
				return this.getBrowser().equals(secondObj.getBrowser());
			}
		}
		return false;
	}
	
	public String getDataSource()
	{
		return mappedValues.get(DriverConfigurations.FrameworkConfig.datasource.toString());
	}

	@Override
	public MutableCapabilities getCapabilities() {
		return mutableCapabilities;
	}
	

	@Override
	public String host() {
		return mappedValues.get(DriverConfigurations.HubConfig.host.toString());
	}

	@Override
	public String port() {
		return mappedValues.get(DriverConfigurations.HubConfig.port.toString());
	}



}
