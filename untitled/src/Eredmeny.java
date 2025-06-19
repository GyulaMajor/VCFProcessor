public class Eredmeny {
    String transcript, hgvsc, variantType;
    int count = 1;

    Eredmeny(String transcript, String hgvsc, String variant) {
        this.transcript = transcript;
        this.hgvsc = hgvsc;
        this.variantType = variant;
    }

    Eredmeny inc() {
        ++this.count;
        return this;
    }

    public String toString() {
        return String.format("%s;%s;%s;%d%n", this.transcript, this.hgvsc, this.variantType, this.count);
    }
}