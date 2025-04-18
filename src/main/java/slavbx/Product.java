package slavbx;

import java.time.LocalDate;

public class Product {
    private final String ownerInn;
    private final String producerInn;
    private final LocalDate productionDate;
    private final String tnvedCode;

    private final String uitCode;
    private final String uituCode;
    private final CertificateType certificateDocument;
    private final LocalDate certificateDocumentDate;
    private final String certificateDocumentNumber;

    private Product(Builder builder) {
        this.ownerInn = builder.ownerInn;
        this.producerInn = builder.producerInn;
        this.productionDate = builder.productionDate;
        this.tnvedCode = builder.tnvedCode;

        this.uitCode = builder.uitCode;
        this.uituCode = builder.uituCode;
        this.certificateDocument = builder.certificateDocument;
        this.certificateDocumentDate = builder.certificateDocumentDate;
        this.certificateDocumentNumber = builder.certificateDocumentNumber;
    }

    public String getOwnerInn() {
        return ownerInn;
    }

    public String getProducerInn() {
        return producerInn;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public String getTnvedCode() {
        return tnvedCode;
    }

    public String getUitCode() {
        return uitCode;
    }

    public String getUituCode() {
        return uituCode;
    }

    public CertificateType getCertificateDocument() {
        return certificateDocument;
    }

    public LocalDate getCertificateDocumentDate() {
        return certificateDocumentDate;
    }

    public String getCertificateDocumentNumber() {
        return certificateDocumentNumber;
    }

    public static class Builder {
        private final String ownerInn;
        private final String producerInn;
        private final LocalDate productionDate;
        private final String tnvedCode;

        private String uitCode;
        private String uituCode;
        private CertificateType certificateDocument;
        private LocalDate certificateDocumentDate;
        private String certificateDocumentNumber;

        public Builder(String ownerInn, String producerInn, LocalDate productionDate, String tnvedCode) {
            this.ownerInn = ownerInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.tnvedCode = tnvedCode;
        }

        public Builder uitCode(String uitCode) {
            this.uitCode = uitCode;
            return this;
        }

        public Builder uituCode(String uituCode) {
            this.uituCode = uituCode;
            return this;
        }

        public Builder certificateDocument(CertificateType certificateDocument) {
            this.certificateDocument = certificateDocument;
            return this;
        }

        public Builder certificateDocumentDate(LocalDate certificateDocumentDate) {
            this.certificateDocumentDate = certificateDocumentDate;
            return this;
        }

        public Builder certificateDocumentNumber(String certificate_document_number) {
            this.certificateDocumentNumber = certificate_document_number;
            return this;
        }

        public Product build() {
            if (this.ownerInn == null || this.producerInn == null || this.productionDate == null || this.tnvedCode == null) {
                throw new IllegalStateException("Обязательные поля не заполнены!");
            }
            if (this.uitCode == null && this.uituCode == null) {
                throw new IllegalStateException("Обязательные поля не заполнены!");
            }
            if (!this.ownerInn.matches("\\d{10}|\\d{12}")) {
                throw new IllegalArgumentException("ИНН собственника товара должен быть 10 или 12 цифр");
            }
            if (!this.producerInn.matches("\\d{10}|\\d{12}")) {
                throw new IllegalArgumentException("ИНН производителя товара должен быть 10 или 12 цифр");
            }
            if (!this.tnvedCode.matches("\\d{1,10}")) {
                throw new IllegalArgumentException("Код ТН ВЭД товара должен содержать от 1 до 10 цифр");
            }
            if (this.uitCode != null && !this.uitCode.matches("[0-9]{38}|[0-9]{40}")) {
                throw new IllegalArgumentException("Код КИТ должен быть 38 или 40 цифр");
            }
            if (this.uituCode != null && !this.uituCode.matches("[0-9]{18}")) {
                throw new IllegalArgumentException("Код КИТУ должен быть 18 цифр");
            }
            if (!this.certificateDocumentNumber.matches("\\d+")) {
                throw new IllegalArgumentException("Код сертификата документа должен содержать только цифры");
            }

            return new Product(this);
        }
    }
}
