package slavbx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
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

public class CrptApi {
    private Limiter limiter;
    private HttpClient client;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.limiter = new Limiter(timeUnit, requestLimit);
        limiter.start();
    }

    public int addProductToTrade(Document document, String signature) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        int statusCode = 0;
        if (limiter.tryAcquire()) {
            String documentJson = null;
            try {
                documentJson = objectMapper.writeValueAsString(document);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            System.out.println(documentJson);

            HttpRequest request = HttpRequest.newBuilder(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                    .header("Content-Type", "application/json")
                    .header("Signature", signature)
                    .POST(HttpRequest.BodyPublishers.ofString(documentJson))
                    .build();

            client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                statusCode = response.statusCode();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return statusCode;
    }

    public void stopLimiter() {
        limiter.stop();
    }

    private class Limiter {
        private Semaphore semaphore;
        private int maxActions;
        private TimeUnit timeUnit;
        private ScheduledExecutorService scheduler;

        public Limiter(TimeUnit timeUnit, int maxActions) {
            this.timeUnit = timeUnit;
            this.maxActions = maxActions;
            this.semaphore = new Semaphore(maxActions);
            this.scheduler = Executors.newScheduledThreadPool(1);
        }

        public boolean tryAcquire() {
            return semaphore.tryAcquire();
        }

        public void stop() {
            scheduler.shutdownNow();
        }

        public void start() {
            scheduler.schedule(() -> semaphore.release(maxActions - semaphore.availablePermits()), 1, timeUnit);
        }
    }



}