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
package org.riotfamily.pages;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.ChainedLocaleResolver;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class SiteLocaleResolver extends ChainedLocaleResolver {

	@Override
	protected Locale resolveLocaleInternal(HttpServletRequest request) {
		Site site = PageResolver.getSite(request);
		if (site != null) {
			return site.getLocale();
		}
		return null;
	}
	
}
