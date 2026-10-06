public class Urun {

    private final String ad;
    private final double fiyat;
    private boolean zeroOlabilir = false;
    private String[] aromalar = new String[0];

    public Urun(String ad, double fiyat) {
        this.ad = ad;
        this.fiyat = fiyat;
    }

    public Urun zeroSecenekli() {
        this.zeroOlabilir = true;
        return this;
    }

    public Urun aromali(String... aromalar) {
        this.aromalar = aromalar;
        return this;
    }

    public String getAd() {
        return ad;
    }

    public double getFiyat() {
        return fiyat;
    }

    public boolean isZeroOlabilir() {
        return zeroOlabilir;
    }

    public String[] getAromalar() {
        return aromalar;
    }

    public boolean aromaliMi() {
        return aromalar.length > 0;
    }
}
