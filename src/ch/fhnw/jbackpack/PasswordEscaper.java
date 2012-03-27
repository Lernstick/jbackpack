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
        String input = "";
        for (char c : password) {
            input += c;
        }
        String escapedInput = input.replace("\"", "\\\"");
        escapedInput = escapedInput.replace("`", "\\`");
        escapedInput = escapedInput.replace("$", "\\$");
        return escapedInput;
    }
}
