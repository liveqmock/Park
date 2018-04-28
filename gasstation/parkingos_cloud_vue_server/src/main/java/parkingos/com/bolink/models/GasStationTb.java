package parkingos.com.bolink.models;

public class GasStationTb {
    private Long id;
    private String name;
    private String key;
    private String address;
    private Integer deviceTotal;
    private String phone;
    private String linkman;
    private Long createTime;
    private Integer state;

    @Override
    public String toString() {
        return "GasStationTb{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", address='" + address + '\'' +
                ", deviceTotal=" + deviceTotal +
                ", phone='" + phone + '\'' +
                ", linkman='" + linkman + '\'' +
                ", createTime=" + createTime +
                ", state=" + state +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getDeviceTotal() {
        return deviceTotal;
    }

    public void setDeviceTotal(Integer deviceTotal) {
        this.deviceTotal = deviceTotal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
