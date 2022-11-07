package me.ezra.pm.job.expire;

import me.ezra.pm.job.validator.TodayJobParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpirePointJobConfiguration {
    @Bean
    public Job expirePointJob(
            JobBuilderFactory jobBuilderFactory,
            TodayJobParameterValidator validator, // Job parameter에 today가 있는지 확인하는 validator
            Step expirePointStep
    ) {
        return jobBuilderFactory
                .get("expirePointJob")
                .validator(validator)
                .incrementer(new RunIdIncrementer())  // 같은 Job을 같은 JobParameter로 돌려도 해당 run.id가 계속해서 증가해서 Job이 중복실행된것으로 인지 하지 않음.
                .start(expirePointStep)
                .build();
    }
}
