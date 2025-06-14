package com.sample.fiboserver;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class FiboServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(FiboServerApplication.class, args);
	}
}

// INITIALIZE
@Slf4j
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
				// One of my misunderstanding, async doesn't mean in parallel. To be parallel
				// need declare it.
				.parallel()
				.runOn(Schedulers.boundedElastic())
				.flatMap(number -> Flux.just(new FiboResponse(number, fibonacciOf(number))))
				.sequential();
	}

	private String fibonacciOf(Integer numInteger) {
		if (numInteger == 0)
			return "0";
		if (numInteger == 1)
			return "1";
		else {
			var array = new BigInteger[numInteger];
			array[0] = BigInteger.ZERO;
			array[1] = BigInteger.valueOf(1);

			for (int i = 2; i < numInteger; i++) {
				array[i] = array[i - 2].add(array[i - 1]);
			}
			return Arrays.stream(array)
					.map(String::valueOf)
					.collect(Collectors.joining(","));
		}
	}
}

// REPOSITORY

// ENTITY

// CONTROLLER
@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
class SampleController {
	private final SampleService sampleService;

	@GetMapping
	public void root() {
		log.info("Server is UP");
	}

	@PostMapping("/calculate-fibos")
	public Flux<FiboResponse> postFibonacci(@RequestBody FiboRequest fibos) {
		log.info("Calculate for {}", fibos);
		return sampleService.postFibonacci(fibos.numbers());
	}
}

// DTO
record FiboRequest(List<Integer> numbers) {
}

record FiboResponse(Integer num, String result) {
}