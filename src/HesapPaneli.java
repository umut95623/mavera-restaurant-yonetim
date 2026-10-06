import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

public class HesapPaneli extends JPanel {

    private static final String BOS_FIS_YAZISI = "Ürünleri ekleyip \"Hesapla\"ya basın,\nfiş burada görünecek.";
    private static final String ESKI_FIS_YAZISI = "Hesap değişti.\nGüncel fiş için tekrar \"Hesapla\"ya basın.";
    private static final Color UYARI_RENGI = new Color(0xB45309);

    private final Sepet sepet;
    private final List<UrunSatiri> satirlar;

    private final DefaultListModel<SiparisKalemi> listeModeli = new DefaultListModel<>();
    private final JList<SiparisKalemi> hesapListesi = new JList<>(listeModeli);

    private JComboBox<String> masaSecimi;
    private JRadioButton salondaRadio, paketRadio;
    private JRadioButton bahsis0, bahsis5, bahsis10, bahsis15, bahsisDiger;
    private JTextField digerBahsisAlani;
    private JTextArea fisAlani;
    private Color normalYaziRengi;
    private boolean fisGosteriliyor = false;

    public HesapPaneli(Sepet sepet, List<UrunSatiri> satirlar) {
        super(new BorderLayout());
        this.sepet = sepet;
        this.satirlar = satirlar;
        setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel ayarlar = new JPanel();
        ayarlar.setLayout(new BoxLayout(ayarlar, BoxLayout.Y_AXIS));
        ayarlar.add(siparisPaneli());
        ayarlar.add(bahsisPaneli());
        ayarlar.add(butonPaneli());

        fisAlani = new JTextArea();
        fisAlani.setEditable(false);
        fisAlani.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        normalYaziRengi = fisAlani.getForeground();
        fisiSifirla();
        JScrollPane fisKaydirma = new JScrollPane(fisAlani);
        fisKaydirma.setBorder(BorderFactory.createTitledBorder("Fiş"));

        JPanel orta = new JPanel(new GridLayout(1, 2, 8, 0));
        orta.add(hesapListesiPaneli());
        orta.add(fisKaydirma);

        add(ayarlar, BorderLayout.NORTH);
        add(orta, BorderLayout.CENTER);

        sepet.degisinceHaberVer(() -> {
            listeyiYenile();
            fisiGecersizKil();
        });
    }

    private JPanel hesapListesiPaneli() {
        hesapListesi.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        hesapListesi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton cikarButonu = new JButton("Seçileni Çıkar");
        cikarButonu.addActionListener(e -> seciliyiCikar());
        JPanel butonSatiri = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        butonSatiri.add(cikarButonu);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Hesaptaki Ürünler"));
        panel.add(new JScrollPane(hesapListesi), BorderLayout.CENTER);
        panel.add(butonSatiri, BorderLayout.SOUTH);
        return panel;
    }

    private void listeyiYenile() {
        listeModeli.clear();
        for (SiparisKalemi kalem : sepet.getKalemler()) {
            listeModeli.addElement(kalem);
        }
    }

    private void seciliyiCikar() {
        int sira = hesapListesi.getSelectedIndex();
        if (sira < 0) {
            JOptionPane.showMessageDialog(this,
                    "Önce listeden çıkarmak istediğiniz satırı seçin.",
                    "Satır seçilmedi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        sepet.cikar(sira);
    }

    private JPanel siparisPaneli() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Sipariş Bilgileri"));

        String[] masalar = new String[10];
        for (int i = 0; i < masalar.length; i++) {
            masalar[i] = "Masa " + (i + 1);
        }
        masaSecimi = new JComboBox<>(masalar);

        salondaRadio = new JRadioButton("Salonda", true);
        paketRadio = new JRadioButton("Paket servis (+" + (int) HesapMakinesi.PAKET_UCRETI + " TL)");
        ButtonGroup siparisTuru = new ButtonGroup();
        siparisTuru.add(salondaRadio);
        siparisTuru.add(paketRadio);

        salondaRadio.addActionListener(e -> masaSecimi.setEnabled(true));
        paketRadio.addActionListener(e -> masaSecimi.setEnabled(false));

        degisinceFisiGecersizKil(masaSecimi);
        degisinceFisiGecersizKil(salondaRadio);
        degisinceFisiGecersizKil(paketRadio);

        panel.add(new JLabel("Masa:"));
        panel.add(masaSecimi);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(salondaRadio);
        panel.add(paketRadio);
        return panel;
    }

    private JPanel bahsisPaneli() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Bahşiş"));

        bahsis0 = new JRadioButton("Yok", true);
        bahsis5 = new JRadioButton("%5");
        bahsis10 = new JRadioButton("%10");
        bahsis15 = new JRadioButton("%15");
        bahsisDiger = new JRadioButton("Diğer: %");
        digerBahsisAlani = new JTextField(4);
        digerBahsisAlani.setEnabled(false);

        ButtonGroup bahsisGrubu = new ButtonGroup();
        for (JRadioButton r : new JRadioButton[]{bahsis0, bahsis5, bahsis10, bahsis15, bahsisDiger}) {
            bahsisGrubu.add(r);
            panel.add(r);
            r.addActionListener(e -> digerBahsisAlani.setEnabled(bahsisDiger.isSelected()));
            degisinceFisiGecersizKil(r);
        }
        panel.add(digerBahsisAlani);

        digerBahsisAlani.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fisiGecersizKil();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fisiGecersizKil();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fisiGecersizKil();
            }
        });
        return panel;
    }

    private void degisinceFisiGecersizKil(ItemSelectable bilesen) {
        bilesen.addItemListener(e -> fisiGecersizKil());
    }

    private JPanel butonPaneli() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton hesaplaButonu = new JButton("Hesapla");
        JButton temizleButonu = new JButton("Temizle");
        hesaplaButonu.addActionListener(e -> hesapla());
        temizleButonu.addActionListener(e -> temizle());
        panel.add(hesaplaButonu);
        panel.add(temizleButonu);
        return panel;
    }

    private void fisiGoster(String fis) {
        fisAlani.setForeground(normalYaziRengi);
        fisAlani.setText(fis);
        fisAlani.setCaretPosition(0);
        fisGosteriliyor = true;
    }

    private void fisiGecersizKil() {
        if (!fisGosteriliyor) {
            return;
        }
        fisAlani.setForeground(UYARI_RENGI);
        fisAlani.setText(ESKI_FIS_YAZISI);
        fisGosteriliyor = false;
    }

    private void fisiSifirla() {
        fisAlani.setForeground(normalYaziRengi);
        fisAlani.setText(BOS_FIS_YAZISI);
        fisGosteriliyor = false;
    }

    private double bahsisYuzdesi() {
        if (bahsis5.isSelected()) return 5;
        if (bahsis10.isSelected()) return 10;
        if (bahsis15.isSelected()) return 15;
        if (bahsisDiger.isSelected()) {
            try {
                double yuzde = Double.parseDouble(digerBahsisAlani.getText().trim().replace(',', '.'));
                if (!(yuzde >= 0 && yuzde <= 100)) throw new NumberFormatException();
                return yuzde;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Bahşiş için 0 ile 100 arasında bir sayı yazın.",
                        "Geçersiz bahşiş", JOptionPane.WARNING_MESSAGE);
                return -1;
            }
        }
        return 0;
    }

    private boolean eklenmemisSecimleriOnayla() {
        StringBuilder liste = new StringBuilder();
        for (UrunSatiri satir : satirlar) {
            if (satir.getAdet() > 0) {
                liste.append("\n   - ").append(satir.getAdet()).append(" x ").append(satir.getUrun().getAd());
            }
        }
        if (liste.length() == 0) {
            return true;
        }

        int cevap = JOptionPane.showConfirmDialog(this,
                "Şu ürünlerin adedi seçili ama \"Ekle\"ye basılmamış, hesaba dahil edilmeyecek:"
                        + liste + "\n\nYine de hesaplansın mı?",
                "Eklenmemiş ürünler var", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return cevap == JOptionPane.YES_OPTION;
    }

    private void hesapla() {
        if (sepet.bosMu()) {
            JOptionPane.showMessageDialog(this,
                    "Hesap boş. Menüden ürün seçip \"Ekle\"ye basın.",
                    "Hesap boş", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!eklenmemisSecimleriOnayla()) return;

        double yuzde = bahsisYuzdesi();
        if (yuzde < 0) return;

        boolean paketServis = paketRadio.isSelected();
        HesapMakinesi hesap = new HesapMakinesi(sepet.getKalemler(), paketServis, yuzde);
        String siparisBilgisi = paketServis ? "Paket servis" : (String) masaSecimi.getSelectedItem();

        fisiGoster(hesap.fisOlustur(siparisBilgisi));
    }

    private void temizle() {
        int cevap = JOptionPane.showConfirmDialog(this,
                "Hesap ve tüm seçimler silinsin mi?", "Temizle", JOptionPane.YES_NO_OPTION);
        if (cevap != JOptionPane.YES_OPTION) return;

        for (UrunSatiri satir : satirlar) {
            satir.sifirla();
        }
        sepet.temizle();
        masaSecimi.setSelectedIndex(0);
        masaSecimi.setEnabled(true);
        salondaRadio.setSelected(true);
        bahsis0.setSelected(true);
        digerBahsisAlani.setText("");
        digerBahsisAlani.setEnabled(false);
        fisiSifirla();
    }
}
