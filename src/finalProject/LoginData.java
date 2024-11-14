package finalProject;

import java.io.Serializable;

public class LoginData implements Serializable {
	private String username;
	private String password;

	// Getters
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	// Setters
	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// Initializes the username and password
	public LoginData(String username, String password) {
		setUsername(username);
		setPassword(password);
	}
}
