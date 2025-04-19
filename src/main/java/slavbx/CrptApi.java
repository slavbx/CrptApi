package slavbx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CrptApi implements AutoCloseable {
    private final LimitedExecutor limitedExecutor;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String url = "https://ismp.crpt.ru/api/v3/";

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.limitedExecutor = new LimitedExecutor(timeUnit, requestLimit);
        this.objectMapper = new ObjectMapperCustom();
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public synchronized int addProductToTrade(Document document, String signature) {
        URI uri = URI.create(url + "lk/documents/create");
        String requestBody;

        ObjectNode objectNode = objectMapper.valueToTree(document);
        objectNode.put("signature", signature);
        try {
            requestBody = objectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при сериализации документа", e);
        }

        System.out.println("Отправляю запрос");
        Result result = limitedExecutor.execute(() -> sendPostRequest(uri, requestBody));

        if (result.hasError()) {
            System.out.println("Ошибка:  " + result.getException().getMessage());
            return -1;
        } else {
            return result.getStatusCode();
        }
    }

    private Result sendPostRequest(URI uri, String requestBody) {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new Result(response.statusCode());
        } catch (IOException  e) {
            return new Result(-1, new RuntimeException("Ошибка ввода-вывода при отправке запроса", e));
        } catch (InterruptedException e) {
            return new Result(-1, new RuntimeException("Запрос был прерван", e));
        }
    }

    @Override
    public void close() throws Exception {
        limitedExecutor.stop();
    }

    private class LimitedExecutor {
        private final Semaphore semaphore;
        private final ScheduledExecutorService executorService;
        private final int maxActions;
        private final TimeUnit timeUnit;

        private LimitedExecutor(TimeUnit timeUnit, int maxActions) {
            this.semaphore = new Semaphore(maxActions, true);
            this.executorService = Executors.newSingleThreadScheduledExecutor();
            this.maxActions = maxActions;
            this.timeUnit = timeUnit;
            start();
        }

        private Result execute(Callable<Result> task) {
            try {
                semaphore.acquire();
                try {
                    return task.call(); // Возвращаем результат выполнения задачи
                } catch (Exception e) {
                    return new Result(-1, e);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("Задача была прервана", e);
            }
         }

        private void start() {
            executorService.scheduleAtFixedRate(() -> {
                System.out.println("10 сек Освобождаю " + (maxActions - semaphore.availablePermits()));
                semaphore.release(maxActions - semaphore.availablePermits());
            }, 10, 10, timeUnit);
        }

        private void stop() {
            semaphore.release(maxActions - semaphore.availablePermits());
            executorService.shutdown();
        }
    }

    private class Result {
        private final int statusCode;
        private final Exception exception;

        private Result(int statusCode, Exception exception) {
            this.statusCode = statusCode;
            this.exception = exception;
        }

        private Result(int statusCode) {
            this.statusCode = statusCode;
            this.exception = null;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public Exception getException() {
            return exception;
        }

        public boolean hasError() {
            return exception != null;
        }
    }

    private class ObjectMapperCustom extends ObjectMapper {
        JavaTimeModule javaTimeModule;

        private ObjectMapperCustom() {
            javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            this.registerModule(javaTimeModule);
            this.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        }
    }
}