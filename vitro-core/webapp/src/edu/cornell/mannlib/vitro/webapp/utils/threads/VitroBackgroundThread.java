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

package edu.cornell.mannlib.vitro.webapp.utils.threads;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple base class that will allow us to find the background threads and
 * check their current status.
 */
public class VitroBackgroundThread extends Thread {
	Log log = LogFactory.getLog(VitroBackgroundThread.class);

	private static final ConcurrentLinkedQueue<WeakReference<VitroBackgroundThread>> allThreads = new ConcurrentLinkedQueue<WeakReference<VitroBackgroundThread>>();

	/**
	 * Get a list of all VitroBackgroundThreads that have not been garbage-collected.
	 */
	public static List<VitroBackgroundThread> getThreads() {
		List<VitroBackgroundThread> list = new ArrayList<VitroBackgroundThread>();
		for (WeakReference<VitroBackgroundThread> ref : allThreads) {
			VitroBackgroundThread t = ref.get();
			if (t != null) {
				list.add(t);
			}
		}
		return list;
	}

	/**
	 * Get a list of all VitroBackgroundThreads that have not died.
	 */
	public static List<VitroBackgroundThread> getLivingThreads() {
		List<VitroBackgroundThread> list = new ArrayList<VitroBackgroundThread>();
		for (VitroBackgroundThread t : getThreads()) {
			if (t.isAlive()) {
				list.add(t);
			}
		}
		return list;
	}

	public enum WorkLevel {
		IDLE, WORKING
	}

	private volatile WorkLevelStamp stamp = new WorkLevelStamp(WorkLevel.IDLE);

	public VitroBackgroundThread(String name) {
		super(name);
		allThreads.add(new WeakReference<VitroBackgroundThread>(this));
	}

	public VitroBackgroundThread(Runnable target, String name) {
		super(target, name);
		allThreads.add(new WeakReference<VitroBackgroundThread>(this));
	}

	protected void setWorkLevel(WorkLevel level, String... flags) {
		log.debug("Set work level on '" + this.getName() + "' to " + level
				+ ", flags=" + flags);
		stamp = new WorkLevelStamp(level, flags);
	}

	public WorkLevelStamp getWorkLevel() {
		return stamp;
	}

	/**
	 * An immutable object that holds both the current work level and the time
	 * that it was set.
	 * 
	 * Particular threads may want to assign additional state using zero or more
	 * "flags".
	 */
	public static class WorkLevelStamp {
		private final WorkLevel level;
		private final long since;
		private final List<String> flags;

		public WorkLevelStamp(WorkLevel level, String... flags) {
			this.level = level;
			this.since = System.currentTimeMillis();
			this.flags = Collections.unmodifiableList(Arrays.asList(flags));
		}

		public WorkLevel getLevel() {
			return level;
		}

		public Date getSince() {
			return new Date(since);
		}

		public Collection<String> getFlags() {
			return flags;
		}
	}
}
