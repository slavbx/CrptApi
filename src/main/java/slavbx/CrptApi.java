package slavbx;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

public class CrptApi {
    private Limiter limiter;
    private HttpClient client;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.limiter = new Limiter(timeUnit, requestLimit);
        limiter.start();
    }

    public int addProductToTrade(Document document, String signature) {
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

    public static class Document {

        public static class Description{
            public String participantInn;

            public void setParticipantInn(String participantInn) {
                this.participantInn = participantInn;
            }
        }

        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private ArrayList<Product> products;
        private String reg_date;
        private String reg_number;

        private Document(Builder builder) {
            this.description = builder.description;
            this.doc_id = builder.doc_id;
            this.doc_status = builder.doc_status;
            this.doc_type = builder.doc_type;
            this.importRequest = builder.importRequest;
            this.owner_inn = builder.owner_inn;
            this.participant_inn = builder.participant_inn;
            this.producer_inn = builder.producer_inn;
            this.production_date = builder.production_date;
            this.production_type = builder.production_type;
            this.products = builder.products;
            this.reg_date = builder.reg_date;
            this.reg_number = builder.reg_number;
        }

        public static class Builder {
            private Description description;
            private String doc_id;
            private String doc_status;
            private String doc_type;
            private boolean importRequest;
            private String owner_inn;
            private String participant_inn;
            private String producer_inn;
            private String production_date;
            private String production_type;
            private ArrayList<Product> products;
            private String reg_date;
            private String reg_number;

            public Builder() {
                this.products = new ArrayList<>();
            }

            public Builder setDescription(String participantInn) {
                Description description = new Description();
                description.setParticipantInn(participantInn);
                this.description = description;
                return this;
            }

            public Builder setDoc_id(String doc_id) {
                this.doc_id = doc_id;
                return this;
            }

            public Builder setDoc_status(String doc_status) {
                this.doc_status = doc_status;
                return this;
            }

            public Builder setDoc_type(String doc_type) {
                this.doc_type = doc_type;
                return this;
            }

            public Builder setImportRequest(boolean importRequest) {
                this.importRequest = importRequest;
                return this;
            }

            public Builder setOwner_inn(String owner_inn) {
                this.owner_inn = owner_inn;
                return this;
            }

            public Builder setParticipant_inn(String participant_inn) {
                this.participant_inn = participant_inn;
                return this;
            }

            public Builder setProducer_inn(String producer_inn) {
                this.producer_inn = producer_inn;
                return this;
            }

            public Builder setProduction_date(String production_date) {
                this.production_date = production_date;
                return this;
            }

            public Builder setProduction_type(String production_type) {
                this.production_type = production_type;
                return this;
            }

            public Builder addProduct(Product product) {
                this.products.add(product);
                return this;
            }

            public Builder setReg_date(String reg_date) {
                this.reg_date = reg_date;
                return this;
            }

            public Builder setReg_number(String reg_number) {
                this.reg_number = reg_number;
                return this;
            }

            public Document build() {
                return new Document(this);
            }
        }
    }

    public static class Product{
        private String certificate_document;
        private String certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private String production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;

        private Product(Builder builder) {
            this.certificate_document = builder.certificate_document;
            this.certificate_document_date = builder.certificate_document_date;
            this.certificate_document_number = builder.certificate_document_number;
            this.owner_inn = builder.owner_inn;
            this.producer_inn = builder.producer_inn;
            this.production_date = builder.production_date;
            this.tnved_code = builder.tnved_code;
            this.uit_code = builder.uit_code;
            this.uitu_code = builder.uitu_code;
        }

        public static class Builder {
            private String certificate_document;
            private String certificate_document_date;
            private String certificate_document_number;
            private String owner_inn;
            private String producer_inn;
            private String production_date;
            private String tnved_code;
            private String uit_code;
            private String uitu_code;

            public Builder() {
            }

            public Builder setCertificate_document(String certificate_document) {
                this.certificate_document = certificate_document;
                return this;
            }

            public Builder setCertificate_document_date(String certificate_document_date) {
                this.certificate_document_date = certificate_document_date;
                return this;
            }

            public Builder setCertificate_document_number(String certificate_document_number) {
                this.certificate_document_number = certificate_document_number;
                return this;
            }

            public Builder setOwner_inn(String owner_inn) {
                this.owner_inn = owner_inn;
                return this;
            }

            public Builder setProducer_inn(String producer_inn) {
                this.producer_inn = producer_inn;
                return this;
            }

            public Builder setProduction_date(String production_date) {
                this.production_date = production_date;
                return this;
            }

            public Builder setTnved_code(String tnved_code) {
                this.tnved_code = tnved_code;
                return this;
            }

            public Builder setUit_code(String uit_code) {
                this.uit_code = uit_code;
                return this;
            }

            public Builder setUitu_code(String uitu_code) {
                this.uitu_code = uitu_code;
                return this;
            }

            public Product build() {
                return new Product(this);
            }
        }
    }

    public static void main(String[] args) {
        String signature = "signature";

        Product product1 = new Product.Builder()
                .setCertificate_document("certificate_document1")
                .setCertificate_document_date("certificate_document_date1")
                .setCertificate_document_number("certificate_document_number1")
                .setOwner_inn("owner_inn1")
                .setProducer_inn("producer_inn1")
                .setProduction_date("production_date1")
                .setTnved_code("tnved_code1")
                .setUit_code("uit_code1")
                .setUitu_code("uitu_code1")
                .build();

        Product product2 = new Product.Builder()
                .setCertificate_document("certificate_document2")
                .setCertificate_document_date("certificate_document_date2")
                .setCertificate_document_number("certificate_document_number2")
                .setOwner_inn("owner_inn2")
                .setProducer_inn("producer_inn2")
                .setProduction_date("production_date2")
                .setTnved_code("tnved_code2")
                .setUit_code("uit_code2")
                .setUitu_code("uitu_code2")
                .build();

        Document document = new Document.Builder()
                .setDescription("participantInn")
                .setDoc_id("doc_id")
                .setDoc_status("doc_status")
                .setDoc_type("doc_type")
                .setImportRequest(true)
                .setOwner_inn("owner_inn")
                .setParticipant_inn("participant_inn")
                .setProducer_inn("producer_inn")
                .setProduction_date("production date")
                .setProduction_type("production_type")
                .setReg_date("reg_date")
                .setReg_number("reg_number")
                .addProduct(product1)
                .addProduct(product2)
                .build();

        CrptApi crptApi = new CrptApi(TimeUnit.MINUTES, 5);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите положительное число для отправки документа или 0 для выхода:");
        while (scanner.nextInt() > 0){
            System.out.println("Получен ответ с кодом: " + crptApi.addProductToTrade(document, signature));
        }
        crptApi.stopLimiter();
    }
}