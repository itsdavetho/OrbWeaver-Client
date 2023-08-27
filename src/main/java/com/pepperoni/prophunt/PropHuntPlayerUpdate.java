package com.pepperoni.prophunt;

public class PropHuntPlayerUpdate {
    private final short propId;
    private final byte propType;
    private final byte orientation;
    private final byte team;
    private final byte status;
    private final String username;

    public PropHuntPlayerUpdate(short propId, byte propType, byte orientation, byte team, byte status, String username) {
        this.propId = propId;
        this.propType = propType;
        this.orientation = orientation;
        this.team = team;
        this.status = status;
        this.username = username;
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
