package SWEFinalProject;

import java.io.Serializable;

//CreateAccountData class is used as data container for transfering info about creating account between client and server      
public class CreateAccountData implements Serializable {
    private String username;
    private String password;
    private boolean success;
    
    public CreateAccountData(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public CreateAccountData(boolean success) {
        this.success = success;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public boolean isSuccess() {
        return success;
    }
}
