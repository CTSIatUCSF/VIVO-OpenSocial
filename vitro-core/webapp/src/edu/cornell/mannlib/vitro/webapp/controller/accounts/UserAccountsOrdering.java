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

/**
 * How are the accounts to be sorted?
 */
public class UserAccountsOrdering {
	public enum Direction {
		ASCENDING("ASC"), DESCENDING("DESC");

		public static Direction DEFAULT_DIRECTION = ASCENDING;

		public static Direction fromKeyword(String keyword) {
			if (keyword == null) {
				return DEFAULT_DIRECTION;
			}

			for (Direction d : Direction.values()) {
				if (d.keyword.equals(keyword)) {
					return d;
				}
			}

			return DEFAULT_DIRECTION;
		}

		public final String keyword;

		Direction(String keyword) {
			this.keyword = keyword;
		}
	}

	public enum Field {
		EMAIL("email"), FIRST_NAME("firstName"), LAST_NAME("lastName"), STATUS(
				"status"), ROLE("ps"), LOGIN_COUNT("count"), LAST_LOGIN_TIME(
				"lastLogin");

		public static Field DEFAULT_FIELD = EMAIL;

		public static Field fromName(String name) {
			if (name == null) {
				return DEFAULT_FIELD;
			}

			for (Field f : Field.values()) {
				if (f.name.equals(name)) {
					return f;
				}
			}

			return DEFAULT_FIELD;
		}

		public final String name;

		Field(String name) {
			this.name = name;
		}
	}

	public static final UserAccountsOrdering DEFAULT_ORDERING = new UserAccountsOrdering(
			Field.DEFAULT_FIELD, Direction.DEFAULT_DIRECTION);

	private final Field field;
	private final Direction direction;

	public UserAccountsOrdering(Field field, Direction direction) {
		this.field = field;
		this.direction = direction;
	}

	public Field getField() {
		return field;
	}

	public Direction getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		return "UserAccountsOrdering[field=" + field + ", direction="
				+ direction + "]";
	}
}
