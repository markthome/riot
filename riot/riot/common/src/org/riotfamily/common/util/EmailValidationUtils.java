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
package org.riotfamily.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Utility class to perform basic email address syntax checks. The
 * implementation is based on the jakarta-commons EmailValidator. Oro-Matcher
 * dependencies have been removed (now using java.util.regex) and comments
 * are no longer processed, i.e. addresses containing comments will be reported
 * as invalid.
 */
public final class EmailValidationUtils {

    private static final String SPECIAL_CHARS = "\\(\\)<>@,;:\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";
    private static final String QUOTED_USER = "(\"[^\"]*\")";
    private static final String ATOM = VALID_CHARS + '+';
    private static final String WORD = "(" + ATOM + "|" + QUOTED_USER + ")";

    private static final String LEGAL_ASCII_PATTERN = "^[\\x00-\\x7F]+$";
    private static final String EMAIL_PATTERN = "^(.+)@(.+)$";
    private static final String IP_DOMAIN_PATTERN =
            "^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$";

    private static final String USER_PATTERN = "^" + WORD + "(\\.|" 
            + WORD + ")*$";
            
    private static final String DOMAIN_PATTERN = "^" + ATOM + "(\\." 
            + ATOM + ")*$";
            
    private static final String ATOM_PATTERN = "(" + ATOM + ")";
    
    private static final String ADDR_PATTERN = "^[^<]*<([^>]*)>\\s*$";


    /* Compiled patterns */
	private static Pattern legalAsciiPattern = Pattern.compile(LEGAL_ASCII_PATTERN);
	private static Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
	private static Pattern ipDomainPattern = Pattern.compile(IP_DOMAIN_PATTERN);
	private static Pattern userPattern = Pattern.compile(USER_PATTERN);
	private static Pattern domainPattern = Pattern.compile(DOMAIN_PATTERN);
	private static Pattern atomPattern = Pattern.compile(ATOM_PATTERN);
	private static Pattern addrPattern = Pattern.compile(ADDR_PATTERN);

    
	private EmailValidationUtils() {
    }

	private static Logger getLog() {
		return LoggerFactory.getLogger(EmailValidationUtils.class);
	}
	
    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param email The value validation is being performed on. 
     *              A <code>null</code> value is considered invalid.
     */
    public static boolean isValid(String email) {
        if (email == null) {
            getLog().debug("Address is null");
            return false;
        }
        Matcher addrMatcher = addrPattern.matcher(email);
        if (addrMatcher.matches()) {
            email = addrMatcher.group(1);
            getLog().debug("Extracted machine part from address: " + email);
        }
        
        if (!legalAsciiPattern.matcher(email).matches()) {
        	getLog().debug("Address must only contain ASCII characters");
            return false;
        }

        // Check the whole email address structure
        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.matches()) {
        	getLog().debug("Invalid address structure");
            return false;
        }

        if (email.endsWith(".")) {
            getLog().debug("Address must not end with a dot");
            return false;
        }

        if (!isValidUser(emailMatcher.group(1))) {
        	getLog().debug("Invalid user");
            return false;
        }

        if (!isValidDomain(emailMatcher.group(2))) {
        	getLog().debug("Invalid domain");
            return false;
        }
        getLog().debug("Address is valid");
        return true;
    }

    /**
     * Returns true if the domain component of an email address is valid.
     * @param domain being validatied.
     */
    private static boolean isValidDomain(String domain) {
        boolean symbolic = false;
        
        Matcher ipAddressMatcher = ipDomainPattern.matcher(domain);
        if (ipAddressMatcher.matches()) {
            if (!isValidIpAddress(ipAddressMatcher)) {
                return false;
            }
        } 
        else {
            // Domain is symbolic name
            symbolic = domainPattern.matcher(domain).matches();
        }

        if (symbolic) {
            if (!isValidSymbolicDomain(domain)) {
                return false;
            }
        } 
        else {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the user component of an email address is valid.
     * @param user being validated
     */
    private static boolean isValidUser(String user) {
        return userPattern.matcher(user).matches();
    }

    /**
     * Validates an IP address. Returns true if valid.
     * @param ipAddressMatcher Pattren matcher
     */
    private static boolean isValidIpAddress(Matcher ipAddressMatcher) {
        for (int i = 1; i <= 4; i++) {
            String ipSegment = ipAddressMatcher.group(i);
            if (ipSegment == null || ipSegment.length() <= 0) {
                return false;
            }

            int iIpSegment = 0;

            try {
                iIpSegment = Integer.parseInt(ipSegment);
            } 
            catch (NumberFormatException e) {
                return false;
            }

            if (iIpSegment > 255) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates a symbolic domain name.  Returns true if it's valid.
     * @param domain symbolic domain name
     */
    private static boolean isValidSymbolicDomain(String domain) {
        String[] domainSegment = new String[10];
        boolean match = true;
        int i = 0;
        Matcher atomMatcher = atomPattern.matcher(domain);
        while (match) {
            match = atomMatcher.find();
            if (match) {
                domainSegment[i] = atomMatcher.group(1);
                int l = domainSegment[i].length() + 1;
                domain =
                        (l >= domain.length())
                        ? ""
                        : domain.substring(l);

                i++;
            }
        }

        int len = i;
        if (domainSegment[len - 1].length() < 2
                || domainSegment[len - 1].length() > 4) {

            return false;
        }

        // Make sure there's a host name preceding the domain.
        if (len < 2) {
            return false;
        }

        return true;
    }
    
}
