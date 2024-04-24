https://spring.io/blog/2022/10/12/observability-with-spring-boot-3

- Grafana datasources
  - https://grafana.com/docs/grafana/latest/datasources/

Case of Microservices can't be scraped: 

App A / App B  -> only Push (Metrics/Logs/Tracing)

Metrics:
APP A and APP B locally has a Prometheus instance to scrap the metrics and uses `remote_write` to send this metrics to external TSDB (Grafana Mimir).
```
App A   |                       |
          <--- Prometheus ----> | Mimir     
App B   |                       |
```

Logs:
APP A and APP B with spring boot and loki-logback-appender push the logs to the external Loki instance.
```
App A    --- loki-logback-appender ---->    |
                                            | Loki     
App B    --- loki-logback-appender ---->    |
```

