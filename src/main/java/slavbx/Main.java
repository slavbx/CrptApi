package slavbx;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        byte[] randomBytes = new byte[512];
        new SecureRandom().nextBytes(randomBytes);
        String signature = Base64.getEncoder().encodeToString(randomBytes);

        Product product1 = new Product.Builder(
                "583713710002",
                "1134567890",
                LocalDate.parse("2022-12-18"),
                "6401000001"
        )
                .uitCode("12345678912345678912345678912345678912")
                .uituCode("123456789012345678")
                .certificateDocument(CertificateType.CONFORMITY_CERTIFICATE)
                .certificateDocumentDate(LocalDate.parse("2019-04-12"))
                .certificateDocumentNumber("12345")
                .build();

        Product product2 = new Product.Builder(
                "583713710002",
                "1134567890",
                LocalDate.parse("2022-12-18"),
                "6401000002"
        )
                .uitCode("22345678912345678912345678912345678912")
                .uituCode("223456789012345678")
                .certificateDocument(CertificateType.CONFORMITY_CERTIFICATE)
                .certificateDocumentDate(LocalDate.parse("2021-03-14"))
                .certificateDocumentNumber("12347")
                .build();

        Document document = new Document.Builder(
                UUID.fromString("8c6000fc-a18e-4711-88b7-11b3e16e6e2c"),
                DocumentStatus.IN_PROGRESS,
                DocumentType.LP_INTRODUCE_GOODS,
                "583713710002",
                "5534785607",
                "1134567890",
                LocalDate.parse("2022-12-18"),
                ProductionType.OWN_PRODUCTION
        )
                .description(new Description("5534785607"))
                .importRequest(true)
                .products(List.of(product1, product2))
                .build();

        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 5);


        //System.out.println("Получен ответ с кодом: " + crptApi.addProductToTrade(document, signature));

        ExecutorService executorService = Executors.newFixedThreadPool(20);

        for (int i = 0; i < 50; i++) {
            executorService.submit(() -> {
                System.out.println(Thread.currentThread() + ": Получен ответ с кодом: " + crptApi.addProductToTrade(document, signature));
                System.out.println(Thread.currentThread() + ": Получен ответ с кодом: " + crptApi.addProductToTrade(null, signature));
            });
        }
        //executorService.shutdown();

//        for (int i = 0; i < 2000000; i++) {
//            System.out.println(": Получен ответ с кодом: " + crptApi.addProductToTrade(document, signature));
//        }
        //crptApi.stopLimiter();
    }
}
