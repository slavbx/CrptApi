package slavbx;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;

public class CrptApi {
    private Limiter limiter;
    private HttpClient client;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.limiter = new Limiter(timeUnit, requestLimit);
        limiter.start();
    }

    public int addProductToTrade(Document.Document document, String signature) {
        int statusCode = 0;
        if (limiter.tryAcquire()) {
            String documentJson = new Gson().toJson(document);
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