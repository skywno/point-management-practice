package me.ezra.pm.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.ezra.pm.point.IdEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Message extends IdEntity {

    @Column(name = "user_id", unique = true, nullable = false)
    String userId;

    // 메시지 제목
    @Column(name = "title", nullable = false)
    String title;
    // 메시지 내용
    @Column(name = "content", nullable = false, columnDefinition = "text")
    String content;

    public static Message expiredPointMessageInstance(
            String userId,
            LocalDate expiredDate,
            BigInteger expiredAmount
    ) {
        return new Message(
                userId,
                String.format("%s points expired", expiredAmount.toString()),
                String.format("As of %s, %s points expired.", expiredDate.format(DateTimeFormatter.ISO_DATE), expiredAmount)
        );
    }

    public static Message expireSoonPointMessageInstance(
            String userId,
            LocalDate expireDate,
            BigInteger expireAmount
    ) {
        return new Message(
                userId,
                String.format("%s 포인트 만료 예정", expireAmount.toString()),
                String.format("%s 까지 %s 포인트가 만료 예정입니다.", expireDate.format(DateTimeFormatter.ISO_DATE), expireAmount)
        );
    }
}
