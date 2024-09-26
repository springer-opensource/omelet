package omelet.data;

import java.util.List;

import omelet.data.DataProvider.mapStrategy;
import omelet.data.driverconf.IBrowserConf;

import org.testng.IRetryAnalyzer;

public interface IMethodContext {
	
	List<IProperty> getMethodTestData();
	List<IBrowserConf> getBrowserConf();
	mapStrategy getRunStrategy();
	DataSource getDataProvider();
	boolean isAfterMethod();
	boolean isBeforeMethod();
	IRetryAnalyzer getRetryAnalyzer();
}
