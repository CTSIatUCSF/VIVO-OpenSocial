/*
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.controller.accounts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.PermissionSet;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.AbstractPageHandler;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.email.FreemarkerEmailFactory;

/**
 * Common routines for the page controllers.
 */
public abstract class UserAccountsPage extends AbstractPageHandler {
	private static final Log log = LogFactory.getLog(UserAccountsPage.class);

	private static final String PERSON_CLASS_URI = "http://xmlns.com/foaf/0.1/Person";

	/**
	 * After the account is created, or the password is reset, the user has this
	 * many days to repond to the email.
	 */
	protected static final int DAYS_TO_USE_PASSWORD_LINK = 90;

	protected UserAccountsPage(VitroRequest vreq) {
		super(vreq);
	}

	protected boolean isEmailEnabled() {
		return FreemarkerEmailFactory.isConfigured(vreq);
	}

	/**
	 * Create a list of all known non-public PermissionSets.
	 */
	protected List<PermissionSet> buildListOfSelectableRoles() {
		List<PermissionSet> list = new ArrayList<PermissionSet>();
		for (PermissionSet ps: userAccountsDao.getAllPermissionSets()) {
			if (!ps.isForPublic()) {
				list.add(ps);
			}
		}

		Collections.sort(list, new Comparator<PermissionSet>() {
			@Override
			public int compare(PermissionSet ps1, PermissionSet ps2) {
				return ps1.getUri().compareTo(ps2.getUri());
			}
		});
		return list;
	}

	/**
	 * Create a list of possible profile types.
	 * 
	 * TODO Right now, these are foaf:Person and it's sub-classes. What will it
	 * be for Vitro?
	 */
	protected SortedMap<String, String> buildProfileTypesList() {
		String seedClassUri = PERSON_CLASS_URI;
		List<String> classUris = vclassDao.getAllSubClassURIs(seedClassUri);
		classUris.add(seedClassUri);

		SortedMap<String, String> types = new TreeMap<String, String>();
		for (String classUri : classUris) {
			VClass vclass = vclassDao.getVClassByURI(classUri);
			if (vclass != null) {
				types.put(classUri, vclass.getName());
			}
		}
		return types;
	}

	/**
	 * Make these URLs available to all of the pages.
	 */
	protected Map<String, String> buildUrlsMap() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("list", UrlBuilder.getUrl("/accountsAdmin/list"));
		map.put("add", UrlBuilder.getUrl("/accountsAdmin/add"));
		map.put("delete", UrlBuilder.getUrl("/accountsAdmin/delete"));
		map.put("myAccount", UrlBuilder.getUrl("/accounts/myAccount"));
		map.put("createPassword", UrlBuilder.getUrl("/accounts/createPassword"));
		map.put("resetPassword", UrlBuilder.getUrl("/accounts/resetPassword"));
		map.put("firstTimeExternal",
				UrlBuilder.getUrl("/accounts/firstTimeExternal"));
		map.put("accountsAjax", UrlBuilder.getUrl("/accountsAjax"));
		map.put("proxyAjax", UrlBuilder.getUrl("/proxiesAjax"));

		return map;
	}

	protected static String editAccountUrl(String uri) {
		return UrlBuilder.getUrl("/accountsAdmin/edit", new ParamMap(
				"editAccount", uri));
	}

	protected Date figureExpirationDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, DAYS_TO_USE_PASSWORD_LINK);
		return c.getTime();
	}

	protected boolean checkPasswordLength(String pw) {
		return pw.length() >= UserAccount.MIN_PASSWORD_LENGTH
				&& pw.length() <= UserAccount.MAX_PASSWORD_LENGTH;
	}

	protected String getSiteName() {
		ApplicationBean appBean = vreq.getAppBean();
		return appBean.getApplicationName();
	}

	protected ProfileInfo buildProfileInfo(String uri) {
		Individual ind = indDao.getIndividualByURI(uri);
		if (ind == null) {
			return null;
		} else {
			return new ProfileInfo(ind.getRdfsLabel(), uri,
					UrlBuilder.getIndividualProfileUrl(uri, vreq));
		}
	}

	public static class ProfileInfo {
		private final String label;
		private final String uri;
		private final String url;

		public ProfileInfo(String label, String uri, String url) {
			this.label = label;
			this.uri = uri;
			this.url = url;
		}

		public String getLabel() {
			return label;
		}

		public String getUri() {
			return uri;
		}

		public String getUrl() {
			return url;
		}
	}
}
