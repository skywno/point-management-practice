package me.ezra.pm.point;


import lombok.*;
import me.ezra.pm.point.wallet.PointWallet;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Point extends IdEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="point_wallet_id", nullable = false)
    PointWallet pointWallet;

    @Column(name="amount", nullable=false, columnDefinition = "BIGINT")
    BigInteger amount;

    @Column(name="earned_date", nullable = false)
    LocalDate earnedDate;

    @Column(name="expire_date", nullable = false)
    LocalDate expireDate;

    @Setter
    @Column(name="is_used", nullable = false, columnDefinition = "TINYINT")
    boolean used;

    @Setter
    @Column(name="is_expired", nullable = false, columnDefinition = "TINYINT")
    boolean expired;

    public Point(PointWallet pointWallet, BigInteger amount, LocalDate earnedDate,
                 LocalDate expireDate) {
        this.pointWallet = pointWallet;
        this.amount = amount;
        this.earnedDate = earnedDate;
        this.expireDate = expireDate;
        this.used = false;
        this.expired = false;
    }

    public void expire(){
        if (!used){
            expired = true;
        }
    }
}
