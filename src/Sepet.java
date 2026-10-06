import java.util.ArrayList;
import java.util.List;

public class Sepet {

    private final List<SiparisKalemi> kalemler = new ArrayList<>();
    private final List<Runnable> dinleyiciler = new ArrayList<>();

    public void degisinceHaberVer(Runnable dinleyici) {
        dinleyiciler.add(dinleyici);
    }

    public void ekle(SiparisKalemi yeni) {
        for (int i = 0; i < kalemler.size(); i++) {
            SiparisKalemi mevcut = kalemler.get(i);
            if (mevcut.ayniCesitMi(yeni)) {
                kalemler.set(i, mevcut.adetEklenmis(yeni.getAdet()));
                haberVer();
                return;
            }
        }
        kalemler.add(yeni);
        haberVer();
    }

    public void cikar(int sira) {
        kalemler.remove(sira);
        haberVer();
    }

    public void temizle() {
        kalemler.clear();
        haberVer();
    }

    public List<SiparisKalemi> getKalemler() {
        return List.copyOf(kalemler);
    }

    public boolean bosMu() {
        return kalemler.isEmpty();
    }

    public int getToplamAdet() {
        int toplam = 0;
        for (SiparisKalemi kalem : kalemler) {
            toplam += kalem.getAdet();
        }
        return toplam;
    }

    public double getAraToplam() {
        double toplam = 0;
        for (SiparisKalemi kalem : kalemler) {
            toplam += kalem.getTutar();
        }
        return toplam;
    }

    private void haberVer() {
        for (Runnable dinleyici : dinleyiciler) {
            dinleyici.run();
        }
    }
}
