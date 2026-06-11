package com.back.global.aspect;

import com.back.global.dto.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ResponseAspect {
    private final HttpServletResponse response;

    public ResponseAspect(HttpServletResponse response) {
        this.response = response;
    }

    @Around("""
            execution(public com.back.global.dto.ResponseDto *(..)) &&
                               (
                                   within(@org.springframework.stereotype.Controller *) ||
                                   within(@org.springframework.web.bind.annotation.RestController *)
                               ) &&
                               (
                                   @annotation(org.springframework.web.bind.annotation.GetMapping) ||
                                   @annotation(org.springframework.web.bind.annotation.PostMapping) ||
                                   @annotation(org.springframework.web.bind.annotation.PatchMapping) ||
                                   @annotation(org.springframework.web.bind.annotation.DeleteMapping) ||
                                   @annotation(org.springframework.web.bind.annotation.RequestMapping)
                               )
            """)
    public Object handleResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        // 원래 메서드 실행
        Object proceed = joinPoint.proceed();

        // ResponseDto 타입이면 상태 코드 설정
        ResponseDto<?> rsData = (ResponseDto<?>) proceed;
        response.setStatus(rsData.statusCode());
        return proceed;
    }
}