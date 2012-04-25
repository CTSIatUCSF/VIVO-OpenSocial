package edu.ucsf.vivo.opensocial;

public class PreparedGadget {
	private GadgetSpec gadgetSpec;
	private OpenSocialHelper helper;
	private int moduleId;
	private String securityToken;

	public PreparedGadget(GadgetSpec gadgetSpec, OpenSocialHelper helper,
			int moduleId, String securityToken) {
		this.gadgetSpec = gadgetSpec;
		this.helper = helper;
		this.moduleId = moduleId;
		this.securityToken = securityToken;
	}

	public int CompareTo(PreparedGadget other) {
		GadgetViewRequirements gvr1 = this.getGadgetViewRequirements();
		GadgetViewRequirements gvr2 = other.getGadgetViewRequirements();
		return ("" + this.getView() + (gvr1 != null ? gvr1.getDisplayOrder()
				: Integer.MAX_VALUE)).compareTo("" + other.getView()
				+ (gvr2 != null ? gvr2.getDisplayOrder() : Integer.MAX_VALUE));
	}

	public GadgetSpec getGadgetSpec() {
		return gadgetSpec;
	}

	public String getSecurityToken() {
		return securityToken;
	}

	public int getAppId() {
		return gadgetSpec.getAppId();
	}

	public String getName() {
		return gadgetSpec.getName();
	}

	public int getModuleId() {
		return moduleId;
	}

	public String getGadgetURL() {
		return gadgetSpec.getGadgetURL();
	}

	GadgetViewRequirements getGadgetViewRequirements() {
		return gadgetSpec.getGadgetViewRequirements(helper.getPageName());
	}

	public String getView() {
		GadgetViewRequirements reqs = getGadgetViewRequirements();
		if (reqs != null) {
			return reqs.getView();
		}
		// default behavior that will get invoked when there is no reqs. Useful
		// for sandbox gadgets
		else if (helper.getPageName().equals("ProfileEdit.aspx")) {
			return "home";
		} else if (helper.getPageName().equals("ProfileDetails.aspx")) {
			return "profile";
		} else if (helper.getPageName().equals("GadgetDetails.aspx")) {
			return "canvas";
		} else if (gadgetSpec.getGadgetURL().contains("Tool")) {
			return "small";
		} else {
			return null;
		}
	}

	public int getOpenWidth() {
		GadgetViewRequirements reqs = getGadgetViewRequirements();
		return reqs != null ? reqs.getOpenWidth() : 0;
	}

	public int getClosedWidth() {
		GadgetViewRequirements reqs = getGadgetViewRequirements();
		return reqs != null ? reqs.getClosedWidth() : 0;
	}

	public boolean getStartClosed() {
		GadgetViewRequirements reqs = getGadgetViewRequirements();
		// if the page specific reqs are present, honor those. Otherwise defaut
		// to true for regular gadgets, false for sandbox gadgets
		return reqs != null ? reqs.getStartClosed() : !gadgetSpec.FromSandbox();
	}

	public String getChromeId() {
		GadgetViewRequirements reqs = getGadgetViewRequirements();
		if (reqs != null) {
			return reqs.getChromeId();
		}
		// default behavior that will get invoked when there is no reqs. Useful
		// for sandbox gadgets
		else if (gadgetSpec.getGadgetURL().contains("Tool")) {
			return "gadgets-tools";
		} else if (helper.getPageName().equals("ProfileEdit.aspx")) {
			return "gadgets-edit";
		} else if (helper.getPageName().equals("ProfileDetails.aspx")) {
			return "gadgets-view";
		} else if (helper.getPageName().equals("GadgetDetails.aspx")) {
			return "gadgets-detail";
		} else if (helper.getPageName().equals("Search.aspx")) {
			return "gadgets-search";
		} else {
			return null;
		}
	}

	public String getCanvasURL() {
		return "~/GadgetDetails.aspx?appId=" + getAppId() + "&Person="
				+ helper.getOwnerId();
	}

}
