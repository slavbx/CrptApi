package slavbx;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        String signature = "signature";

        CrptApi.Product product1 = new CrptApi.Product.Builder()
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

        CrptApi.Product product2 = new CrptApi.Product.Builder()
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

        Document.Document document = new Document.Document.Builder()
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
