/**
 * PasswordEscaper.java
 *
 * Copyright (C) 2010 imedias
 *
 * This file is part of JBackpack.
 *
 * JBackpack is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * JBackpack is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Created on 27. March 2012, 22:31
 */
package ch.fhnw.jbackpack;

/**
 * A tool class for escaping passwords so that they can be used in shell scripts
 *
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class PasswordEscaper {

    /**
     * escapes bash control characters (e.g. $,",`)
     *
     * @param password the password to be escaped
     * @return escaped password
     */
    public static String escapePassword(char[] password) {
        StringBuilder stringBuilder = new StringBuilder(password.length);
        stringBuilder.append(password);
        String input = stringBuilder.toString();
        String escapedInput = input.replace("\"", "\\\"");
        escapedInput = escapedInput.replace("`", "\\`");
        escapedInput = escapedInput.replace("$", "\\$");
        return escapedInput;
    }
}
