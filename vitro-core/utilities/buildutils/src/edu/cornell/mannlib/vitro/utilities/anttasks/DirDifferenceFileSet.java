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

package edu.cornell.mannlib.vitro.utilities.anttasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.resources.FileResource;

/**
 * TODO
 */
public class DirDifferenceFileSet extends AbstractWrappedFileSet {
	private Path blockingPath;

    public Path createBlockingPath() {
        if (blockingPath == null) {
        	blockingPath = new Path(getProject());
        }
        return blockingPath.createPath();
    }

	@Override
	protected void fillFileList() {
		if (files != null) {
			return;
		}

		FileSet fs = getInternalFileSet();

		@SuppressWarnings("unchecked")
		Iterator<FileResource> iter = fs.iterator();

		files = new ArrayList<FileResource>();
		while (iter.hasNext()) {
			FileResource fr = iter.next();
			if (!isBlocked(fr)) {
				files.add(fr);
			}
		}
	}

	/**
	 * Check to see whether this same file exists in any of the blocking
	 * directories.
	 */
	private boolean isBlocked(FileResource fr) {
		for (String blockingDir : blockingPath.list()) {
			File f = new File(blockingDir + File.separator + fr.getName());
			if (f.exists()) {
				return true;
			}
		}
		return false;
	}
}
