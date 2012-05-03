package edu.ucsf.vitro.opensocial;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

public class GadgetSpec {
	private String openSocialGadgetURL;
	private String name;
	private int appId = 0;
	private List<String> channels = new ArrayList<String>();
	private boolean fromSandbox = false;
	private Map<String, GadgetViewRequirements> viewRequirements = new HashMap<String, GadgetViewRequirements>();

	// For preloading
	public GadgetSpec(int appId, String name, String openSocialGadgetURL,
			String[] channels) {
		this.appId = appId;
		this.name = name;
		this.openSocialGadgetURL = openSocialGadgetURL;
		this.channels.addAll(Arrays.asList(channels));
	}

	public GadgetSpec(int appId, String name, String openSocialGadgetURL,
			String channelsStr) {
		this(appId, name, openSocialGadgetURL, channelsStr != null
				&& channelsStr.length() > 0 ? channelsStr.split(" ")
				: new String[0]);
	}

	public GadgetSpec(int appId, String name, String openSocialGadgetURL,
			String[] channels, boolean fromSandbox, BasicDataSource ds)
			throws SQLException {
		this(appId, name, openSocialGadgetURL, channels);
		this.fromSandbox = fromSandbox;
		// Load gadgets from the DB first
		if (!fromSandbox) {
			Connection conn = null;
			Statement stmt = null;
			ResultSet rset = null;

			try {
				String sqlCommand = "select page, viewer_req, owner_req, [view], closed_width, open_width, start_closed, chromeId, display_order from shindig_app_views where appId = "
						+ appId;
				conn = ds.getConnection();
				stmt = conn.createStatement();
				rset = stmt.executeQuery(sqlCommand);
				while (rset.next()) {
					viewRequirements.put(
							rset.getString(0),
							new GadgetViewRequirements(rset.getString(0), rset
									.getString(1), rset.getString(2), rset
									.getString(3), rset.getInt(4), rset
									.getInt(5), rset.getBoolean(6), rset
									.getString(7), rset.getInt(8)));
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
		}
	}

	public int getAppId() {
		return appId;
	}

	public String getName() {
		return name;
	}

	public String getGadgetURL() {
		return openSocialGadgetURL;
	}

	public String[] getChannels() {
		return (String[]) channels.toArray();
	}

	public boolean listensTo(String channel) { // if fromSandbox just say yes,
												// we don't care about
												// performance in this situation
		return fromSandbox || channels.contains(channel);
	}

	public GadgetViewRequirements getGadgetViewRequirements(String page) {
		if (viewRequirements.containsKey(page)) {
			return viewRequirements.get(page);
		}
		return null;
	}

	public boolean show(int viewerId, int ownerId, String page,
			BasicDataSource ds) throws SQLException {
		boolean show = true;
		// if there are no view requirements, go ahead and show it. We are
		// likely testing out a new gadget
		// if there are some, turn it off unless this page is
		if (viewRequirements.size() > 0) {
			show = false;
		}

		if (viewRequirements.containsKey(page)) {
			show = true;
			GadgetViewRequirements req = getGadgetViewRequirements(page);
			if ('U' == req.getViewerReq() && viewerId <= 0) {
				show = false;
			} else if ('R' == req.getViewerReq()) {
				show &= isRegisteredTo(viewerId, ds);
			}
			if ('R' == req.getOwnerReq()) {
				show &= isRegisteredTo(ownerId, ds);
			} else if ('S' == req.getOwnerReq()) {
				show &= (viewerId == ownerId);
			}
		}
		return show;
	}

	public boolean isRegisteredTo(int personId, BasicDataSource ds)
			throws SQLException {
		int count = 0;

		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;

		try {
			String sqlCommand = "select count(*) from shindig_app_registry where appId = "
					+ getAppId() + " and personId = " + personId + ";";
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlCommand);
			while (rset.next()) {
				count = rset.getInt(0);
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

		return (count == 1);
	}

	public boolean fromSandbox() {
		return fromSandbox;
	}

	// who sees it? Return the viewerReq for the ProfileDetails page
	public char getVisibleScope() {
		GadgetViewRequirements req = getGadgetViewRequirements("ProfileDetails.aspx");
		return req != null ? req.getViewerReq() : ' ';
	}

}
