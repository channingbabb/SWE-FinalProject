import java.io.Serializable;

public class LoginData implements Serializable {
    private String username;
    private String password;
    private boolean success;
    
    public LoginData(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public LoginData(boolean success) {
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
