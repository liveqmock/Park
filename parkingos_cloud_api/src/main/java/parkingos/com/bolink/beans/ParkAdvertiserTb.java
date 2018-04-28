package parkingos.com.bolink.beans;

public class ParkAdvertiserTb {

    private Long id;
    private Long parkId;
    private Long advertiserId;

    public Long getId() {
        return id;
    }

    public Long getParkId() {
        return parkId;
    }

    @Override
    public String toString() {
        return "ParkAdvertiserTb{" +
                "id=" + id +
                ", parkId=" + parkId +
                ", advertiserId=" + advertiserId +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setParkId(Long parkId) {
        this.parkId = parkId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Long getAdvertiserId() {
        return advertiserId;

    }
}
