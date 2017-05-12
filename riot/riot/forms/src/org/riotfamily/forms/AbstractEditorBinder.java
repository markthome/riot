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

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.riotfamily.common.beans.property.SqlDateEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.propertyeditors.CustomDateEditor;


/**
 * This class is used to bind a form element to the property of a bean. The
 * form element must implement the {@link org.riotfamily.forms.Editor}
 * interface.
 */
public abstract class AbstractEditorBinder extends PropertyEditorRegistrySupport
		implements EditorBinder {

	private Logger log = LoggerFactory.getLogger(AbstractEditorBinder.class);

	/** List of {@link EditorBinding editor bindings} */
	private Map<String, EditorBinding> bindings = new LinkedHashMap<String, EditorBinding>();

	public AbstractEditorBinder() {
		registerDefaultEditors();
		registerCustomEditor(java.sql.Date.class,new SqlDateEditor());
		registerCustomEditor(Date.class, new CustomDateEditor(
				new SimpleDateFormat("yyyy-MM-dd"), false));
	}
	
	@Override
	public abstract Class<?> getPropertyType(String path);
	
	public Map<String, EditorBinding> getBindings() {
		return this.bindings;
	}

	public EditorBinder replace(EditorBinder previousBinder) {
		if (previousBinder != null) {
			this.bindings = previousBinder.getBindings();
		}
		return this;
	}

	public void bind(Editor editor, String property) {
		log.debug("Binding " + editor + " to property " + property);
		if (bindings.containsKey(property)) {
			EditorBindingImpl eb = (EditorBindingImpl) bindings.get(property);
			eb.setEditor(editor);
			editor.setEditorBinding(eb);
		}
		else {
			EditorBinding eb = new EditorBindingImpl(editor, property);		
			bindings.put(property, eb);
			editor.setEditorBinding(eb);
		}
	}

	public Editor getEditor(String property) {
		if (property != null) {
			int i = property.indexOf('.');
			if (i != -1) {
				String nested = property.substring(i + 1);
				try {
					Editor editor = findEditorByProperty(property.substring(0, i));
					if (editor instanceof NestedEditor) {
						NestedEditor ne = (NestedEditor) editor;
						return ne.getEditor(nested);
					}
					else {
						throw new IllegalStateException("Editor for " + property 
								+ " must implement the NestedEditor interface");
					}
				}
				catch (InvalidPropertyException e) {
					//nested editor was not defined, fall back to direct binding
				}
			}
			return findEditorByProperty(property);
		}
		return null;
	}

	protected Editor findEditorByProperty(String property) {
		EditorBinding binding = bindings.get(property);
		if (binding != null) {
			return binding.getEditor();
		}		
		throw new InvalidPropertyException(getBeanClass(), property,
				"No editor bound to property");
	}

	public String[] getBoundProperties() {
		String[] props = new String[bindings.size()];		
		Iterator<String> it = bindings.keySet().iterator();
		for (int i = 0; it.hasNext(); i++) {			 
			props[i] = it.next();
		}
		return props;
	}

	public void registerPropertyEditors(
			Collection<PropertyEditorRegistrar> registrars) {
		
		if (registrars != null) {
			for (PropertyEditorRegistrar registrar : registrars) {
				registrar.registerCustomEditors(this);
			}
		}
	}
	
	public void initEditors() {
		for (EditorBinding binding : bindings.values()) {
			Editor editor = binding.getEditor();
			Object value = getPropertyValue(binding.getProperty());
			editor.setValue(value);
		}
	}

	public Object populateBackingObject() {
		for (EditorBinding binding : bindings.values()) {
			Editor editor = binding.getEditor();
			if (editor.isEnabled()) {
				Object value = editor.getValue();
				setPropertyValue(binding.getProperty(), value);
			}
		}
		return getBackingObject();
	}

	public PropertyEditor getPropertyEditor(Class<?> type, String propertyPath) {
		PropertyEditor pe = findCustomEditor(type, propertyPath);
		if (pe == null) {
			pe = getDefaultEditor(type);
		}
		return pe;
	}
	
	protected String getPropertyPath(Editor editor, String property) {
		EditorBinding parentBinding = findParentBinding(editor);
		if (parentBinding != null) {
			return parentBinding.getPropertyPath() + '.' + property;
		}
		return property;
	}
	
	private EditorBinding findParentBinding(Editor editor) {
		Element parent = editor.getParent();
		while (parent != null) {
			if (parent instanceof Editor) {
				Editor parentEditor = (Editor) parent;
				if (parentEditor.getEditorBinding() != null) {
					return parentEditor.getEditorBinding(); 
				}
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	private class EditorBindingImpl implements EditorBinding {

		private Editor editor;

		private String property;
		
		public EditorBindingImpl(Editor editor,	String property) {
			this.editor = editor;
			this.property = property;
		}

		public EditorBinder getEditorBinder() {
			return AbstractEditorBinder.this;
		}

		public void setEditor(Editor editor) {
			this.editor = editor;
		}
		
		public Editor getEditor() {
			return editor;
		}

		public String getProperty() {
			return property;
		}
		
		public Object getValue() {
			return getPropertyValue(property);
		}
		
		public Class<?> getBeanClass() {
			return AbstractEditorBinder.this.getBeanClass();
		}
		
		public boolean isEditingExistingBean() {
			return AbstractEditorBinder.this.isEditingExistingBean();
		}
		
		public String getPropertyPath() {
			return AbstractEditorBinder.this.getPropertyPath(editor, property);
		}
				
		public Class<?> getPropertyType() {
			return AbstractEditorBinder.this.getPropertyType(property);
		}
		
		public PropertyEditor getPropertyEditor() {
			return AbstractEditorBinder.this.getPropertyEditor(
					getPropertyType(), getPropertyPath());
		}
		
	}

}
