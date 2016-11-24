package com.example.license;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class LicenseData {
    private int userNum;
    private long spaceSum;
    private Date endTime;

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public long getSpaceSum() {
        return spaceSum;
    }

    public void setSpaceSum(long spaceSum) {
        this.spaceSum = spaceSum;
    }

    @JsonSerialize(using = DateSerializer.class)
    public Date getEndTime() {
        return endTime;
    }

    @JsonDeserialize(using = DateDeserializer.class)
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

}
