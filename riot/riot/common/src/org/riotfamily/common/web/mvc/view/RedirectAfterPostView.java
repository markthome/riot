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
package org.riotfamily.common.web.mvc.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.support.ServletUtils;

/**
 * View that sends a redirect to the originating request URI.
 * <b>NOTE:</b> The implementation is not thread safe.
 *  
 * @see ServletUtils#getOriginatingRequestUri(HttpServletRequest)
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RedirectAfterPostView extends FlashScopeView {

	public void render(Map<String, ?> model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		setHttp10Compatible(ServletUtils.isHttp10(request));
		setUrl(ServletUtils.getOriginatingRequestUri(request));
		super.render(model, request, response);
	}
	
}
