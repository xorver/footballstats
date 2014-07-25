package footballstats.gui;

import footballstats.core.FullRoster;
import footballstats.statparser.AnnabetStatParser;
import footballstats.statparser.HttpClientAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by Tomasz Lichon on 24.07.14.
 */
public class MainWindow extends JFrame implements ActionListener {

    FullRoster roster;
    JPanel mainPanel = new JPanel(new BorderLayout());
    JPanel topPanel = new JPanel(new BorderLayout());
    JTextArea mainTextArea = new JTextArea();
    JComboBox<String> dateComboBox = new JComboBox<>();
    JComboBox<Integer> countComboBox = new JComboBox<>();
    JButton button = new JButton("Get Stats");

    public MainWindow() throws HeadlessException {
        super();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //set
        for(int i=1;i<10;i++)
            countComboBox.addItem(i);
        dateComboBox.setPreferredSize(new Dimension(200, 50));
        countComboBox.setPreferredSize(new Dimension(200, 50));
        button.setPreferredSize(new Dimension(200,50));
        button.addActionListener(this);
        topPanel.add(countComboBox,BorderLayout.WEST);
        topPanel.add(dateComboBox,BorderLayout.CENTER);
        topPanel.add(button,BorderLayout.EAST);
        mainPanel.add(topPanel,BorderLayout.PAGE_START);
        mainPanel.add(new JScrollPane(mainTextArea), BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(LogReceiver.getInstance()), BorderLayout.PAGE_END);
        this.setContentPane(mainPanel);
        this.setPreferredSize(new Dimension(800,600));
        this.setTitle("FootballStats 1.0");

        //Display the window.
        this.pack();
        this.setVisible(true);
    }

    public void init() {
        roster = new FullRoster(new AnnabetStatParser(new HttpClientAdapter()));
        LogReceiver.getInstance().info("All proxy found!");
        java.util.List<String> dates = roster.getDayList();
        LogReceiver.getInstance().info("All dates found!");
        dates.forEach(dateComboBox::addItem);
        LogReceiver.getInstance().info("Choose date and compute stats...");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(dateComboBox.getItemCount()!=0) {
            String date = (String) dateComboBox.getSelectedItem();
            int count = (int) countComboBox.getSelectedItem();
            new Thread(() -> mainTextArea.setText(roster.dayMatches(date,count))).start();
        }
    }
}
