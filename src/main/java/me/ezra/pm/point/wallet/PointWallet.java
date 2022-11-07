package me.ezra.pm.point.wallet;

import lombok.*;
import me.ezra.pm.point.IdEntity;
import me.ezra.pm.point.Point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointWallet extends IdEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    String userId;

    @Setter
    @Column(name = "amount", nullable = false, columnDefinition = "BIGINT")
    BigInteger amount;

    public void expire(Point point) {
        if (this.getId().equals(point.getPointWallet().getId()) && point.isExpired()) {
            this.amount = this.amount.subtract(point.getAmount());
        }
    }
}
