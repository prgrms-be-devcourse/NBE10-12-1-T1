package com.back.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


// BackApplication 에 있던 @EnableJpaAuditing 을 이곳으로 옮겼습니다.
// 웹 테스트 시 "JPA metamodel must not be empty" 오류 발생으로 테스트가 실패하는데,
// 우회 방법이 있으나, 설정이 복잡해서 LLM에게 물어보니 팀프로젝트에서는
// 다른 팀원분들도 테스트 때마다 같은 에러로 우회 코드를 작성해야 해서
// 분리하는게 낫다고 해서 분리 했습니다.

@EnableJpaAuditing
@Configuration
public class JpaAuditingConfig {
}
