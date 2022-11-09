package me.ezra.pm.job.reserve;

import com.mysema.commons.lang.Pair;
import me.ezra.pm.job.reader.ReverseJpaPagingItemReader;
import me.ezra.pm.job.reader.ReverseJpaPagingItemReaderBuilder;
import me.ezra.pm.point.Point;
import me.ezra.pm.point.PointRepository;
import me.ezra.pm.point.reservation.PointReservation;
import me.ezra.pm.point.reservation.PointReservationRepository;
import me.ezra.pm.point.wallet.PointWallet;
import me.ezra.pm.point.wallet.PointWalletRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Configuration
public class ExecutePointReservationStepConfiguration {

    @Bean
    @JobScope
    public Step executePointReservationStep(
            StepBuilderFactory stepBuilderFactory,
            PlatformTransactionManager transactionManager,
            ReverseJpaPagingItemReader<PointReservation> executePointReservationItemReader,
            ItemProcessor<PointReservation, Pair<PointReservation, Point>> executePointReservationItemProcessor,
            ItemWriter<Pair<PointReservation, Point>> executePointReservationItemWriter
    ) {
        return stepBuilderFactory.get("executePointReservationStep")
                .allowStartIfComplete(true)
                .transactionManager(transactionManager)
                .<PointReservation, Pair<PointReservation,Point>>chunk(1000)
                .reader(executePointReservationItemReader)
                .processor(executePointReservationItemProcessor)
                .writer(executePointReservationItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ReverseJpaPagingItemReader<PointReservation> executePointReservationItemReader(
            PointReservationRepository pointReservationRepository ,
            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}")
            LocalDate today
    ) {
        return new ReverseJpaPagingItemReaderBuilder<PointReservation>()
                .name("executePointReservationItemReader")
                .query(pageable -> pointReservationRepository.findPointReservationToExecute(today, pageable))
                .pageSize(1)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<PointReservation, Pair<PointReservation, Point>> executePointReservationItemProcessor() {
        return reservation -> {
            reservation.setExecuted(true);
            Point earnedPoint = new Point(
                    reservation.getPointWallet(),
                    reservation.getAmount(),
                    reservation.getEarnedDate(),
                    reservation.getExpireDate()
            );
            PointWallet wallet = reservation.getPointWallet();
            wallet.setAmount(wallet.getAmount().add(earnedPoint.getAmount()));
            return Pair.of(reservation, earnedPoint);
        };
    }


    @Bean
    @StepScope
    public ItemWriter<Pair<PointReservation, Point>> executePointReservationItemWriter(
            PointReservationRepository pointReservationRepository,
            PointRepository pointRepository,
            PointWalletRepository pointWalletRepository
    ) {
        return reservationAndPointPairs -> {
            for (Pair<PointReservation, Point> pair : reservationAndPointPairs) {
                PointReservation reservation = pair.getFirst();
                Point point = pair.getSecond();
                pointReservationRepository.save(reservation);
                pointRepository.save(point);
                pointWalletRepository.save(reservation.getPointWallet());
            }
        };
    }
}
