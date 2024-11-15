package com.example.reactapi.app;

import com.example.reactapi.app.FakeRepository.NameData;
import com.giffing.bucket4j.spring.boot.starter.context.metrics.MetricHandler;
import com.giffing.bucket4j.spring.boot.starter.context.metrics.MetricTagResult;
import com.giffing.bucket4j.spring.boot.starter.context.metrics.MetricType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EnableCaching
public class ReactRestApiRequestLimitApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReactRestApiRequestLimitApplication.class, args);
	}
}


@Log4j2
@Component
class FakeRepository {
	private static List<NameData> nameDataStream = new ArrayList<>();
	record NameData(String name) {}

	static {
		nameDataStream.add(new NameData("Stewart"));
		nameDataStream.add(new NameData("Brandon"));
		nameDataStream.add(new NameData("Maria"));
		log.info("====> data loaded {}", nameDataStream.stream().map(NameData::name).collect(Collectors.joining(",")));
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	static class Actions {
		public static List<NameData> getAll() {
			return nameDataStream;
		}

		public static void create(String name) {
			nameDataStream = Stream.concat(nameDataStream.stream(), Stream.of(new NameData(name))).toList();
		}
	}
}

// INIT
@Log4j2
@Component
class Init {
	@EventListener(ApplicationReadyEvent.class)
	public void ready() {
		log.info("====> {}", this.getClass().getName());
	}
}

// SERVICE
@Log4j2
@Service
@AllArgsConstructor
class NameService {
	private final FakeRepository daoRepo;
	public Flux<String> getAllNames() {
		log.info("====> {}", this.getClass().getName());
		return Flux.fromIterable(FakeRepository.Actions.getAll())
				.map(NameData::name);
	}

	public Mono<Void> create(String name) {
		FakeRepository.Actions.create(name);
		return Mono.empty().then();
	}
}

// CONTROLLER
@Log4j2
@RestController
@RequestMapping("api/v1/names")
@AllArgsConstructor
class NameController {

	private final NameService nameService;

	@GetMapping(produces = "application/json")
	public ResponseEntity<Mono<List<String>>> getAll() {
		log.info("====> {}", this.getClass().getName());
		return ResponseEntity.ok(nameService.getAllNames().collectList());
	}

	@PostMapping(consumes = "text/plain")
	public ResponseEntity<Mono<Void>> create(@RequestBody String name) {
		return new ResponseEntity<>(nameService.create(name), HttpStatus.CREATED);
	}
}

//Only for print the metric as log
@Component
@Slf4j
class DebugMetricHandler implements MetricHandler {

	/*
	 These are the Metric types available:
	  CONSUMED_COUNTER
	  REJECTED_COUNTER
	  PARKED_COUNTER
	  INTERRUPTED_COUNTER
	  DELAYED_COUNTER
	 */
	@Override
	public void handle(MetricType type, String name, long tokens, List<MetricTagResult> tags) {
		log.info(String.format("type: %s; name: %s; tags: %s",
				type,
				name,
				tags.stream()
					.map(mtr -> mtr.getKey() + ":" + mtr.getValue())
					.collect(Collectors.joining(","))));
	}
}
