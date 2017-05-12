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
package org.riotfamily.forms.element.select;

import java.util.List;

import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.request.FormRequest;


/**
 * Abstract superclass for elements that let the user choose from a set of
 * options like selectboxes or radio button groups.
 */
public abstract class AbstractSingleSelectElement 
		extends AbstractSelectElement {
	
	private Object selectedValue;
	
	public final void setValue(Object value) {
		this.selectedValue = value;
	}
	
	public Object getValue() {
		return selectedValue;
	}

	protected boolean hasSelection() {
		return selectedValue != null;
	}
	
	public boolean isSelected(OptionItem item) {
		return hasSelection() && selectedValue.equals(item.getValue());
	}

	/**
	 * @see org.riotfamily.forms.AbstractElement#processRequest
	 */
	public void processRequest(FormRequest request) {
		updateSelection(request.getParameter(getParamName()));
	}
	
	public int getSelectedIndex() {
		if (hasSelection()) {
			for (int i = 0; i < getOptionItems().size(); i++) {
				OptionItem item = getOptionItems().get(i);
				if (selectedValue.equals(item.getValue())) {
					return i;
				}
			}
		}
		return -1;
	}
	
	protected void updateSelection(List<OptionItem> items) {
		if (items != null && selectedValue != null) {
			for (OptionItem item : items) {
				if (selectedValue.equals(item.getValue())) {
					selectedValue = item.getValue();
					return;
				}
			}
		}
		selectedValue = null;
	}
	
	private void updateSelection(String index) {
		int i = -1;
		if (index != null) {
			i = Integer.parseInt(index);
		}
		if (i >= 0) {
			OptionItem option = getOptionItems().get(i);
			selectedValue = option.getValue();
		}
		else {
			selectedValue = null;	
		}
		validate();
	}
	
	public void handleJavaScriptEvent(JavaScriptEvent event) {
		if (event.getType() == JavaScriptEvent.ON_CHANGE) {
			Object oldValue = selectedValue;
			updateSelection(event.getValue());
			fireChangeEvent(selectedValue, oldValue);
		}
	}

}