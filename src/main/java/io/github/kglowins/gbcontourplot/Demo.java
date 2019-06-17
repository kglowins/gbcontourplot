package io.github.kglowins.gbcontourplot;

import io.github.kglowins.gbcontourplot.demo.In100GbcdDemo;
import io.github.kglowins.gbcontourplot.demo.TiGbcdDemo;
import io.github.kglowins.gbcontourplot.demo.TiGbpdDemo;
import io.github.kglowins.gbcontourplot.demo.ZrO2Demo;
import lombok.extern.slf4j.Slf4j;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;

import static java.util.Arrays.asList;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

@Slf4j
public class Demo {

    private static final String ZRO2 = "ZrO2";
    private static final String TI_GBCD = "Ti GBCD";
    private static final String TI_GBPD = "Ti GBPD";
    private static final String IN100_GBCD = "IN100 GBCD";

    public static void main(String[] args) {

        log.debug("Avalable fonts = {}", asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

        JPanel cards = new JPanel(new CardLayout());
        cards.add(ZrO2Demo.createPlots(), ZRO2);
        cards.add(TiGbcdDemo.createPlots(), TI_GBCD);
        cards.add(TiGbpdDemo.createPlots(), TI_GBPD);
        cards.add(In100GbcdDemo.createPlots(), IN100_GBCD);

        JPanel comboBoxPanel = new JPanel();
        String[] comboBoxItems = new String[]{ZRO2, TI_GBCD, TI_GBPD, IN100_GBCD};
        JComboBox<String> comboBox = new JComboBox<>(comboBoxItems);
        comboBox.setEditable(false);
        comboBox.addItemListener(itemEvent -> {
            CardLayout cl = (CardLayout) cards.getLayout();
            cl.show(cards, (String) itemEvent.getItem());
        });
        comboBoxPanel.add(comboBox);

        Panel panel = new Panel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(comboBoxPanel);
        panel.add(cards);

        JFrame f = new JFrame();
        f.add(panel);
        f.pack();
        f.setVisible(true);
        f.setResizable(false);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
