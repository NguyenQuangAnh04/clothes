package com.example.clothes.dto;

import lombok.Data;
@Data
public class OTPDTO {
    private String otp;
    private Long timeStamp;
    public OTPDTO(String otp){
        this.otp = otp;
        this.timeStamp = System.currentTimeMillis();
    }
    public boolean isExpired() {
        return System.currentTimeMillis() - timeStamp > 5 * 60 * 1000;
    }

}
