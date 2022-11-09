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
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;

import java.time.LocalDate;

public class ExecutePointReservationPartitionStepConfiguration {

    /**
     * 파티셔닝 사용
     * 단, 동시성 문제가 있어서 사용이 불가합니다.
     */

    @Bean
    @JobScope
    public Step executePointReservationMasterStep(
            StepBuilderFactory stepBuilderFactory,
            TaskExecutorPartitionHandler partitionHandler,
            PointReservationRepository pointReservationRepository,
            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}")
            LocalDate today
    ) {
        return stepBuilderFactory
                .get("executePointReservationMasterStep")
                .partitioner(
                        "executePointReservationStep",
                        new ExecutePointReservationStepPartitioner(pointReservationRepository, today)
                ).partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler partitionHandler(
            Step executePointReservationStep,
            TaskExecutor taskExecutor
    ) {
        TaskExecutorPartitionHandler partitionHandler =
                new TaskExecutorPartitionHandler();
        partitionHandler.setStep(executePointReservationStep);
        partitionHandler.setGridSize(8);
        partitionHandler.setTaskExecutor(taskExecutor);
        return partitionHandler;
    }


    @Bean
    @StepScope

    public ReverseJpaPagingItemReader<PointReservation> executePointReservationItemReader(
            PointReservationRepository pointReservationRepository ,
            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}")
            LocalDate today,
            @Value("#{stepExecutionContext[minId]}") Long minId,
            @Value("#{stepExecutionContext[maxId]}") Long maxId
    ) {
        return new ReverseJpaPagingItemReaderBuilder<PointReservation>()
                .name("executePointReservationItemReader")
                .query(pageable -> pointReservationRepository.findPointReservationToExecute(today, minId, maxId, pageable))
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
