package fpt.aptech.api.service;

import fpt.aptech.api.models.Users;


public interface IAuth {

    Users register(Users user);

    Users login(String email, String password);
    
    Users loginWithGoogle(String idToken);
    
    boolean checkVerifyCode(int userId, String verifyCode);

}
