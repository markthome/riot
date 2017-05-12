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
package org.riotfamily.components.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ContentChangedEvent extends ApplicationEvent {

	private String url;

	public ContentChangedEvent(Object source, String url) {
		super(source);
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
	
}
