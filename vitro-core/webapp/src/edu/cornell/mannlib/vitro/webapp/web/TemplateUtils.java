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

package edu.cornell.mannlib.vitro.webapp.web;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.DeepUnwrap;

/**
 * TODO
 */
public class TemplateUtils {
	private static final Log log = LogFactory.getLog(TemplateUtils.class);

	public static class DropFromSequence implements TemplateMethodModelEx {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Object exec(List args) throws TemplateModelException {
			if (args.size() != 2) {
				throw new TemplateModelException("Wrong number of arguments");
			}

			TemplateModel sequenceWrapper = (TemplateModel) args.get(0);
			if (!(sequenceWrapper instanceof TemplateSequenceModel)
					&& !(sequenceWrapper instanceof TemplateCollectionModel)) {
				throw new TemplateModelException(
						"First argument must be a sequence or a collection");
			}
			TemplateModel unwantedWrapper = (TemplateModel) args.get(1);
			if (!(unwantedWrapper instanceof TemplateScalarModel)) {
				throw new TemplateModelException(
						"Second argument must be a string");
			}

			List<String> sequence = (List<String>) DeepUnwrap
					.unwrap(sequenceWrapper);
			String unwanted = (String) DeepUnwrap.unwrap(unwantedWrapper);

			sequence.remove(unwanted);
			return sequence;
		}

	}
}
