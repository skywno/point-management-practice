package me.ezra.pm.point;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigInteger;


@Getter
public class ExpiredPointSummary {


    private String userId;
    private BigInteger amount;

    @QueryProjection
    public ExpiredPointSummary(String userId, BigInteger amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
