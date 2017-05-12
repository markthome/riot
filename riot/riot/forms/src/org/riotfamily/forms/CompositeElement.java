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
package org.riotfamily.forms;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.riotfamily.forms.request.FormRequest;
import org.riotfamily.forms.ui.Dimension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;


/**
 * Abstract superclass for elements that consist of several child elements.  
 * Calls to <code>processRequest()</code> and <code>render()</code> are 
 * automatically delegated to the components. Additionally the components are
 * initialized, i.e. <code>setParent()</code>, <code>setForm()</code> and 
 * <code>Form.registerElement()</code> are called.  
 */
public abstract class CompositeElement extends AbstractEditorBase 
		implements BeanFactoryAware {

	private List<Element> components;
	
	private AutowireCapableBeanFactory beanFactory;
	
	/**
	 * Empty default constructor.
	 */
	public CompositeElement() {
		components = new ArrayList<Element>();
	}
	
	/**
	 * Empty default constructor.
	 */
	@SuppressWarnings("unchecked")
	public CompositeElement(List<? extends Element> components) {
		this.components = (List<Element>) components;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof AutowireCapableBeanFactory) {
			this.beanFactory = (AutowireCapableBeanFactory) beanFactory;
		}
	}
	
	protected List<Element> getComponents() {
		return components;
	}

	/**
	 * Adds the given element to the list of components. If a form as already
	 * been set {@link #initComponent(Element)} is 
	 * invoked, otherwise initialization is deferred until 
	 * {@link #setForm(Form)} is called.
	 * 
	 * @param element the element to add
	 */
	protected void addComponent(Element element) {
		components.add(element);
		element.setParent(this);
		if (getForm() != null) {
			initComponent(element);
		}
	}

	/**
	 * Removes the given component. Note that the element is
	 * not unregistered from the form.
	 */
	protected void removeComponent(Element element) {
		components.remove(element);
	}

	public boolean isEmpty() {
		return components.isEmpty();
	}
	
	/**
	 * Invokes <code>initComponent(Element)</code> on all components and 
	 * finally calls <code>initCompositeElement()</code>.
	 */
	@Override
	protected final void afterFormSet() {
		for (Element element : components) {
			initComponent(element);
		}
		initCompositeElement();
	}
	
	/**
	 * Subclasses may override this method to perform initialization tasks.
	 * A reference to the form will be set at this point and all components
	 * will be initialized.
	 * 
	 *The default implementation does nothing.
	 */
	protected void initCompositeElement() {
	}

	/**
	 * Calls <code>processRequestInternal()</code> and afterwards 
	 * <code>processRequestComponents()</code> to process the components.
	 */
	@Override
	public void processRequest(FormRequest request) {
		processRequestInternal(request);		
		processRequestCompontents(request);
	}
		
	/**
	 * Processes the request for all the components
	 */
	protected void processRequestCompontents(FormRequest request) {
		// Temporary list to allow concurrent modification
		List<Element> tempList = new ArrayList<Element>(components);
		for (Element component : tempList) {
			if (component.isEnabled()) {
				component.processRequest(request);
			}
		}
	}
	
	/**
	 * Sets a reference to the form and registers the element by calling 
	 * {@link Form#registerElement(Element)}.
	 *  
	 * @throws IllegalStateException if form is null
	 */
	protected void initComponent(Element element) {
		Assert.state(getForm() != null, "The form must be set");
		if (beanFactory != null) {
			beanFactory.initializeBean(element, null);
		}
		getForm().registerElement(element);
	}

	/**
	 * Called before processRequest() is invoked on the contained elements.
	 * Subclasses can override this method to perform custom processing. The
	 * default implementation does nothing.
	 */
	protected void processRequestInternal(FormRequest request) {
	}

	@Override
	protected void renderInternal(PrintWriter writer) {		
		for (Element component : components) {
			component.render(writer);
		}	
	}
	
	public Dimension getDimension() {
		Dimension d = getPadding();
		for (Element component : components) {
			d = d.addHeight(getComponentPadding(component).add(component.getDimension()));
		}
		return d;
	}

	protected Dimension getPadding() {
		return new Dimension();
	}
	
	protected Dimension getComponentPadding(Element component) {
		return new Dimension();
	}
	
	/**
	 * Delegates the call to the first component.
	 */
	@Override
	public void focus() {
		if (!components.isEmpty()) {
			components.get(0).focus();
		}
	}
	
	/**
	 * Helper method to check for composite elements in templates.
	 * Always returns <code>true</code>
	 */
	@Override
	public boolean isCompositeElement() {
		return true;
	}
}