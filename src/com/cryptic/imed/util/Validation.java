package com.cryptic.imed.util;

import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * @author sharafat
 */
public class Validation {
    public static final String EMAIL_PATTERN = "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
    public static final String URL_PATTERN = "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    public static boolean validateRequired(EditText editText, String errorMsg) {
        if ("".equals(editText.getText().toString().trim())) {
            editText.setError(errorMsg);
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    /*
        WARNING: Returns true if input is empty.
     */
    public static boolean validateRegex(String regex, EditText editText, String errorMsg) {
        if ("".equals(editText.getText().toString())) {
            return true;
        } else if (Pattern.compile(regex).matcher(editText.getText().toString()).matches()) {
            editText.setError(null);
            return true;
        } else {
            editText.setError(errorMsg);
            return false;
        }
    }

    public static boolean validateEmail(EditText editText, String errorMsg) {
        return validateRegex(EMAIL_PATTERN, editText, errorMsg);
    }

    public static boolean validateUrl(EditText editText, String errorMsg) {
        return validateRegex(URL_PATTERN, editText, errorMsg);
    }

    public static boolean validatePasswordsMatch(EditText passwordEditText, EditText confirmPasswordEditText,
                                                 String errorMsg) {
        if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
            passwordEditText.setError(errorMsg);
            passwordEditText.setText("");
            confirmPasswordEditText.setText("");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }
}
