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
package org.riotfamily.common.log4j;

import java.util.Timer;
import java.util.TimerTask;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

/**
 * Log4J Appender that sends reports via email.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class SmartSmtpAppender extends AppenderSkeleton {

	private String[] to;
	
	private String from;
	
	private String subject = "Riot Log Report [%s]";
	
	private long startUpWindow;
	
	private long reportWindow;
	
	private long minLagBetweenMails;
	
	private JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	
	private Timer timer = new Timer("log4jMailSender", true);
	
	private StringBuilder text;
	
	private boolean reportScheduled;
	
	private long lastReportSent;
	
	private boolean html;
	
	private boolean startUpMail;
	
	private Priority actualThreshold;
	
	private Priority highestSeverity = Level.DEBUG;
	
	public SmartSmtpAppender() {
		setHost("localhost");
		setMinLagBetweenMails("1h");
		setReportWindow("2s");
		setStartUpWindow("30s");
		setThreshold(Level.ERROR);
	}
	
	/**
	 * Sets the SMTP server to use.
	 */
	public void setHost(String host) {
		mailSender.setHost(host);
	}

	/**
	 * Sets the SMTP username to use.
	 */
	public void setUsername(String username) {
		mailSender.setUsername(username);
		enableSmtpAuth();
	}

	/**
	 * Sets the SMTP password to use.
	 */
	public void setPassword(String password) {
		mailSender.setPassword(password);
		enableSmtpAuth();
	}
	
	private void enableSmtpAuth() {
		mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", "true");
	}

	/**
	 * Sets the recipients. Multiple addresses can be specified separated 
	 * by commas.
	 */
	public void setTo(String to) {
		this.to = StringUtils.commaDelimitedListToStringArray(to);
	}
	
	/**
	 * Sets the from address.
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * Sets the subject. The first occurrence of <code>%s</code> will be 
	 * replaced by the highest severity included in the report.    
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * Sets the time to wait for further events after a report has been 
	 * scheduled.
	 */
	public void setReportWindow(String s) {
		this.reportWindow = FormatUtils.parseMillis(s);
	}

	/**
	 * Sets the time that must elapse before a new report is sent.
	 */
	public void setMinLagBetweenMails(String s) {
		this.minLagBetweenMails = FormatUtils.parseMillis(s);
	}
	
	/**
	 * Sets how long INFO events should be recorded at start-up. If set
	 * to <code>0</code> no mail will be sent at startup.
	 */
	public void setStartUpWindow(String s) {
		this.startUpWindow = FormatUtils.parseMillis(s);
	}

	@Override
	public void activateOptions() {
		if (getLayout() == null) {
			FreeMarkerLayout layout = new FreeMarkerLayout();
			layout.activateOptions();
			setLayout(layout);
		}
		html = "text/html".equals(getLayout().getContentType());
		if (startUpWindow > 0) {
			startUpMail = true;
			actualThreshold = getThreshold();
			setThreshold(Level.INFO);
			schedule(startUpWindow);
		}
	}
	
	@Override
	protected synchronized void append(LoggingEvent event) {
		if (lastReportSent + minLagBetweenMails <= System.currentTimeMillis()) {
			if (text == null) {
				text = new StringBuilder();
				String header = getLayout().getHeader();
				if (header != null) {
					text.append(header);
				}
			}
			text.append(getLayout().format(event));
			if (event.getLevel().isGreaterOrEqual(highestSeverity)) {
				highestSeverity = event.getLevel();
			}
			schedule(reportWindow);
		}
	}
	
	private synchronized void schedule(long delay) {
		if (!reportScheduled) {
			reportScheduled = true;
			timer.schedule(new TimerTask() {
				public void run() {
					send();
				}
			}, delay);
		}
	}
	
	protected synchronized void send() {
		try {
			String footer = getLayout().getFooter();
			if (footer != null) {
				text.append(footer);
			}
			MimeMessage mime = mailSender.createMimeMessage();
			MimeMessageHelper msg = new MimeMessageHelper(mime);
			msg.setTo(to);
			msg.setFrom(from);
			msg.setSubject(String.format(subject, highestSeverity.toString()));
			msg.setText(text.toString(), html);
			mailSender.send(mime);
		}
		catch (Exception e) {
			getErrorHandler().error("Failed to send report", e, 0);
		}
		finally {
			text = null;
			highestSeverity = Level.DEBUG;
			if (startUpMail) {
    			setThreshold(actualThreshold);
    			startUpMail = false;
    		}
			else {
				lastReportSent = System.currentTimeMillis();
			}
			reportScheduled = false;
		}
	}

	public boolean requiresLayout() {
        return false;
    }
   
	public void close() {
		timer.cancel();
		timer = null;
	}
	
}
