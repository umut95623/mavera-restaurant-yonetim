import java.util.Objects;

public class SiparisKalemi {

    private final Urun urun;
    private final int adet;
    private final boolean zero;
    private final String aroma;

    public SiparisKalemi(Urun urun, int adet, boolean zero, String aroma) {
        this.urun = urun;
        this.adet = adet;
        this.zero = zero;
        this.aroma = aroma;
    }

    public boolean ayniCesitMi(SiparisKalemi diger) {
        return urun == diger.urun
                && zero == diger.zero
                && Objects.equals(aroma, diger.aroma);
    }

    public SiparisKalemi adetEklenmis(int ekAdet) {
        return new SiparisKalemi(urun, adet + ekAdet, zero, aroma);
    }

    public String getFisAdi() {
        String isim = urun.getAd();
        if (zero) {
            isim += " Zero";
        }
        if (aroma != null) {
            isim += " " + aroma;
        }
        return isim;
    }

    public String fisSatiri() {
        return String.format("%-24s x%-3d %10.2f TL", getFisAdi(), adet, getTutar());
    }

    @Override
    public String toString() {
        return fisSatiri();
    }

    public int getAdet() {
        return adet;
    }

    public double getTutar() {
        return adet * urun.getFiyat();
    }
}
