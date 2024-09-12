package omelet.data.driverconf;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.MutableCapabilities;
//import org.openqa.selenium.remote.DesiredCapabilities;

public class PrepareDesiredCapability {
	
	private MutableCapabilities dc = new MutableCapabilities();
	private Properties props;
	private Map<String, String> initialDesiredCap = new HashMap<String, String>();
	private static final String intialz = "dc.";

	public PrepareDesiredCapability(Properties props) {
		this.props = props;
		convertToMap();
	}

	public PrepareDesiredCapability(Map<String, String> desiredCap) {
		this.initialDesiredCap.putAll(desiredCap);
	}

	private void convertToMap() {
		Enumeration<?> keys = props.propertyNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			initialDesiredCap.put(key, (String) props.get(key));
		}
	}

	/***
	 * Make DesiredCapabilities from the properites Map
	 * 
	 * @return
	 */
	public MutableCapabilities get() {
		for (String key : initialDesiredCap.keySet()) {
			KeyValueHolder kv = getKey(key);
			if (kv != null && StringUtils.isNotBlank(kv.value)) {
				 dc.setCapability(kv.key, kv.value);
			}
		}
		return dc;
	}

	private KeyValueHolder getKey(String key) {
		if (key.startsWith(intialz) && key.length() > 3) {
			return new KeyValueHolder(key.substring(3), initialDesiredCap.get(key));
		}
		return null;
	}

	private class KeyValueHolder {
		String key;
		String value;

		KeyValueHolder(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}

}
