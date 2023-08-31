package com.pepperoni.prophunt;

public class PropHuntPlayer {
    private short propId;
    private byte propType;
    private byte orientation;
    private byte team;
    private byte status;
    private String username;

    public PropHuntPlayer(String username) {
        this.username = username;
    }

    public short getPropId() {
        return propId;
    }

    public void setPropId(short propId) {
        this.propId = propId;
    }

    public byte getPropType() {
        return propType;
    }

    public void setPropType(byte propType) {
        this.propType = propType;
    }

    public byte getOrientation() {
        return orientation;
    }

    public void setOrientation(byte orientation) {
        this.orientation = orientation;
    }

    public byte getTeam() {
        return team;
    }

    public void setTeam(byte team) {
        this.team = team;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "propId=" + propId +
                ", propType=" + propType +
                ", orientation=" + orientation +
                ", team=" + team +
                ", status=" + status +
                ", username='" + username + '\'' +
                '}';
    }
}
