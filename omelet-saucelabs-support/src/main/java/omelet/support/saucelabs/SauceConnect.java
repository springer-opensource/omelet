package omelet.support.saucelabs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import omelet.common.OSName;
import omelet.configuration.DefaultBrowserConf;

/**
 * This Class create and destroy the tunnel to SauceLabs.
 */
public class SauceConnect implements ISuiteListener {
	private static final Logger Log = LogManager.getLogger(SauceConnect.class);
	private Process tunnelProcess;

	@Override
	public void onStart(ISuite suite) {
		createTunnel();
	}

	@Override
	public void onFinish(ISuite suite) {
		tunnelProcess.destroy();
	}

	/***
	 * create the SauceLabs tunnel
	 */
	private void createTunnel() {
		try {
			tunnelProcess = Runtime.getRuntime()
					.exec(getSetUpCommand(DefaultBrowserConf.get().getuserName(), DefaultBrowserConf.get().getKey()));
			waitforTunnelTobeUp("Sauce Connect is up, you may start your tests.");
		} catch (IOException e) {
			Log.error("Create tunnel not successful. " + e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			Log.error("Create tunnel not successful. " + e);
			e.printStackTrace();
		}
	}

	/***
	 * wait for tunnel to be up
	 *
	 * @param waitForMessage
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void waitforTunnelTobeUp(String waitForMessage) throws IOException, InterruptedException {
		InputStream is = tunnelProcess.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String t = "";
		while (!t.contains(waitForMessage) && t != null) {
			try {
				t = br.readLine();
				Log.info("cmd Output: " + t);
			} catch (IOException e) {
				Log.error(e);
			}
		}
	}

	/***
	 * Return the set up command for setting the SauceLabs tunnel
	 *
	 * @param slUserName
	 * @return
	 */
	private String getSetUpCommand(String slUserName, String slKey) {
		StringBuilder command = new StringBuilder();

		switch (OSName.get()) {
		case WIN:
			command.append(System.getProperty("user.dir") + "/src/main/resources/win/sc.exe");
			break;
		case UNIX:
			command.append(System.getProperty("user.dir") + "/src/main/resources/linux/sc");
			break;
		case MAC:
			command.append(System.getProperty("user.dir") + "/src/main/resources/osx/sc");
			break;
		}
		command.append(" -u " + slUserName);
		command.append(" -k " + slKey);
		Log.info("Command: " + command);
		return command.toString();
	}
}
