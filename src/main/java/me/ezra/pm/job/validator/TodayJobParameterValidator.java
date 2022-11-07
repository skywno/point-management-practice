package me.ezra.pm.job.validator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;


@Component
public class TodayJobParameterValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if (parameters == null)
            throw new JobParametersInvalidException("Job parameter today is required");

        String todayStr = parameters.getString("today");
        if (todayStr.isBlank())
            throw new JobParametersInvalidException("Job parameter today is required");

        try {
            LocalDate.parse(todayStr);
        } catch (DateTimeParseException exception) {
            throw new JobParametersInvalidException("Job parameter today is required");
        }
    }
}
