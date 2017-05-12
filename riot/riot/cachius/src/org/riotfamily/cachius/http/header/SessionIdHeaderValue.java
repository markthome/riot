/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.cachius.http.header;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class SessionIdHeaderValue extends DynamicHeaderValue {

	public SessionIdHeaderValue(String value, int insertAt, int skip) {
		super(value, insertAt, skip);
	}

	@Override
	protected void appendDynamicValue(StringBuilder sb,
			HttpServletRequest request) {
		
		if (!request.isRequestedSessionIdFromCookie()) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				sb.append(";jsessionid=");
				sb.append(session.getId());
			}
		}
	}

}
