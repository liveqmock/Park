package parkingos.com.bolink.beans;

public class AdvertisementTb {
    private Long id;
    private Long advertiserId;
    private String description;
    private String redirectUrl;
    private String path;
    private Integer state;

    @Override
    public String toString() {
        return "AdvertisementTb{" +
                "id=" + id +
                ", advertiserId=" + advertiserId +
                ", description='" + description + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", path='" + path + '\'' +
                ", state=" + state +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getId() {

        return id;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public String getDescription() {
        return description;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getPath() {
        return path;
    }

    public Integer getState() {
        return state;
    }
}
