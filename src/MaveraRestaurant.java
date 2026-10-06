import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaveraRestaurant extends JFrame {

    private static final Color GECE_MAVISI = new Color(0x1B2A49);
    private static final Color ALTIN = new Color(0xE0B04B);
    private static final Color ONAY_YESILI = new Color(0x2E7D32);

    private final Sepet sepet = new Sepet();
    private final List<UrunSatiri> satirlar = new ArrayList<>();
    private final JLabel sepetEtiketi = new JLabel();
    private final JLabel bilgiEtiketi = new JLabel();

    public MaveraRestaurant() {
        setTitle("Mavera Restoran");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 680);
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane sekmeler = new JTabbedPane();
        for (Map.Entry<String, List<Kategori>> sekme : RestoranMenusu.getSekmeler().entrySet()) {
            sekmeler.addTab(sekme.getKey(), menuSekmesi(sekme.getValue()));
        }
        sekmeler.addTab("Hesap", new HesapPaneli(sepet, satirlar));

        sepet.degisinceHaberVer(this::sepetYazisiniGuncelle);

        add(baslikPaneli(), BorderLayout.NORTH);
        add(sekmeler, BorderLayout.CENTER);
        add(altCubuk(), BorderLayout.SOUTH);

        sepetYazisiniGuncelle();
    }

    private JPanel baslikPaneli() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(GECE_MAVISI);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel isim = new JLabel("Mavera");
        isim.setFont(new Font("SansSerif", Font.BOLD, 30));
        isim.setForeground(ALTIN);

        JLabel altBaslik = new JLabel("Restoran Yönetim Sistemi");
        altBaslik.setFont(new Font("SansSerif", Font.PLAIN, 13));
        altBaslik.setForeground(Color.WHITE);

        panel.add(isim);
        panel.add(altBaslik);
        return panel;
    }

    private JComponent menuSekmesi(List<Kategori> kategoriler) {
        JPanel icerik = new JPanel();
        icerik.setLayout(new BoxLayout(icerik, BoxLayout.Y_AXIS));
        for (Kategori kategori : kategoriler) {
            icerik.add(kategoriPaneli(kategori));
        }

        JPanel sarmalayici = new JPanel(new BorderLayout());
        sarmalayici.add(icerik, BorderLayout.NORTH);
        sarmalayici.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane kaydirma = new JScrollPane(sarmalayici);
        kaydirma.setBorder(null);
        kaydirma.getVerticalScrollBar().setUnitIncrement(16);
        return kaydirma;
    }

    private JPanel kategoriPaneli(Kategori kategori) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(kategori.getBaslik()));

        for (Urun urun : kategori.getUrunler()) {
            UrunSatiri satir = new UrunSatiri(urun, this::hesabaEkle);
            satirlar.add(satir);
            panel.add(satir);
        }
        return panel;
    }

    private void hesabaEkle(SiparisKalemi kalem) {
        sepet.ekle(kalem);
        bilgiEtiketi.setText(kalem.getAdet() + " x " + kalem.getFisAdi() + " hesaba eklendi");
    }

    private JPanel altCubuk() {
        bilgiEtiketi.setForeground(ONAY_YESILI);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 12, 8, 12));
        panel.add(sepetEtiketi, BorderLayout.WEST);
        panel.add(bilgiEtiketi, BorderLayout.EAST);
        return panel;
    }

    private void sepetYazisiniGuncelle() {
        sepetEtiketi.setText(String.format("Hesapta: %d ürün   |   Ara toplam: %.2f TL",
                sepet.getToplamAdet(), sepet.getAraToplam()));
        bilgiEtiketi.setText("");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new MaveraRestaurant().setVisible(true));
    }
}
