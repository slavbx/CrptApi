package slavbx;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Document {
    private final UUID docId;
    private final DocumentStatus docStatus;
    private final DocumentType docType;
    private final String ownerInn;
    private final String participantInn;
    private final String producerInn;
    private final LocalDate productionDate;
    private final ProductionType productionType;
    private final LocalDate regDate;

    private final Description description;
    private final boolean importRequest;
    private final List<Product> products;
    private final String regNumber;

    private Document(Builder builder) {
        this.docId = builder.docId;
        this.docStatus = builder.docStatus;
        this.docType = builder.docType;
        this.ownerInn = builder.ownerInn;
        this.participantInn = builder.participantInn;
        this.producerInn = builder.producerInn;
        this.productionDate = builder.productionDate;
        this.productionType = builder.productionType;
        this.regDate = builder.regDate;

        this.description = builder.description;
        this.importRequest = builder.importRequest;
        this.products = builder.products;
        this.regNumber = builder.regNumber;
    }

    public static class Builder {
        private final UUID docId;
        private final DocumentStatus docStatus;
        private final DocumentType docType;
        private final String ownerInn;
        private final String participantInn;
        private final String producerInn;
        private final LocalDate productionDate;
        private final ProductionType productionType;
        private final LocalDate regDate;

        private Description description;
        private boolean importRequest;
        private List<Product> products;
        private String regNumber;

        public Builder(UUID docId, DocumentStatus docStatus, DocumentType docType, String ownerInn, String participantInn,
                       String producerInn, LocalDate productionDate, ProductionType productionType, LocalDate regDate) {
            this.docId = docId;
            this.docStatus = docStatus;
            this.docType = docType;
            this.ownerInn = ownerInn;
            this.participantInn = participantInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.productionType = productionType;
            this.regDate = regDate;
        }

        public Builder description(String participantInn) {
            this.description = new Description(participantInn);
            return this;
        }

        public Builder importRequest(boolean importRequest) {
            this.importRequest = importRequest;
            return this;
        }

        public Builder products(List<Product> products) {
            this.products = products;
            return this;
        }

        public Builder regNumber(String regNumber) {
            this.regNumber = regNumber;
            return this;
        }

        public Document build() {
            if (this.docId == null || this.docType == null || this.ownerInn == null || this.participantInn == null || this.producerInn == null ||
                    this.productionDate == null || this.productionType == null || this.regDate == null) {
                throw new IllegalStateException("Обязательные поля не заполнены!");
            }
            if (!this.description.getParticipantInn().matches("\\d{10}|\\d{12}")) {
                throw new IllegalArgumentException("ИНН описания должен быть 10 или 12 цифр");
            }
            if (!this.ownerInn.matches("\\d{10}|\\d{12}")) {
                throw new IllegalArgumentException("ИНН собственника товара должен быть 10 или 12 цифр");
            }
            if (!this.participantInn.matches("\\d{10}|\\d{12}")) {
                throw new IllegalArgumentException("ИНН участника оборота товара должен быть 10 или 12 цифр");
            }
            if (!this.producerInn.matches("\\d{10}|\\d{12}")) {
                throw new IllegalArgumentException("ИНН производителя товара должен быть 10 или 12 цифр");
            }
            try {
                UUID.fromString(this.description.getParticipantInn());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Идентификатор документа должен быть корректным UUID");
            }
            return new Document(this);
        }
    }
}
