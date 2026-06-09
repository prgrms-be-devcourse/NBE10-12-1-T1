package com.back.domain.order.job;

import com.back.domain.order.entity.Order;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Configuration
public class OrderBatchConfig {

    private static final int CHUNK_SIZE = 100;

    // ---------------------------------------------------------
    // 👉 1. Reader: DB에서 데이터 읽어오기 (JpaPagingItemReader)
    // ---------------------------------------------------------
    @Bean
    public JpaPagingItemReader<Order> myReader(EntityManagerFactory entityManagerFactory) {
        LocalDateTime startDate = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(14, 0));
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 59, 59));

        return new JpaPagingItemReaderBuilder<Order>()
                .name("OrderReader")
                .entityManagerFactory(entityManagerFactory)
                // 실행할 JPQL 쿼리 (결제완료이면서 어제 14시 이후 오늘 13시59분59초 이전 상태)
                .queryString("SELECT o.status FROM Order o WHERE o.status = 'PAYMENT_COMPLETE' AND o.created_at BETWEEN :startDate AND :endDate")
                .parameterValues(Map.of(
                        "startDate", startDate,
                        "endDate", endDate
                ))
                // 한 번에 가져올 페이지 사이즈 (보통 Chunk Size와 동일하게 맞춤)
                .pageSize(CHUNK_SIZE)
                .build();
    }

    // ---------------------------------------------------------
    // 👉 2. Processor: 데이터 가공하기
    // ---------------------------------------------------------
    @Bean
    public ItemProcessor<Order, Order> myProcessor() {
        return order -> {
            // 배송상태 변경(결제 완료 -> 상품 준비 중)
            order.advanceToNextStatus();
//            if(order.getStatus() == DELIVERED) return null;
            return order; // 가공된 데이터를 Writer로 넘김
        };
    }

    // ---------------------------------------------------------
    // 👉 3. Writer: 처리된 데이터 DB에 반영하기 (JpaItemWriter)
    // ---------------------------------------------------------
    @Bean
    public JpaItemWriter<Order> myWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Order>()
                .entityManagerFactory(entityManagerFactory)
                // JpaItemWriter는 내부적으로 EntityManager.merge()를 사용해서
                // 변경된 엔티티를 DB에 알아서 Update 해줌
                .build();
    }

    // ---------------------------------------------------------
    // ⚙️ Step 조립하기 (위에서 만든 3가지를 합침)
    // ---------------------------------------------------------
    @Bean
    public Step orderDeliveryStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  JpaPagingItemReader<Order> myReader, // 파라미터로 주입받는 게 더 깔끔함
                                  ItemProcessor<Order, Order> myProcessor,
                                  JpaItemWriter<Order> myWriter) {

        return new StepBuilder("orderDeliveryStep", jobRepository)
                .<Order, Order>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(myReader)
                .processor(myProcessor)
                .writer(myWriter)
                .build();
    }


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
    
}