package com.back.domain.coffee.service;

import com.back.domain.coffee.dto.CoffeeResponseDto;
import com.back.domain.coffee.entity.Coffee;
import com.back.domain.coffee.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
//@Transactional 이걸 쓸까요? 말까요? 쓰면 메소스단에 단순 Transactional은 제거가능
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;

    @Transactional(readOnly = true)
    public List<CoffeeResponseDto> getCoffees() {
        return coffeeRepository.findAll()
                .stream()
                .map(CoffeeResponseDto::from)
                .toList();

    }

    //해당 메소드의 기능은 뭐일까요? 테스트용도일까요?
    @Transactional(readOnly = true)
    public Optional<Coffee> findLatest() {
        return coffeeRepository.findFirstByOrderByIdDesc();
    }


    @Transactional
    public Coffee create(
            String name,
            int price,
            int stock,
            String imgUrl
    ) {
        final Coffee coffee = new Coffee(name, price, stock, imgUrl);
        // 흐음~ entity를 바로 넘기는건 위험하다고 하네요
        // 저희 수업시간에 post & comment처럼 서로 참조하는 상황일 때
        // 화면에 계속 연쇄적으로 붙어서 출력된 거 기억하실까요? 그런 문제가 발생 할 수 도있고
        // DB값이 그대로 화면으로 노출 될 위험이 있기에 Service단에서 포장까지 다 하고
        // Controller로 넘기는게 더 안전하다고하네용?? 허헣 덕분에 배워갑니다.
        return coffeeRepository.save(coffee);
    }
}