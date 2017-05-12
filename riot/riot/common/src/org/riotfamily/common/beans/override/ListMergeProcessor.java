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
package org.riotfamily.common.beans.override;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ListMergeProcessor implements BeanFactoryPostProcessor, PriorityOrdered {

	private Logger log = LoggerFactory.getLogger(ListMergeProcessor.class);
			
	private String ref;
	
	private String property;
	
	private List<?> values;
	
	private boolean append = false;
	
	private int order = 1;
	
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public void setProperty(String property) {
		this.property = property;
	}

	public void setValues(List<?> values) {
		this.values = values;
	}
	
	public void setAppend(boolean append) {
		this.append = append;
	}

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) 
			throws BeansException {

		BeanDefinition bd = beanFactory.getBeanDefinition(ref);
		if (property == null) {
			Assert.state(ListFactoryBean.class.getName().equals(bd.getBeanClassName()),
					"Bean [" + ref + "] must be a ListFactoryBean");
			
			property = "sourceList";
		}
		
		log.debug("Adding " + values.size() + " items to " + ref 
				+ "." + property + ": " + values);
		
		PropertyValue pv = bd.getPropertyValues().getPropertyValue(property);
		if (pv == null) {
			// No list set on the target bean, create a new one ...
			ManagedList list = new ManagedList();
			list.addAll(values);
			bd.getPropertyValues().addPropertyValue(property, list);
		}
		else {
			Object value = pv.getValue();
			if (value instanceof RuntimeBeanReference) {
				RuntimeBeanReference ref = (RuntimeBeanReference) value;
				value = beanFactory.getBean(ref.getBeanName());
			}
			else if (value instanceof TypedStringValue) {
				TypedStringValue tsv = (TypedStringValue) value;
				ManagedList list = new ManagedList();
				list.addAll(values);
				list.add(tsv.getValue());
				bd.getPropertyValues().addPropertyValue(property, list);
				return;
			}
			Assert.isInstanceOf(List.class, value);
			List list = (List) value;
			if (append) {
				list.addAll(values);
			}
			else {
				for (int i = values.size() - 1; i >= 0; i--) {
					list.add(0, values.get(i));
				}
			}
		}
	}
	
}
