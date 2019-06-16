package io.github.kglowins.gbcontourplot;

import io.github.kglowins.gbcontourplot.demo.TiGbcdDemoProvider;
import io.github.kglowins.gbcontourplot.demo.TiGbpdDemoProvider;
import io.github.kglowins.gbcontourplot.demo.ZrO2DemoProvider;
import lombok.extern.slf4j.Slf4j;


import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

@Slf4j
public class Demo {

    private static final String ZRO2 = "ZrO2";
    private static final String TI_GBCD = "Ti GBCD";
    private static final String TI_GBPD = "Ti GBPD";


    public static void main(String[] args) throws IOException {

        /*log.info("Avalable fonts = {}", asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

        contourPlot.toVectorFile("pdf", PageSize.A4, "test.pdf");
        contourPlot.toVectorFile("eps", PageSize.A4, "test.eps");
        contourPlot.toRasterFile("jpg","test.jpg");

*/


        JPanel cards = new JPanel(new CardLayout());
        cards.add(ZrO2DemoProvider.createSubplotsGrid(), ZRO2);
        cards.add(TiGbcdDemoProvider.createSubplotsGrid(), TI_GBCD);
        cards.add(TiGbpdDemoProvider.createSubplotsGrid(), TI_GBPD);


        JPanel comboBoxPanel = new JPanel();
        String[] comboBoxItems = new String[]{ ZRO2, TI_GBCD, TI_GBPD };
        JComboBox<String> comboBox = new JComboBox(comboBoxItems);
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


     /*   JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        mainPanel.add(comboBoxPane, 0 , 0);
        mainPanel.add(cards, 1, 0);

        setLayout(new MigLayout("insets 0", "[grow]", "[][][][][]"));
*/




        JFrame f = new JFrame();
        f.add(panel);


     //   gridPanel.repaint();

        f.pack();
        f.setVisible(true);
        f.setResizable(false);
    }
}
