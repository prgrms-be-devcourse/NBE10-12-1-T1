package com.back.domain.order.job;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderBatchConfig {

    // GlobalConfig에서 정의한 리스너와 ID 생성기를 주입받아 조립
    @Bean
    public Job orderDeliveryJob(JobRepository jobRepository,
                            Step orderDeliveryStep,
                            JobExecutionListener globalJobListener, // 👈 공통 리스너 가져옴
                            RunIdIncrementer globalRunIdIncrementer) { // 👈 공통 생성기 가져옴
        
        return new JobBuilder("orderDeliveryJob", jobRepository)
                .incrementer(globalRunIdIncrementer) // 매번 강제 실행되도록 파라미터 ID 증가
                .listener(globalJobListener)         // 실패하면 슬랙 알람 오도록 부착
                .start(orderDeliveryStep)
                .build();
    }
    
    // Step 정의 생략...
}