package com.cryptic.imed.util;

import android.widget.EditText;

/**
 * @author sharafat
 */
public class Validation {

    public static boolean validateRequired(EditText editText, String errorMsg) {
        if ("".equals(editText.getText().toString().trim())) {
            editText.setError(errorMsg);
            return false;
        } else {
            editText.setError(null);
            return true;
        }
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
