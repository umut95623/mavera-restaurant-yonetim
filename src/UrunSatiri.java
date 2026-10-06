import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UrunSatiri extends JPanel {

    private static final int MAKS_ADET = 99;
    private static final int ISIM_GENISLIGI = 170;

    private final Urun urun;
    private final Consumer<SiparisKalemi> hesabaEkle;

    private final JTextField adetAlani;
    private JCheckBox zeroKutusu;
    private final List<JRadioButton> aromaButonlari = new ArrayList<>();

    public UrunSatiri(Urun urun, Consumer<SiparisKalemi> hesabaEkle) {
        super(new BorderLayout());
        this.urun = urun;
        this.hesabaEkle = hesabaEkle;
        setBorder(new EmptyBorder(3, 6, 3, 6));

        adetAlani = new JTextField("0", 3);
        adetAlani.setHorizontalAlignment(JTextField.CENTER);

        adetAlani.addActionListener(e -> adetDegistir(0));

        adetAlani.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                secenekleriGuncelle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                secenekleriGuncelle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                secenekleriGuncelle();
            }
        });

        JPanel solOrtali = new JPanel(new GridBagLayout());
        GridBagConstraints kural = new GridBagConstraints();
        kural.anchor = GridBagConstraints.WEST;
        kural.weightx = 1;
        solOrtali.add(solTaraf(), kural);

        add(solOrtali, BorderLayout.CENTER);
        add(sagTaraf(), BorderLayout.EAST);
        secenekleriGuncelle();
    }

    private JPanel solTaraf() {
        JPanel sol = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));

        JLabel isimEtiketi = new JLabel(urun.getAd());
        Dimension boyut = isimEtiketi.getPreferredSize();
        isimEtiketi.setPreferredSize(new Dimension(Math.max(boyut.width, ISIM_GENISLIGI), boyut.height));
        sol.add(isimEtiketi);

        if (urun.isZeroOlabilir()) {
            zeroKutusu = new JCheckBox("Zero");
            sol.add(zeroKutusu);
        }

        if (urun.aromaliMi()) {
            ButtonGroup aromaGrubu = new ButtonGroup();
            for (String aroma : urun.getAromalar()) {
                JRadioButton r = new JRadioButton(aroma);
                aromaGrubu.add(r);
                aromaButonlari.add(r);
                sol.add(r);
            }
            aromaButonlari.get(0).setSelected(true);
        }

        return sol;
    }

    private JPanel sagTaraf() {
        JLabel fiyatEtiketi = new JLabel(String.format("%.2f TL", urun.getFiyat()));
        fiyatEtiketi.setPreferredSize(new Dimension(80, 25));
        fiyatEtiketi.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton eksiButon = new JButton("-");
        JButton artiButon = new JButton("+");
        JButton ekleButonu = new JButton("Ekle");
        eksiButon.addActionListener(e -> adetDegistir(-1));
        artiButon.addActionListener(e -> adetDegistir(+1));
        ekleButonu.addActionListener(e -> ekle());

        JPanel sag = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        sag.add(fiyatEtiketi);
        sag.add(Box.createHorizontalStrut(10));
        sag.add(eksiButon);
        sag.add(adetAlani);
        sag.add(artiButon);
        sag.add(Box.createHorizontalStrut(6));
        sag.add(ekleButonu);
        return sag;
    }

    private void secenekleriGuncelle() {
        boolean adetVar = getAdet() > 0;
        if (zeroKutusu != null) {
            zeroKutusu.setEnabled(adetVar);
        }
        for (JRadioButton r : aromaButonlari) {
            r.setEnabled(adetVar);
        }
    }

    private void adetDegistir(int fark) {
        int yeniAdet = Math.max(0, Math.min(MAKS_ADET, getAdet() + fark));
        adetAlani.setText(String.valueOf(yeniAdet));
        if (yeniAdet == 0 && zeroKutusu != null) {
            zeroKutusu.setSelected(false);
        }
    }

    private void ekle() {
        int adet = getAdet();
        if (adet < 1) {
            JOptionPane.showMessageDialog(this,
                    "Önce - / + butonlarıyla ya da kutuya yazarak adet seçin.",
                    "Adet seçilmedi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (adet > MAKS_ADET) {
            JOptionPane.showMessageDialog(this,
                    "Tek seferde en fazla " + MAKS_ADET + " adet eklenebilir.",
                    "Adet çok fazla", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean zero = zeroKutusu != null && zeroKutusu.isSelected();

        String aroma = null;
        for (JRadioButton r : aromaButonlari) {
            if (r.isSelected()) aroma = r.getText();
        }

        hesabaEkle.accept(new SiparisKalemi(urun, adet, zero, aroma));
        sifirla();
    }

    public int getAdet() {
        try {
            return Math.max(0, Integer.parseInt(adetAlani.getText().trim()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Urun getUrun() {
        return urun;
    }

    public void sifirla() {
        adetAlani.setText("0");
        if (zeroKutusu != null) zeroKutusu.setSelected(false);
        if (!aromaButonlari.isEmpty()) aromaButonlari.get(0).setSelected(true);
    }
}
