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
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;

/**
 * A base class for our custom-made FileSet extensions.
 */
public abstract class AbstractWrappedFileSet implements ResourceCollection {
	private Project p;
	
	/** The list of FileResources that we will yield to the task. */
	protected List<FileResource> files;
	
	/** The internal FileSet */
	private FileSet fileSet = new FileSet();
	
	public void setProject(Project p) {
		this.p = p;
		fileSet.setProject(p);
	}
	
	public void setDir(File dir) {
		fileSet.setDir(dir);
	}
	
    public PatternSet.NameEntry createInclude() {
    	return fileSet.createInclude();
    }

    public PatternSet.NameEntry createExclude() {
    	return fileSet.createExclude();
    }
    
    public PatternSet createPatternSet() {
    	return fileSet.createPatternSet();
    }
    	 
    
	@Override
	public Object clone() {
		throw new BuildException(this.getClass().getSimpleName()
				+ " does not support cloning.");
	}

	@Override
	public boolean isFilesystemOnly() {
		return true;
	}

	@Override
	public Iterator<? extends Resource> iterator() {
		fillFileList();
		return files.iterator();
	}

	@Override
	public int size() {
		fillFileList();
		return files.size();
	}

	protected abstract void fillFileList();
	
	protected Project getProject() {
		return p;
	}
	
	protected FileSet getInternalFileSet() {
		return fileSet;
	}

}
