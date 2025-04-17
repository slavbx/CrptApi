package slavbx;

public class Product {
    private final String ownerInn;
    private final String producerInn;
    private final String productionDate;
    private final String tnvedCode;

    private final String uitCode;
    private final String uituCode;
    private final String certificateDocument;
    private final String certificateDocumentDate;
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

    public static class Builder {
        private final String ownerInn;
        private final String producerInn;
        private final String productionDate;
        private final String tnvedCode;

        private String uitCode;
        private String uituCode;
        private String certificateDocument;
        private String certificateDocumentDate;
        private String certificateDocumentNumber;

        public Builder(String ownerInn, String producerInn, String productionDate, String tnvedCode) {
            this.ownerInn = ownerInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.tnvedCode = tnvedCode;
        }

        public Builder certificateDocument(String certificateDocument) {
            this.certificateDocument = certificateDocument;
            return this;
        }

        public Builder certificateDocumentDate(String certificateDocumentDate) {
            this.certificateDocumentDate = certificateDocumentDate;
            return this;
        }

        public Builder certificateDocumentNumber(String certificate_document_number) {
            this.certificateDocumentNumber = certificate_document_number;
            return this;
        }

        public Builder uitCode(String uitCode) {
            this.uitCode = uitCode;
            return this;
        }

        public Builder uituCode(String uituCode) {
            this.uituCode = uituCode;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
