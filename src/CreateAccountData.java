import java.io.Serializable;

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
