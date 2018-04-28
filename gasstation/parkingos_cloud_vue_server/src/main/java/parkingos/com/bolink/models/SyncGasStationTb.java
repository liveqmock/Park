package parkingos.com.bolink.models;

/**
 * 加油站信息同步表
 */
public class SyncGasStationTb {

    private Long id;//主键
    private String parkedId;//加油站id
    private String deviceId;//加油机id
    private String info;//LED需要显示的信息
    private Integer status;//0 不开闸  1开闸
    private Long timeStamp;//时间戳
    private Integer sendState; //发送到sdk状态  0 发送失败  1发送成功
    private Integer sdkState; //sdk处理返回结果   0 未处理  1处理成功

    @Override
    public String toString() {
        return "SyncGasStationTb{" +
                "id=" + id +
                ", parkedId='" + parkedId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", info='" + info + '\'' +
                ", status=" + status +
                ", timeStamp=" + timeStamp +
                ", sendState=" + sendState +
                ", sdkState=" + sdkState +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParkedId() {
        return parkedId;
    }

    public void setParkedId(String parkedId) {
        this.parkedId = parkedId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getSendState() {
        return sendState;
    }

    public void setSendState(Integer sendState) {
        this.sendState = sendState;
    }

    public Integer getSdkState() {
        return sdkState;
    }

    public void setSdkState(Integer sdkState) {
        this.sdkState = sdkState;
    }
}
