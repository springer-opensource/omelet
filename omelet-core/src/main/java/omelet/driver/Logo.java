package omelet.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logo {

	private static final Logger LOGGER = LogManager.getLogger(Logo.class);
	/*private final PropertyValueMin prop = new PropertyValueMin(Utils.getResources(this,
			"omelet-builtin.properties"));*/
	private static Logo instance;

	private static final String LOGO = "\n"
			+ "**********************************************\n"
			+ "**********************************************\n"
			+ "                      .__          __" + "\n"
			+ "  ____   _____   ____ |  |   _____/  |_" + "\n"
			+ "/  _  \\ /     \\_/ __ \\|  | _/ __ \\   __\\" + "\n"
			+ "(  <_> )  Y Y  \\  ___/|  |_\\  ___/|  |" + "\n"
			+ "\\____ /|__|_|  /\\___  >____/\\___  >__|" + "\n"
			+ "        \\/     \\/          \\/" + "\n"
			+ "**********************************************\n"
			+ "           LET'S COOK SOME TESTS!\n"
			+ "**********************************************\n";

	private Logo() {

	}

	public static Logo getInstance() {
		if (instance == null) {
			instance = new Logo();
		}
		return instance;
	}


	public void printLogoAndVersion() {

		LOGGER.info(LOGO);
		//TODO:need to fix the display of omelet version
		//LOGGER.info(prop.getValue("omelet.version"));
	}

}
