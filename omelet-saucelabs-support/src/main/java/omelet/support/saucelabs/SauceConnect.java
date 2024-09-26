package omelet.support.saucelabs;

import omelet.common.OSName;
import omelet.configuration.DefaultBrowserConf;
import org.apache.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This Class create and destroy the tunnel to SauceLabs.
 */
public class SauceConnect implements ISuiteListener {
  private static final Logger LOGGER = Logger.getLogger(SauceConnect.class);
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
      tunnelProcess = Runtime.getRuntime().exec(getSetUpCommand(DefaultBrowserConf.get().getuserName(), DefaultBrowserConf.get().getKey()));
      waitforTunnelTobeUp("Sauce Connect is up, you may start your tests.");
    } catch (IOException e) {
      LOGGER.error("Create tunnel not successful. " + e);
      e.printStackTrace();
    } catch (InterruptedException e) {
      LOGGER.error("Create tunnel not successful. " + e);
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
  private void waitforTunnelTobeUp(String waitForMessage) throws IOException,
    InterruptedException {
    InputStream is = tunnelProcess.getInputStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    String t = "";
    while (!t.contains(waitForMessage) && t != null) {
      try {
        t = br.readLine();
        LOGGER.info("cmd Output: " + t);
      } catch (IOException e) {
        LOGGER.error(e);
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
    LOGGER.info("Command: "+command);
    return command.toString();
  }
}
