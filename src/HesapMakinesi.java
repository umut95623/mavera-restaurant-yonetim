import java.text.DecimalFormat;
import java.util.List;

public class HesapMakinesi {

    public static final double PAKET_UCRETI = 30.0;
    public static final double KDV_ORANI = 10.0;

    private final List<SiparisKalemi> kalemler;
    private final double bahsisYuzdesi;

    private final double araToplam;
    private final double paketUcreti;
    private final double kdv;
    private final double bahsis;
    private final double genelToplam;

    public HesapMakinesi(List<SiparisKalemi> kalemler, boolean paketServis, double bahsisYuzdesi) {
        this.kalemler = kalemler;
        if (!(bahsisYuzdesi >= 0 && bahsisYuzdesi <= 100)) {
            throw new IllegalArgumentException("Bahşiş yüzdesi 0 ile 100 arasında olmalı, gelen: " + bahsisYuzdesi);
        }
        this.bahsisYuzdesi = Math.round(bahsisYuzdesi * 100) / 100.0;

        double toplam = 0;
        for (SiparisKalemi kalem : kalemler) {
            toplam += kalem.getTutar();
        }

        araToplam = toplam;
        paketUcreti = paketServis ? PAKET_UCRETI : 0;
        kdv = (araToplam + paketUcreti) * KDV_ORANI / 100;
        bahsis = araToplam * this.bahsisYuzdesi / 100;
        genelToplam = araToplam + paketUcreti + kdv + bahsis;
    }

    public String fisOlustur(String siparisBilgisi) {
        String cizgi = "-".repeat(43) + "\n";
        String ciftCizgi = "=".repeat(43) + "\n";
        String satirFormati = "%-29s %10.2f TL%n";

        StringBuilder fis = new StringBuilder();
        fis.append(ciftCizgi);
        fis.append("              MAVERA RESTORAN\n");
        fis.append(ciftCizgi);
        fis.append(siparisBilgisi).append("\n");
        fis.append(cizgi);

        for (SiparisKalemi kalem : kalemler) {
            fis.append(kalem.fisSatiri()).append("\n");
        }

        fis.append(cizgi);
        fis.append(String.format(satirFormati, "Ara toplam", araToplam));
        if (paketUcreti > 0) {
            fis.append(String.format(satirFormati, "Paket ücreti", paketUcreti));
        }
        fis.append(String.format(satirFormati, "KDV (%" + yuzdeYazisi(KDV_ORANI) + ")", kdv));
        if (bahsis > 0) {
            fis.append(String.format(satirFormati, "Bahşiş (%" + yuzdeYazisi(bahsisYuzdesi) + ")", bahsis));
        }
        fis.append(cizgi);
        fis.append(String.format(satirFormati, "GENEL TOPLAM", genelToplam));
        fis.append(ciftCizgi);
        fis.append("        Bizi tercih ettiğiniz için\n");
        fis.append("               teşekkürler!\n");

        return fis.toString();
    }

    private static String yuzdeYazisi(double yuzde) {
        return new DecimalFormat("0.##").format(yuzde);
    }

    public double getAraToplam() {
        return araToplam;
    }

    public double getGenelToplam() {
        return genelToplam;
    }
}
