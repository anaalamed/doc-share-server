package docSharing.utils;

import java.util.regex.Pattern;

public class InputValidation {
    public enum Field {NAME, EMAIL, PASSWORD}
    public Field field;

    public static boolean isValidEmail(String email) {
        String regexPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (patternMatches(email, regexPattern)) {
            return true;
        }
        return false;
    }

    public static boolean isValidName(String name) {
        String regexPattern = "[a-zA-Z]{3,30}";                   // only letters. length: 3-30
        if (patternMatches(name, regexPattern)) {
            return true;
        }
        return false;
    }

    public static boolean isValidPassword(String password) {
        String regexPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";       // Minimum eight characters, at least one letter and one number:
        if (patternMatches(password, regexPattern)) {
            return true;
        }
        return false;
    }

    private static boolean patternMatches(String fieldToValidate, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(fieldToValidate)
                .matches();
    }


//    public static boolean validateUserFields(Field field, String variable) {
//        switch (field) {
//            case NAME:
//                return isValidName(variable);
//            case EMAIL:
//                return isValidEmail(variable);
//            case PASSWORD:
//                return isValidPassword(variable);
//            default:
//                return false;
//        }
//    }


//    public static boolean validateUserFields(String email, String password) {
//        return validateEmail(email) && validatePassword(password);
//    }
}
