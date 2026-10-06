import java.util.Arrays;
import java.util.List;

public class Kategori {

    private final String baslik;
    private final List<Urun> urunler;

    public Kategori(String baslik, Urun... urunler) {
        this.baslik = baslik;
        this.urunler = Arrays.asList(urunler);
    }

    public String getBaslik() {
        return baslik;
    }

    public List<Urun> getUrunler() {
        return urunler;
    }
}
