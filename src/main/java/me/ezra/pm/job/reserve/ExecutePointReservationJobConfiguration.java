package me.ezra.pm.job.reserve;

import me.ezra.pm.job.validator.TodayJobParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutePointReservationJobConfiguration {

    @Bean
    public Job executePointReservationJob(
            JobBuilderFactory jobBuilderFactory,
            Step executePointReservationStep,
            TodayJobParameterValidator validator
    ){
        return jobBuilderFactory.get("executePointReservationJob")
                .incrementer(new RunIdIncrementer())  // 같은 Job을 같은 JobParameter로 돌려도 해당 run.id가 계속해서 증가해서 Job이 중복실행된것으로 인지 하지 않음.
                .validator(validator)
                .start(executePointReservationStep)
                .build();
    }
}
