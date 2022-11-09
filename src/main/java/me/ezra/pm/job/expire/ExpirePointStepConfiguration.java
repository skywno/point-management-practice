package me.ezra.pm.job.expire;

import me.ezra.pm.job.reader.ReverseJpaPagingItemReader;
import me.ezra.pm.job.reader.ReverseJpaPagingItemReaderBuilder;
import me.ezra.pm.point.Point;
import me.ezra.pm.point.PointRepository;
import me.ezra.pm.point.wallet.PointWallet;
import me.ezra.pm.point.wallet.PointWalletRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class ExpirePointStepConfiguration {

    @Bean
    @JobScope
    public Step expirePointStep(
            StepBuilderFactory stepBuilderFactory,
            PlatformTransactionManager transactionManager,
            ReverseJpaPagingItemReader<Point> expirePointItemReader,
            ItemProcessor<Point, Point> expirePointItemProcessor,
            ItemWriter<Point> expirePointItemWriter
    ) {
        return stepBuilderFactory
                .get("expirePointStep")// step 이름름
                .allowStartIfComplete(true) // step 중복 실행 가능???
                .transactionManager(transactionManager)
                .<Point, Point>chunk(1000)
                .reader(expirePointItemReader)
                .processor(expirePointItemProcessor)
                .writer(expirePointItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ReverseJpaPagingItemReader<Point> expirePointItemReader(
            PointRepository pointRepository,
            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}") LocalDate today // JobParameter 를 가져와서 LocalDate 로 converting 함
    ) {
        return new ReverseJpaPagingItemReaderBuilder<Point>()
                .name("ExpirePointItemReader")
                .query(
                        pageable -> pointRepository.findPointToExpire(today, pageable)
                )
                .sort(Sort.by(Sort.Direction.ASC, "id"))
                .pageSize(1)
                .build();

    }

    @Bean
    @StepScope
    public ItemProcessor<Point, Point> expirePointItemProcessor() {
        return point -> {
            point.setExpired(true); // point 의 만료 상태를 true 로 바꿈
            PointWallet wallet = point.getPointWallet();
            wallet.expire(point); // point 안에 있는 PointWallet 의 잔액을 차감함
            return point;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Point> expirePointItemWriter(
            PointRepository pointRepository,
            PointWalletRepository pointWalletRepository
    ) {
        return points -> {
            for (Point point : points) {
                if (point.isExpired()) {
                    pointRepository.save(point); //Processor 에서 수정한 Point 와
                    // PointWallet 을 수정함
                    pointWalletRepository.save(point.getPointWallet());
                }
            }
        };
    }
}
