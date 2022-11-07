package me.ezra.pm.job.message;

import me.ezra.pm.BatchTestSupport;
import me.ezra.pm.message.Message;
import me.ezra.pm.message.MessageRepository;
import me.ezra.pm.point.Point;
import me.ezra.pm.point.PointRepository;
import me.ezra.pm.point.wallet.PointWallet;
import me.ezra.pm.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

class
MessageExpiredPointJobConfigurationTest extends BatchTestSupport {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    PointWalletRepository pointWalletRepository;

    @Autowired
    PointRepository pointRepository;

    @Autowired
    Job messageExpiredPointJob;

    @Test
    void messageExpiredPointJob() throws Exception {
        // Given
        LocalDate earnedDate = LocalDate.of(2022, 1, 1);
        LocalDate expiredDate = LocalDate.of(2022, 11, 3);
        LocalDate today = LocalDate.of(2022, 11, 4);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        PointWallet pointWallet1 = new PointWallet("user1", BigInteger.valueOf(1000));
        PointWallet pointWallet2 = new PointWallet("user2", BigInteger.valueOf(4000));

        pointWalletRepository.save(pointWallet1);
        pointWalletRepository.save(pointWallet2);

        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000),
                earnedDate, expiredDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000),
                earnedDate, expiredDate, false, true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000),
                earnedDate, today.plusDays(5)));
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(1000),
                earnedDate, expiredDate, false, true));
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(1000),
                earnedDate, today.plusDays(5)));

        // When
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", today.format(formatter))
                .addDate("date", new Date())
                .toJobParameters();
        JobExecution jobExecution = launchJob(messageExpiredPointJob, jobParameters);

        // Then
        then(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        then(pointWalletRepository.findAll()).hasSize(2);
        then(pointRepository.findAll()).hasSize(5);
        then(pointRepository.findByExpiredTrue()).hasSize(3);
        List<Message> messages = messageRepository.findAll();
        then(messages).hasSize(2);
        Message message1 = messages.stream().filter(item -> item.getUserId().equals("user1")).findFirst().orElseGet(null);
        then(message1).isNotNull();
        then(message1.getTitle()).isEqualTo("2000 points expired");
        then(message1.getContent()).isEqualTo("As of 2022-11-04, 2000 points expired.");
        Message message2 = messages.stream().filter(item -> item.getUserId().equals("user2")).findFirst().orElseGet(null);
        then(message2).isNotNull();
        then(message2.getTitle()).isEqualTo("1000 points expired");
        then(message2.getContent()).isEqualTo("As of 2022-11-04, 1000 points expired.");
    }
}