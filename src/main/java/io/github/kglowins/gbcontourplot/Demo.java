package io.github.kglowins.gbcontourplot;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;

import io.github.kglowins.gbcontourplot.demo.ZrO2GBCDDemo2021;
import io.github.kglowins.gbcontourplot.demo.ZrO2GBPDDemo2021;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Demo {
    private static final Logger log = LoggerFactory.getLogger(Demo.class);

    private static final String ZRO2 = "ZrO2";
    private static final String TI_GBCD = "Ti GBCD";
    private static final String TI_GBPD = "Ti GBPD";
    private static final String IN100_GBCD = "IN100 GBCD";
    private static final String CHARACTERISTIC = "Characteristic GBs";
    private static final String SYMMETRIES = "GBCD Symmetries";

    public static void main(String[] args) {

        log.debug("Available fonts = {}", asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

        JPanel cards = new JPanel(new CardLayout());

      //  cards.add(ZrO2GBCDDemo2021.createPlotsPanel(0, 4, "samp08-09_res_5_7", ZrO2GBCDDemo2021.H2_1400, ZrO2GBCDDemo2021.H2_1400_ERR, null), "ZrO2GBCDDemo2021.H2_1400a");
     //   cards.add(ZrO2GBCDDemo2021.createPlotsPanel(4, 6, "samp08-09_res_5_7", ZrO2GBCDDemo2021.H2_1400, ZrO2GBCDDemo2021.H2_1400_ERR, null), "ZrO2GBCDDemo2021.H2_1400b");
     //   cards.add(ZrO2GBCDDemo2021.createPlotsPanel(6, 10, "samp08-09_res_5_7", ZrO2GBCDDemo2021.H2_1400, ZrO2GBCDDemo2021.H2_1400_ERR, null), "ZrO2GBCDDemo2021.H2_1400c");

      //  cards.add(ZrO2GBCDDemo2021.createPlotsPanel(0, 4, "samp10-13_res57", ZrO2GBCDDemo2021.H2_1450, ZrO2GBCDDemo2021.H2_1450_ERR, null), "ZrO2GBCDDemo2021.H2_1450a");
    //    cards.add(ZrO2GBCDDemo2021.createPlotsPanel(4, 6, "samp10-13_res57", ZrO2GBCDDemo2021.H2_1450, ZrO2GBCDDemo2021.H2_1450_ERR, null), "ZrO2GBCDDemo2021.H2_1450b");

      //  cards.add(ZrO2GBCDDemo2021.createPlotsPanel(0, 4, "samp14-16_res57", ZrO2GBCDDemo2021.H20_1450, ZrO2GBCDDemo2021.H20_1450_ERR, null), "ZrO2GBCDDemo2021.H20_1450a");
     //   cards.add(ZrO2GBCDDemo2021.createPlotsPanel(4, 5, "samp14-16_res57", ZrO2GBCDDemo2021.H20_1450, ZrO2GBCDDemo2021.H20_1450_ERR, null), "ZrO2GBCDDemo2021.H20_1450b");


   //     cards.add(ZrO2GBCDDemo2021.createPlotsPanel(0, 2, "samp08b_5_7", ZrO2GBCDDemo2021.H2_1400_8B, ZrO2GBCDDemo2021.H2_1400_8B_ERR, null), "ZrO2GBCDDemo2021.H2_1400_8Ba");
     //   cards.add(ZrO2GBCDDemo2021.createPlotsPanel(4, 6, "samp08b_5_7", ZrO2GBCDDemo2021.H2_1400_8B, ZrO2GBCDDemo2021.H2_1400_8B_ERR, null), "ZrO2GBCDDemo2021.H2_1400_8Bb");

      //  cards.add(ZrO2GBCDDemo2021.createPlotsPanel(0, 2, "samp08c_5_7", ZrO2GBCDDemo2021.H2_1400_8C, ZrO2GBCDDemo2021.H2_1400_8C_ERR, null), "ZrO2GBCDDemo2021.H2_1400_8Ca");
      //  cards.add(ZrO2GBCDDemo2021.createPlotsPanel(4, 6, "samp08c_5_7", ZrO2GBCDDemo2021.H2_1400_8C, ZrO2GBCDDemo2021.H2_1400_8C_ERR, null), "ZrO2GBCDDemo2021.H2_1400_8Cb");

      //  cards.add(ZrO2GBCDDemo2021.createPlotsPanel(0, 2, "samp09b_5_7", ZrO2GBCDDemo2021.H2_1400_9B, ZrO2GBCDDemo2021.H2_1400_9B_ERR, null), "ZrO2GBCDDemo2021.H2_1400_9Ba");
      //  cards.add(ZrO2GBCDDemo2021.createPlotsPanel(4, 6, "samp09b_5_7", ZrO2GBCDDemo2021.H2_1400_9B, ZrO2GBCDDemo2021.H2_1400_9B_ERR, null), "ZrO2GBCDDemo2021.H2_1400_9Bb");


      /*  cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H2_1400, null, false), "ZrO2Demo2021.H2_1400");
        cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H2_1450, null, false), "ZrO2Demo2021.H2_1450");
        cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H2_1475, null, false), "ZrO2Demo2021.H2_1475");
        cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H2_1500, null, false), "ZrO2Demo2021.H2_1500");
        cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H2_1525, null, false), "ZrO2Demo2021.H2_1525");

        cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H2_1550, null, false), "ZrO2Demo2021.H2_1550");
*/
    //    cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H20_1450, null, false), "ZrO2Demo2021.H20_1450");
   //     cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H20_1475, null, false), "ZrO2Demo2021.H20_1475");
   //     cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H20_1500, null, false), "ZrO2Demo2021.H20_1500");
    //    cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H20_1525, null, false), "ZrO2Demo2021.H20_1525");
     //   cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H20_1550, asList(0.8, 1.,1.2, 1.4, 1.6, 1.8, 2.), true), "ZrO2Demo2021.H20_1550");

        cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H20_1575, null, false), "ZrO2Demo2021.H20_1575");
        cards.add(ZrO2GBPDDemo2021.createPlotsPanel(ZrO2GBPDDemo2021.H20_1600, null, false), "ZrO2Demo2021.H20_1600");

        /*cards.add(ZrO2Demo.createPlots(), ZRO2);
        cards.add(TiGbcdDemo.createPlots(), TI_GBCD);
        cards.add(TiGbpdDemo.createPlots(), TI_GBPD);
        cards.add(In100GbcdDemo.createPlots(), IN100_GBCD);
        cards.add(CharacteristicGBsDemo.createPlot(), CHARACTERISTIC);
        cards.add(GbcdSymmetriesDemo.createPlot(), SYMMETRIES);*/

        JPanel comboBoxPanel = new JPanel();
        String[] comboBoxItems = new String[]{
         //       "ZrO2GBCDDemo2021.H2_1400_8Ba",
             //   "ZrO2GBCDDemo2021.H2_1400_8Bb",
          //      "ZrO2GBCDDemo2021.H2_1400_8Ca",
              //  "ZrO2GBCDDemo2021.H2_1400_8Cb",
         //       "ZrO2GBCDDemo2021.H2_1400_9Ba",
              //  "ZrO2GBCDDemo2021.H2_1400_9Bb",

           //     "ZrO2GBCDDemo2021.H2_1400a",
          //      "ZrO2GBCDDemo2021.H2_1400b",
         //       "ZrO2GBCDDemo2021.H2_1400c",

           /*     "ZrO2GBCDDemo2021.H2_1450a"
                , "ZrO2GBCDDemo2021.H2_1450b"
                , "ZrO2GBCDDemo2021.H20_1450a"
                , "ZrO2GBCDDemo2021.H20_1450b",

                "ZrO2Demo2021.H2_1400",
                "ZrO2Demo2021.H2_1450",
                "ZrO2Demo2021.H2_1475",

                "ZrO2Demo2021.H2_1500",
                "ZrO2Demo2021.H2_1525",

                "ZrO2Demo2021.H2_1550",

                "ZrO2Demo2021.H20_1450",
                "ZrO2Demo2021.H20_1475",
                "ZrO2Demo2021.H20_1500",
                "ZrO2Demo2021.H20_1525",
                "ZrO2Demo2021.H20_1550"*/

                "ZrO2Demo2021.H20_1575",
                "ZrO2Demo2021.H20_1600"
        };
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
