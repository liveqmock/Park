package parkingos.com.bolink.models;

public class TokenTb {
    private Long id;
    private String username;
    private String token;

    @Override
    public String toString() {
        return "TokenTb{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
