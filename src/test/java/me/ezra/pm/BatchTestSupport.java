package me.ezra.pm;


import me.ezra.pm.message.MessageRepository;
import me.ezra.pm.point.PointRepository;
import me.ezra.pm.point.reservation.PointReservationRepository;
import me.ezra.pm.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class BatchTestSupport {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    PointWalletRepository pointWalletRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    PointReservationRepository pointReservationRepository;

    protected JobExecution launchJob(Job job, JobParameters jobParameters) throws Exception {
        JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
        jobLauncherTestUtils.setJob(job);
        jobLauncherTestUtils.setJobLauncher(jobLauncher);
        jobLauncherTestUtils.setJobRepository(jobRepository);

        return jobLauncherTestUtils.launchJob(jobParameters);
    }

    @BeforeEach
    protected  void deleteAll() {
        pointRepository.deleteAll();
        pointReservationRepository.deleteAll();
        pointWalletRepository.deleteAll();
        messageRepository.deleteAll();
    }

}
