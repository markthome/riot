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
package org.riotfamily.core.runtime;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

/**
 * Bean that exposes the riot-servlet prefix, the resource path and the
 * riot version.
 * <p>
 * By default, Riot assumes that the riot-servlet is mapped to 
 * <code>/riot/*</code>. In order to use a different mapping, you have to set 
 * the context attribute <code>riotServletPrefix</code> in your 
 * <code>web.xml</code>.
 * </p>
 */
public class RiotRuntime implements ServletContextAware {

	public static final String SERVLET_PREFIX_ATTRIBUTE = "riotServletPrefix";
	
	public static final String DEFAULT_SERVLET_PREFIX = "/riot";
	
	public static final String DEFAULT_RESOURCE_MAPPING = "/resources";
	
	private String resourceMapping = DEFAULT_RESOURCE_MAPPING;
	
	private String servletPrefix;
	
	private String resourcePath;
	
	
	public void setResourceMapping(String resourceMapping) {
		this.resourceMapping = resourceMapping;
	}
	
	public void setServletContext(ServletContext context) {
		Assert.notNull(resourceMapping, "A resourceMapping must be specified.");
		servletPrefix = context.getInitParameter(SERVLET_PREFIX_ATTRIBUTE);
		if (servletPrefix == null) {
			servletPrefix = DEFAULT_SERVLET_PREFIX;
		}
		resourcePath = servletPrefix + resourceMapping + '/' + getRiotVersion() + '/';
	}
		
	public String getServletPrefix() {
		return servletPrefix;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
    
	private String getRiotVersion() {
		return RiotVersion.getVersionString().replace('.', '-');
	}	
	
	public static RiotRuntime getRuntime(ApplicationContext context) {
		return BeanFactoryUtils.beanOfTypeIncludingAncestors(
				context, RiotRuntime.class);
	}
}
