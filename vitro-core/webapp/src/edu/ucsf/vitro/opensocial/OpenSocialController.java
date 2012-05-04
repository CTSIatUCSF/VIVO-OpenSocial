package edu.ucsf.vitro.opensocial;

import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.IndividualController;

public class OpenSocialController {
	public static final String OPENSOCIAL_DEBUG = "OPENSOCIAL_DEBUG";
	public static final String OPENSOCIAL_NOCACHE = "OPENSOCIAL_NOCACHE";
	public static final String OPENSOCIAL_GADGETS = "OPENSOCIAL_GADGETS";

	public static final String JSON_PERSONID_CHANNEL = "JSONPersonIds";
	public static final String JSON_PMID_CHANNEL = "JSONPubMedIds";

	private static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";

	private Random myRandom = new Random();
	private List<PreparedGadget> gadgets = new ArrayList<PreparedGadget>();
	private Map<String, String> pubsubdata = new HashMap<String, String>();
	private int viewerId = -1;
	private int ownerId = -1;
	private boolean isDebug = false;
	private boolean noCache = false;
	private String pageName;
	private ConfigurationProperties configuration;

	private BasicDataSource dataSource;

	public OpenSocialController(VitroRequest vreq) throws SQLException, IOException {
		this.isDebug = vreq.getSession() != null
				&& Boolean.TRUE.equals(vreq.getSession().getAttribute(
						OPENSOCIAL_DEBUG));
		this.noCache = vreq.getSession() != null
				&& Boolean.TRUE.equals(vreq.getSession().getAttribute(
						OPENSOCIAL_NOCACHE));
		this.pageName = "" + vreq.getServletPath(); // http://www.exampledepot.com/egs/javax.servlet/GetReqUrl.html

		configuration = ConfigurationProperties.getBean(vreq.getSession()
				.getServletContext());

		if (configuration.getProperty("OpenSocial.url") == null) {
			// do nothing
			return;
		}
		String defaultNamespace = configuration
				.getProperty("Vitro.defaultNamespace");
		UserAccount viewer = LoginStatusBean.getCurrentUser(vreq);
		this.viewerId = viewer != null ? Integer.parseInt(viewer.getUri()
				.substring(defaultNamespace.length() + 1)) : -1;

		Individual owner = IndividualController.getIndividualFromRequest(vreq);
		this.ownerId = owner != null ? Integer.parseInt(owner.getLocalName().substring(1))
				: -1;

		Random random = new Random();

		boolean gadgetLogin = pageName.contains("gadgetlogin");
		String requestAppId = vreq.getParameter("appId");

		Map<String, GadgetSpec> dbApps = new HashMap<String, GadgetSpec>();
		Map<String, GadgetSpec> officialApps = new HashMap<String, GadgetSpec>();

		dataSource = new BasicDataSource();
		dataSource.setDriverClassName(DEFAULT_DRIVER);
		dataSource.setUsername(configuration
				.getProperty("VitroConnection.DataSource.username"));
		dataSource.setPassword(configuration
				.getProperty("VitroConnection.DataSource.password"));
		dataSource.setUrl(configuration
				.getProperty("VitroConnection.DataSource.url"));

		// Load gadgets from the DB first
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {

			String sqlCommand = "select appId, name, url, channels, enabled from shindig_apps";
			// if a specific app is requested, only grab it
			if (requestAppId != null) {
				sqlCommand += " where appId = " + requestAppId;
			}
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlCommand);

			while (rset.next()) {
				GadgetSpec spec = new GadgetSpec(rset.getInt(1),
						rset.getString(2), rset.getString(3), rset.getString(4));
				String gadgetFileName = getGadgetFileNameFromURL(rset
						.getString(3));

				dbApps.put(gadgetFileName, spec);
				if (requestAppId != null || rset.getBoolean(5)) {
					officialApps.put(gadgetFileName, spec);
				}
			}
		} finally {
			try {
				rset.close();
			} catch (Exception e) {
			}
			try {
				stmt.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		// Add manual gadgets if there are any
		// Note that this block of code only gets executed after someone logs in
		// with gadgetlogin.aspx!
		int moduleId = 0;
		if (vreq.getSession() != null
				&& vreq.getSession().getAttribute(OPENSOCIAL_GADGETS) != null) {
			String openSocialGadgetURLS = (String) vreq.getSession()
					.getAttribute(OPENSOCIAL_GADGETS);
			String[] urls = openSocialGadgetURLS.split(System
					.getProperty("line.separator"));
			for (String openSocialGadgetURL : urls) {
				if (openSocialGadgetURL.length() == 0)
					continue;
				int appId = 0; // if URL matches one in the DB, use DB provided
								// appId, otherwise generate one
				String gadgetFileName = getGadgetFileNameFromURL(openSocialGadgetURL);
				String name = gadgetFileName;
				List<String> channels = new ArrayList<String>();
				boolean sandboxOnly = true;
				if (dbApps.containsKey(gadgetFileName)) {
					appId = dbApps.get(gadgetFileName).getAppId();
					name = dbApps.get(gadgetFileName).getName();
					channels = dbApps.get(gadgetFileName).getChannels();
					sandboxOnly = false;
				} else {
					appId = openSocialGadgetURL.hashCode();
				}
				// if they asked for a specific one, only let it in
				if (requestAppId != null
						&& Integer.getInteger(requestAppId) != appId) {
					continue;
				}
				GadgetSpec gadget = new GadgetSpec(appId, name,
						openSocialGadgetURL, channels, sandboxOnly, dataSource);
				// only add ones that are visible in this context!
				if (sandboxOnly
						|| gadget.show(viewerId, ownerId, pageName, dataSource)) {
					String securityToken = socketSendReceive(viewerId, ownerId,
							"" + gadget.getAppId());
					gadgets.add(new PreparedGadget(gadget, this, moduleId++,
							securityToken));
				}
			}
		}

		// if no manual one were added, use the ones from the DB
		if (gadgets.size() == 0) {
			// Load DB gadgets
			if (gadgetLogin) {
				officialApps = dbApps;
			}
			for (GadgetSpec spec : officialApps.values()) {
				GadgetSpec gadget = new GadgetSpec(spec.getAppId(),
						spec.getName(), spec.getGadgetURL(),
						spec.getChannels(), false, dataSource);
				// only add ones that are visible in this context!
				if (gadgetLogin
						|| gadget.show(viewerId, ownerId, pageName, dataSource)) {
					String securityToken = socketSendReceive(viewerId, ownerId,
							"" + gadget.getAppId());
					gadgets.add(new PreparedGadget(gadget, this, moduleId++,
							securityToken));
				}
			}
		}
		// sort the gadgets
		// TODO
		// gadgets.Sort();

		if (gadgets.size() != 0) {
			// trigger the javascript to render gadgets
			// TODO
			/****
			 * HtmlGenericControl body =
			 * (HtmlGenericControl)page.Master.FindControl("bodyMaster"); if
			 * (body == null) { body =
			 * (HtmlGenericControl)page.Master.Master.FindControl("bodyMaster");
			 * } body.Attributes.Add("onload", "my.init();");
			 ***/
		}
	}

	private String getGadgetFileNameFromURL(String url) {
		String[] urlbits = url.split("/");
		return urlbits[urlbits.length - 1];
	}

	public boolean isDebug() {
		return isDebug;
	}

	public boolean noCache() {
		return noCache;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public boolean hasGadgetListeningTo(String channel) {
		for (PreparedGadget gadget : getVisibleGadgets()) {
			if (gadget.getGadgetSpec().listensTo(channel)) {
				return true;
			}
		}
		return false;
	}

	// JSON Helper Functions
	public static String buildJSONPersonIds(List<Integer> personIds,
			String message) {
		Map<String, Object> foundPeople = new HashMap<String, Object>();
		foundPeople.put("personIds", personIds);
		foundPeople.put("message", message);
		// TODO find JavaScrprtSerializer
		// JavaScriptSerializer serializer = new JavaScriptSerializer();
		return "";// serializer.Serialize(foundPeople);
	}

	public static String buildJSONPersonIds(int personId, String message) {
		List<Integer> personIds = new ArrayList<Integer>();
		personIds.add(personId);
		return buildJSONPersonIds(personIds, message);
	}

	/****
	 * public static String BuildJSONPubMedIds(Person person) { List<Int32>
	 * pubIds = new List<Int32>(); foreach (Publication pub in
	 * person.PublicationList) { foreach (PublicationSource pubSource in
	 * pub.PublicationSourceList) { if ("PubMed".Equals(pubSource.Name)) {
	 * pubIds.Add(Int32.Parse(pubSource.ID)); } } } Dictionary<string, Object>
	 * foundPubs = new Dictionary<string, object>(); foundPubs.Add("pubIds",
	 * pubIds); foundPubs.Add("message", "PubMedIDs for " +
	 * person.Name.FullName); JavaScriptSerializer serializer = new
	 * JavaScriptSerializer(); return serializer.Serialize(foundPubs); }
	 ***/

	public void setPubsubData(String key, String value) {
		if (pubsubdata.containsKey(key)) {
			pubsubdata.remove(key);
		}
		if (value != null || !value.isEmpty()) {
			pubsubdata.put(key, value);
		}
	}

	public Map<String, String> getPubsubData() {
		return pubsubdata;
	}

	public void gemovePubsubGadgetsWithoutData() {
		// if any visible gadgets depend on pubsub data that isn't present,
		// throw them out
		List<PreparedGadget> removedGadgets = new ArrayList<PreparedGadget>();
		for (PreparedGadget gadget : gadgets) {
			for (String channel : gadget.getGadgetSpec().getChannels()) {
				if (!pubsubdata.containsKey(channel)) {
					removedGadgets.add(gadget);
					break;
				}
			}
		}
		for (PreparedGadget gadget : removedGadgets) {
			gadgets.remove(gadget);
		}
	}

	public void removeGadget(String name) {
		// if any visible gadgets depend on pubsub data that isn't present,
		// throw them out
		PreparedGadget gadgetToRemove = null;
		for (PreparedGadget gadget : gadgets) {
			if (name.equals(gadget.getName())) {
				gadgetToRemove = gadget;
				break;
			}
		}
		gadgets.remove(gadgetToRemove);
	}

	public String getPageName() {
		return pageName;
	}

	public String getIdToUrlMapJavascript() {
		String retval = "var idToUrlMap = {";
		for (PreparedGadget gadget : gadgets) {
			// retval += gadget.GetAppId() + ":'" + gadget.GetGadgetURL() +
			// "', ";
			retval += "'remote_iframe_" + gadget.getAppId() + "':'"
					+ gadget.getGadgetURL() + "', ";
		}
		return retval.substring(0, retval.length() - 2) + "};";
	}

	public boolean isVisible() {
		// always have turned on for ProfileDetails.aspx because we want to
		// generate the "profile was viewed" in Javascript (bot proof)
		// regardless of any gadgets being visible, and we need this to be True
		// for the shindig javascript libraries to load
		return (configuration.getProperty("OpenSocial.url") != null
				&& (getVisibleGadgets().size() > 0) || getPageName().equals(
				"/display"));
	}

	public List<PreparedGadget> getVisibleGadgets() {
		return gadgets;
	}

	public static void postActivity(int userId, String title) {
		postActivity(userId, title, null, null, null);
	}

	public static void postActivity(int userId, String title, String body) {
		postActivity(userId, title, body, null, null);
	}

	public static void postActivity(int userId, String title, String body,
			String xtraId1Type, String xtraId1Value) {
		// TODO
		/**
		 * try { Database db = DatabaseFactory.CreateDatabase();
		 * 
		 * string sqlCommand =
		 * "INSERT INTO shindig_activity (userId, activity, xtraId1Type, xtraId1Value) VALUES ("
		 * + userId +
		 * ",'<activity xmlns=\"http://ns.opensocial.org/2008/opensocial\"><postedTime>"
		 * + Convert.ToInt64((DateTime.UtcNow - new DateTime(1970, 1,
		 * 1)).TotalMilliseconds) +"</postedTime><title>" + title + "</title>" +
		 * (body != null ? "<body>" + body + "</body>" : "") + "</activity>','"
		 * + xtraId1Type + "','" + xtraId1Value + "');"; DbCommand dbCommand =
		 * db.GetSqlStringCommand(sqlCommand); db.ExecuteNonQuery(dbCommand); }
		 * catch (Exception e) { throw new Exception(e.Message); }
		 **/
	}

	private String socketSendReceive(int viewer, int owner, String gadget)
			throws IOException {
		// These keys need to match what you see in
		// edu.ucsf.profiles.shindig.service.SecureTokenGeneratorService in
		// Shindig
		String[] tokenService = configuration.getProperty(
				"OpenSocial.tokenService").split(":");
		String request = "c=default&v=" + viewer + "&o=" + owner + "&g="
				+ gadget + "\r\n";
		byte[] bytesReceived = new byte[256];

		// Create a socket connection with the specified server and port.
		Socket s = new Socket(tokenService[0],
				Integer.parseInt(tokenService[1]));

		// Send request to the server.
		s.getOutputStream().write(request.getBytes());

		// Receive the server home page content.
		int bytes = 0;
		String page = "";

		// The following will block until the page is transmitted.
		while ((bytes = s.getInputStream().read(bytesReceived)) > 0) {
			page += new String(bytesReceived, 0, bytes);
		};

		return page;
	}
	
	public String getContainerJavascriptSrc() {
		return configuration.getProperty("OpenSocial.url")
				+ "/gadgets/js/core:dynamic-height:osapi:pubsub:rpc:views:shindig-container.js?c=1"
				+ (isDebug ? "&debug=1" : "");
	}

	public String getGadgetJavascript() {
		String lineSeparator = System.getProperty("line.separator");
		String gadgetScriptText = lineSeparator
				+ "var my = {};"
				+ lineSeparator
				+ "my.gadgetSpec = function(appId, name, url, secureToken, view, closed_width, open_width, start_closed, chrome_id, visible_scope) {"
				+ lineSeparator + "this.appId = appId;" + lineSeparator
				+ "this.name = name;" + lineSeparator + "this.url = url;"
				+ lineSeparator + "this.secureToken = secureToken;"
				+ lineSeparator + "this.view = view || 'default';"
				+ lineSeparator + "this.closed_width = closed_width;"
				+ lineSeparator + "this.open_width = open_width;"
				+ lineSeparator + "this.start_closed = start_closed;"
				+ lineSeparator + "this.chrome_id = chrome_id;" + lineSeparator
				+ "this.visible_scope = visible_scope;" + lineSeparator + "};"
				+ lineSeparator + "my.pubsubData = {};" + lineSeparator;
		for (String key : getPubsubData().keySet()) {
			gadgetScriptText += "my.pubsubData['" + key + "'] = '"
					+ getPubsubData().get(key) + "';" + lineSeparator;
		}
		gadgetScriptText += "my.openSocialURL = '"
				+ configuration.getProperty("OpenSocial.url") + "';"
				+ lineSeparator + "my.debug = " + (isDebug() ? "1" : "0") + ";"
				+ lineSeparator + "my.noCache = " + (noCache() ? "1" : "0")
				+ ";" + lineSeparator + "my.gadgets = [";
		for (PreparedGadget gadget : getVisibleGadgets()) {
			gadgetScriptText += "new my.gadgetSpec(" + gadget.getAppId() + ",'"
					+ gadget.getName() + "','" + gadget.getGadgetURL() + "','"
					+ gadget.getSecurityToken() + "','" + gadget.getView()
					+ "'," + gadget.getClosedWidth() + ","
					+ gadget.getOpenWidth() + ","
					+ (gadget.getStartClosed() ? "1" : "0") + ",'"
					+ gadget.getChromeId() + "','"
					+ gadget.getGadgetSpec().getVisibleScope() + "'), ";
		}
		gadgetScriptText = gadgetScriptText.substring(0,
				gadgetScriptText.length() - 2)
				+ "];"
				+ lineSeparator;

		return gadgetScriptText;
	}
}
