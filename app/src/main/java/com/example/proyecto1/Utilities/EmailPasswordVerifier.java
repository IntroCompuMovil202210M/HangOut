package com.example.proyecto1.Utilities;

public class EmailPasswordVerifier {
    public static boolean verifyEmail(String email){
        //Use regex to verify email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(email.matches(emailPattern)){
            return true;
        }
        return false;
    }

    public static boolean verifyPassword(String password)
    {
        //Use regex
        int passwordLength = password.length();
        boolean isValid=false;
        isValid = (passwordLength>=6) ? true : false;
        return isValid;
    }
}
