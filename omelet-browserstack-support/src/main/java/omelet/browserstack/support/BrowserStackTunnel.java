/*******************************************************************************
 *
 * 	Copyright 2014 Springer Science+Business Media Deutschland GmbH
 * 	
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 * 	
 * 	    http://www.apache.org/licenses/LICENSE-2.0
 * 	
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 *******************************************************************************/
package omelet.browserstack.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import omelet.common.OSName;
import omelet.common.OSName.OSN;
import omelet.common.Utils;

/**
 * For Setting and terminating BrowserStack Tunnel
 * 
 * @author kapilA
 *
 */
public class BrowserStackTunnel {

	private static Set<String> activeTunnels = Collections
			.synchronizedSet(new HashSet<String>());
	private static BrowserStackTunnel browserStackTunnel;
	private static final Logger Log =LogManager.getLogger(BrowserStackTunnel.class);
	private static OSN environment;
	private Process tunnelProcess;
	private InputStream is;
	private BufferedReader br;
	private ProcessBuilder pb;
	private String tunnelIdentifier = UUID.randomUUID().toString();

	String browserStackKey;
	List<String> browserStackURLS;

	// Hiding the constructor for Singleton
	private BrowserStackTunnel() {
	}

	public static BrowserStackTunnel getInstance() {
		if (browserStackTunnel == null) {
			synchronized (BrowserStackTunnel.class) {
				if (browserStackTunnel == null) {
					browserStackTunnel = new BrowserStackTunnel();
				}
			}
		}
		environment = OSName.get();	
		return browserStackTunnel;
	}

	/***
	 * Return the List of the keys present for termination
	 * 
	 * @return
	 */
	public List<String> getOpenTunnelKeys() {
		List<String> returnKeys = new ArrayList<String>();
		for (String s : activeTunnels) {
			returnKeys.add(s);
		}
		return returnKeys;
	}

	/***
	 * Set ups the tunnel for the Keys
	 * 
	 * @param browserStackKey
	 * @param browserStackURLS
	 */
	public void createTunnel(String browserStackKey,
			List<String> browserStackURLS) {
		this.browserStackKey = browserStackKey;
		this.browserStackURLS = browserStackURLS;

		if (!activeTunnels.contains(browserStackKey)) {
			synchronized (browserStackTunnel) {
				if (!activeTunnels.contains(browserStackKey)) {
					Log.info("Starting tunnel for Key:" + browserStackKey);
					pb = new ProcessBuilder();

					pb.command(getSetUpCommand(browserStackKey,
							browserStackURLS));
					try {
						tunnelProcess = pb.start();
						waitforTunnelTobeUp("Press Ctrl-C to exit");
						activeTunnels.add(browserStackKey);
					} catch (IOException e) {
						Log.error(e);
					} catch (InterruptedException e) {
						Log.error(e);
					}
				} else {
					Log.info("Tunnel Already exsist for they key:"
							+ browserStackKey);
				}
			}
		} else {
			Log.info("Tunnel Already exsist for key:" + browserStackKey);
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
		is = tunnelProcess.getInputStream();
		br = new BufferedReader(new InputStreamReader(is));
		String t = "";
		while (!waitForMessage.equalsIgnoreCase(t) && t != null) {
			try {
				t = br.readLine();
				// if(t == null) {
				// createTunnel(browserStackKey, browserStackURLS);
				// }
				Log.info("cmd Output:" + t);
			} catch (IOException e) {
				Log.error(e);
			}
		}
	}

	/***
	 * Terminate tunnel
	 * 
	 * @param browserStackKey
	 */
	public void terminateTunnel(String browserStackKey) {
		// Check if it is present in the Hash Set
		// if not , remove from the set
		if (isTunnelPresent(browserStackKey)) {
			Log.debug("Starting termination of Tunnel for key:"
					+ browserStackKey);
			KillTunnel kbs = this.new KillTunnel(browserStackKey);
			kbs.kill();
			Log.info("Killing BrowserStack tunnel for Key:"
					+ browserStackKey);
		} else {
			Log.info("Tunnel Already Terminated for the key:"
					+ browserStackKey);
		}
	}

	/***
	 * Check if key entry there in Set yes remove and return true for killing
	 * else false
	 * 
	 * @param browserStackKey
	 * @return
	 */
	private boolean isTunnelPresent(String browserStackKey) {
		if (activeTunnels.contains(browserStackKey)) {
			synchronized (browserStackTunnel) {
				if (activeTunnels.contains(browserStackKey)) {
					activeTunnels.remove(browserStackKey);
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/***
	 * Return the set up command for setting the browserstack tunnel
	 * 
	 * @param browserStackKey
	 * @param bsURLS
	 * @return
	 */
	private List<String> getSetUpCommand(String browserStackKey,
			List<String> bsURLS) {
		List<String> command = new ArrayList<String>();
		StringBuilder urls = new StringBuilder();
		for (String s : bsURLS) {
			if (!s.contains(",")) {
				urls.append(s);
				urls.append(",80,0,");
				urls.append(s);
				urls.append(",443,1,");
			} else {
				urls.append(s);
				urls.append(",");
			}
		}

		urls.deleteCharAt(urls.length() - 1);
		switch (environment) {
		case UNIX:
			command.add(Utils.getResources(this, "BrowserStackLocal"));
			
			break;
		case WIN:
			command.add("cmd");
			command.add("/c");
			command.add('"' + Utils.getResources(this, "BrowserStackLocal.exe") + '"');
			break;
		case MAC:
			command.add(Utils.getResources(this, "BrowserStackLocal"));
			break;
		default:
			break;
		}
		command.add(browserStackKey);
		command.add(urls.toString());
		command.add("-force");
		command.add("-tunnelIdentifier");
		command.add(tunnelIdentifier);
		return command;
	}
	
	public String getTunnelIdentifier(){
		return tunnelIdentifier;
	}

	/***
	 * @author kapilA
	 * 
	 */
	private class KillTunnel {
		// As of now key is not required because process name is taken and then
		// killed
		// Expectation is that there should be only one BrowserStackLocal task
		// running
		String browserStackKey;

		public KillTunnel(String browserStackKey) {
			this.browserStackKey = browserStackKey;
		}

		/***
		 * Kills the tunnel
		 */
		public void kill() {
			Process killProcess = null;
			ProcessBuilder killpb = new ProcessBuilder();
			killpb.command(getKillCommand());
			try {
				killProcess = killpb.start();
				killProcess.waitFor();
			} catch (IOException e) {
				Log.error("Kill process for BrowserStack Tunnel having key: "
						+ browserStackKey + " failed to start. " + e);
			} catch (InterruptedException e) {
				Log.error("Kill process for BrowserStack Tunnel having key: "
						+ browserStackKey + " failed to start. " + e);
			} finally {
				if (killProcess != null) {
					killProcess.destroy();
				}
			}
		}

		private List<String> getKillCommand() {
			List<String> killCommand = new ArrayList<String>();
			switch (environment) {
			case UNIX:
				killCommand.add("pkill");
				killCommand.add("-f");
				killCommand.add("BrowserStackLocal");
				break;
			case WIN:
				killCommand.add("taskkill");
				killCommand.add("/F");
				killCommand.add("/IM");
				killCommand.add("BrowserStackLocal.exe");
				break;
			case MAC:
				killCommand.add("pkill");
				killCommand.add("-f");
				killCommand.add("BrowserStackLocal");
				break;
			default:
				break;
			}
			return killCommand;
		}
	}
}
