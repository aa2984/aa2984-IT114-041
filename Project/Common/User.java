package Project.Common;

public class User {
    private long clientId = Constants.DEFAULT_CLIENT_ID;

    private String clientName;

    public User() {
    
    }

    public User(long clientId, String clientName) {
        this.clientId = clientId;
        this.clientName = clientName;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;

    
    }

    public void setClientName() {
        return clientName;
    
    }

    

    public String getDisplayName() {
        return String.format("%s#%s", this.clientName, this.clientId);
    
    }

    public void reset() {
        this.clientId = Constants.DEFAULT_CLIENT_ID; 
        this.clientName = null;
    }

}