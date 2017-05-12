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
package org.riotfamily.pages.controller;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.controller.CacheableViewController;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.springframework.ui.Model;

public class CacheablePageController extends CacheableViewController {

	@Override
	protected void populateModel(Model model, HttpServletRequest request) {
		populateModel(model, PageResolver.getPage(request));
	}
	
	protected void populateModel(Model model, Page page) {
	}

}
