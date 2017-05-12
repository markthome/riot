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
package org.riotfamily.forms.element;

import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.riotfamily.common.util.DocumentWriter;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.MessageUtils;
import org.riotfamily.forms.request.FormRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A text input field.
 */
public class TextField extends AbstractTextElement {

	private static final String CONFIRM_SUFFIX = "-confirm";
	
	private static final String DEFAULT_CONFIRM_MESSAGE_KEY = 
			"label.textField.confirmInput";
	
	private static final String DEFAULT_REGEX_MISMATCH_MESSAGE_KEY =
			"error.textField.regexMismatch";
	
	private boolean confirm;
	
	private String confirmText = null;
	
	private String confirmMessageKey;
	
	private String confirmMessageText;
	
	private Pattern pattern;
	
	private String regexMismatchMessageKey = DEFAULT_REGEX_MISMATCH_MESSAGE_KEY;
	
	private String regexMismatchMessageText;
	
	
	public TextField() {
		this("text");
	}
	
	public TextField(String s) {		
		super(s);
	}
	
	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}	

	public void setConfirmMessageKey(String confirmMessageKey) {
		this.confirmMessageKey = confirmMessageKey;
	}

	public void setConfirmMessageText(String confirmMessageText) {
		this.confirmMessageText = confirmMessageText;
		this.confirmMessageKey = null;
	}	

	public void setRegex(String regex) {
		this.pattern = Pattern.compile(regex);
		setValidateOnChange(true);
	}

	public void setRegexMismatchMessageKey(String regexMismatchMessageKey) {
		this.regexMismatchMessageKey = regexMismatchMessageKey;
	}

	public void setRegexMismatchMessageText(String regexMismatchMessageText) {
		this.regexMismatchMessageText = regexMismatchMessageText;
		this.regexMismatchMessageKey = null;
	}

	@Override
	public void renderInternal(PrintWriter writer) {
		super.renderInternal(writer);
		if (confirm) {
			DocumentWriter doc = new DocumentWriter(writer);
									
			String msg = MessageUtils.getMessage(this, getConfirmMessage());
			doc.start("p").body(msg, false).end();
			doc.startEmpty("input")
					.attribute("type", getType())
					.attribute("class", getStyleClass())
					.attribute("name", getConfirmParamName())
					.attribute("disabled", !isEnabled())
					.attribute("value",	confirmText != null 
							? confirmText 
							: getText());
			
			doc.closeAll();
		}		
	}
	
	@Override
	public void processRequest(FormRequest request) {
		if (confirm) {
			confirmText = request.getParameter(getConfirmParamName());
		}
		super.processRequest(request);
	}

	@Override
	public void validate() {		
		super.validate();
		if (confirm) {
			if (!ObjectUtils.nullSafeEquals(getText(), confirmText)) {
				ErrorUtils.reject(this, "error.textField.confirmationFailed");
			}
		}
	}
	
	@Override
	protected void validateSyntax() {
		if (pattern != null && StringUtils.hasLength(getText())) {
			if (!pattern.matcher(getText()).matches()) {
				getForm().getErrors().rejectValue(getFieldName(), 
						regexMismatchMessageKey, regexMismatchMessageText);
			}
		}
	}

	protected String getConfirmParamName() {
		return getParamName() + CONFIRM_SUFFIX;
	}
	
	protected String getConfirmMessage() {
		if (confirmMessageText != null) {
			return confirmMessageText;			
		}
		else if (confirmMessageKey != null){
			return MessageUtils.getMessage(this, confirmMessageKey);
		}		
		else {
			return MessageUtils.getMessage(this, getDefaultConfirmMessageKey());
		}
	}
	
	protected String getDefaultConfirmMessageKey() {
		return DEFAULT_CONFIRM_MESSAGE_KEY;
	}

}
