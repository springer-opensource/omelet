package omelet.driver;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.MutableCapabilities;

public class ConfigureDesiredCapability {
	
	private static Map<String,MutableCapabilities> lDesiredCapMap = new HashMap<String, MutableCapabilities>();
	
	
	public static void load(Map<String,MutableCapabilities> desiredCapMap){
		lDesiredCapMap = desiredCapMap;
	}
	
	protected static MutableCapabilities getDesiredCapabilities(String browserName){
		if(lDesiredCapMap.containsKey(browserName)){
			return lDesiredCapMap.get(browserName);
		}
		return null;
	}

}
