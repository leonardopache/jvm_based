package com.sample.fiboserver;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class FiboServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiboServerApplication.class, args);
	}

}

// INITIALIZE
@Log4j2
@Component
class SampleInitializer {

	@EventListener(ApplicationReadyEvent.class)
	public void ready() {
		log.info("Fibo Server Working ...");
	}
}

// SERVICE
@Service
@AllArgsConstructor
class SampleService {
	public Flux<FiboResponse> postFibonacci(List<Integer> numbers) {
		return Flux.fromIterable(numbers)
				.flatMap(n -> {
					var result = fibonacciOf(n);
					return Flux.just(new FiboResponse(n, result));
				});
	}

	private String fibonacciOf(Integer numInteger) {
		if (numInteger == 0)
			return "0";
		if (numInteger == 1)
			return Arrays.asList(1, 2).toString();
		else {
			var array = new int[numInteger];
			array[0] = 0;
			array[1] = 1;

			for (int i = 2; i < numInteger; i++) {
				array[i] = array[i - 2] + array[i - 1];
			}
			return array.toString();
		}
	}
}

// REPOSITORY

// ENTITY

// CONTROLLER
@Log4j2
@RestController("/api")
@AllArgsConstructor
class SampleController {
	private final SampleService sampleService;

	@PostMapping("/calculate-fibos")
	public Flux<FiboResponse> postFibonacci(@RequestBody Integer fibos) {
		log.info("Calculate for {}", fibos);
		return sampleService.postFibonacci(List.of(fibos));
	}
}

// DTO
record FiboResponse(Integer num, String result) {
}