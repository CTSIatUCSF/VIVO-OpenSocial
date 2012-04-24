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

package edu.cornell.mannlib.vitro.webapp.auth.policy.bean;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.listener.ChangeListener;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.beans.Property;

/**
 * Add this ChangeListener to your EditProcessObject when modifying the
 * ontology, and we will refresh the PropertyRestrictionPolicyHelper bean as
 * appropriate.
 */
public class PropertyRestrictionListener implements ChangeListener {
	private static final Log log = LogFactory
			.getLog(PropertyRestrictionListener.class);

	private final ServletContext ctx;

	public PropertyRestrictionListener(ServletContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * If the deleted property had a non-null restriction, rebuild the bean.
	 */
	@Override
	public void doDeleted(Object oldObj, EditProcessObject epo) {
		Property p = (Property) oldObj;
		if (eitherRoleChanged(p.getHiddenFromDisplayBelowRoleLevel(),
				p.getProhibitedFromUpdateBelowRoleLevel(), null, null)) {
			log.debug("rebuilding the PropertyRestrictionPolicyHelper after deletion");
			createAndSetBean();
		}
	}

	/**
	 * If the inserted property has a non-null restriction, rebuild the bean.
	 */
	@Override
	public void doInserted(Object newObj, EditProcessObject epo) {
		Property p = (Property) newObj;
		if (eitherRoleChanged(null, null,
				p.getHiddenFromDisplayBelowRoleLevel(),
				p.getProhibitedFromUpdateBelowRoleLevel())) {
			log.debug("rebuilding the PropertyRestrictionPolicyHelper after insertion");
			createAndSetBean();
		}
	}

	/**
	 * If the updated property has changed its restrictions, rebuild the bean.
	 */
	@Override
	public void doUpdated(Object oldObj, Object newObj, EditProcessObject epo) {
		Property oldP = (Property) oldObj;
		Property newP = (Property) newObj;
		if (eitherRoleChanged(oldP.getHiddenFromDisplayBelowRoleLevel(),
				oldP.getProhibitedFromUpdateBelowRoleLevel(),
				newP.getHiddenFromDisplayBelowRoleLevel(),
				newP.getProhibitedFromUpdateBelowRoleLevel())) {
			log.debug("rebuilding the PropertyRestrictionPolicyHelper after update");
			createAndSetBean();
		}
	}

	private boolean eitherRoleChanged(RoleLevel oldDisplayRole,
			RoleLevel oldUpdateRole, RoleLevel newDisplayRole,
			RoleLevel newUpdateRole) {
		return (!isTheSame(oldDisplayRole, newDisplayRole))
				|| (!isTheSame(oldUpdateRole, newUpdateRole));
	}

	private boolean isTheSame(RoleLevel oldRole, RoleLevel newRole) {
		if ((oldRole == null) && (newRole == null)) {
			return true;
		} else if ((oldRole == null) || (newRole == null)) {
			return false;
		} else {
			return oldRole.compareTo(newRole) == 0;
		}
	}

	private void createAndSetBean() {
		OntModel model = (OntModel) ctx.getAttribute("jenaOntModel");
		PropertyRestrictionPolicyHelper bean = PropertyRestrictionPolicyHelper
				.createBean(model);
		PropertyRestrictionPolicyHelper.setBean(ctx, bean);
	}
}
