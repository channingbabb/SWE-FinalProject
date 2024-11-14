package finalProject;

import ocsf.client.AbstractClient;

public class PlayerClient extends AbstractClient{

    private LoginControl loginControl;
    //private CreateAccountControl createAccountControl;

    public void setLoginControl(LoginControl loginControl)
    {
        this.loginControl = loginControl;
    }

    /*public void setcreateaccountcontrol(createaccountcontrol createaccountcontrol)
    {
        this.createaccountcontrol = createaccountcontrol;
    }*/

    public PlayerClient()
    {
        super("localhost", 8300);
    }

    public void handleMessageFromServer(Object arg0)
    {
        if (arg0 instanceof String)
        {
            // Get the text of the message.
            String message = (String)arg0;

            if (message.equals("LoginSuccessful"))
            {
                loginControl.loginSuccess();
            }

            else if (message.equals("CreateAccountSuccessful"))
            {
                //createAccountControl.createAccountSuccess();
            }
        }

        else if (arg0 instanceof Error)
        {
            Error error = (Error)arg0;

            if (error.getCause().equals("Login"))
            {
                loginControl.displayError(error.getMessage());
            }

            else if (error.getCause().equals("CreateAccount"))
            {
                //createAccountControl.displayError(error.getMessage());
            }
        }
    }
}
