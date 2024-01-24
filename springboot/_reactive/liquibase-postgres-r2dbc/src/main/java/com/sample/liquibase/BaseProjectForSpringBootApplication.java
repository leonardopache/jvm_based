package com.sample.liquibase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SpringBootApplication
public class BaseProjectForSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseProjectForSpringBootApplication.class, args);
    }
}

// INITIALIZE
@Log4j2
@Component
@AllArgsConstructor
class SampleInitializer {
    private final SampleRepository repository;

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        log.info("Working on sample initializer...");
        Flux<Sample> samples = Flux.just("Madhura", "Josh", "Olga")
                .map(Sample::new);

        repository
                .findAll()
                .thenMany(repository.saveAll(samples))
                .subscribe(log::info);
    }
}

// SERVICE
@Log4j2
@Service
@AllArgsConstructor
class SampleService {

    {
        log.info("Working on sample service...");
    }

    private final SampleRepository repository;

    public Flux<Sample> saveAll(Flux<Sample> samples) {
        return repository.saveAll(samples);
    }

    public Flux<SampleDTO> getAll() {
        return repository.findAll()
                .map(SampleDTO::fromEntity);
    }
}

// REPOSITORY
interface SampleRepository extends ReactiveCrudRepository<Sample, UUID> {}

// ENTITY
@Table("samples")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Sample {
    @Id
    private UUID id;
    @Column(value = "name")
    private String name;
    @Column(value = "status")
    private String status;

    public Sample(String name) {
        this.name = name;
        this.status = "ACTIVE";
    }
}

// CONTROLLER
@Log4j2
@RestController("/")
@AllArgsConstructor
class SampleController {
    private final SampleService sampleService;

    @GetMapping
    public Mono<String> getRoot() {
        log.info("Working on controller...");
        return Mono.just("alive");
    }

    @GetMapping("all")
    public Flux<SampleDTO> getAllRegistres() {
        log.info("Working on controller...");
        return sampleService.getAll();
    }
}

// DTO
record SampleDTO(String name, String status) {
    public Sample toEntity() {
        return Sample.builder()
                .name(name)
                .status(status)
                .build();
    }

    public static SampleDTO fromEntity(Sample entity) {
        return new SampleDTO(entity.getName(), entity.getStatus());
    }
}
