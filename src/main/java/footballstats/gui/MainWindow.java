package footballstats.gui;

import footballstats.core.FullRoster;
import footballstats.statparser.AnnabetStatParser;
import footballstats.statparser.HttpClientAdapter;
import footballstats.statparser.SportType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;


/**
 * Created by Tomasz Lichon on 24.07.14.
 */
public class MainWindow extends JFrame {

    FullRoster footballRoster;
    FullRoster hockeyRoster;
    JPanel mainPanel = new JPanel(new BorderLayout());
    JPanel topPanel = new JPanel(new BorderLayout());
    JTextArea mainTextArea = new JTextArea();
    JComboBox<String> dateComboBox = new JComboBox<>();
    JComboBox<Integer> countComboBox = new JComboBox<>();
    JComboBox<String> disciplineComboBox = new JComboBox<>();
    JButton button = new JButton("Get Stats");

    public MainWindow() throws HeadlessException {
        super();

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //set
        mainTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        for(int i=1;i<10;i++)
            countComboBox.addItem(i);
        for(SportType type : SportType.values())
            disciplineComboBox.addItem(type.name());
        dateComboBox.setPreferredSize(new Dimension(200, 50));
        countComboBox.setPreferredSize(new Dimension(200, 50));
        button.setEnabled(false);
        button.setPreferredSize(new Dimension(200, 50));
        topPanel.add(disciplineComboBox, BorderLayout.WEST);
        JPanel countAndDate = new JPanel(new BorderLayout());
        topPanel.add(countAndDate, BorderLayout.CENTER);
        countAndDate.add(countComboBox,BorderLayout.WEST);
        countAndDate.add(dateComboBox,BorderLayout.EAST);
        topPanel.add(button, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.PAGE_START);
        mainPanel.add(new JScrollPane(mainTextArea), BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(LogReceiver.getInstance()), BorderLayout.PAGE_END);
        this.setContentPane(mainPanel);
        this.setPreferredSize(new Dimension(1000, 600));
        this.setTitle("FootballStats 1.0");

        //Add listeners
        button.addActionListener(e -> {
            if(dateComboBox.getItemCount()!=0) {
                String date = (String) dateComboBox.getSelectedItem();
                int count = (int) countComboBox.getSelectedItem();
                if(disciplineComboBox.getSelectedItem().equals(SportType.FOOTBALL.name()))
                    new Thread(() -> mainTextArea.setText(footballRoster.dayMatches(date,count))).start();
                else if (disciplineComboBox.getSelectedItem().equals(SportType.HOCKEY.name()))
                    new Thread(() -> mainTextArea.setText(hockeyRoster.dayMatches(date,count))).start();
            }
        });
        disciplineComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                if(item.equals(SportType.FOOTBALL.name())) {
                    dateComboBox.removeAllItems();
                    footballRoster.getDayList().forEach(dateComboBox::addItem);
                } else if(item.equals(SportType.HOCKEY.name())) {
                    dateComboBox.removeAllItems();
                    hockeyRoster.getDayList().forEach(dateComboBox::addItem);
                } else
                    dateComboBox.removeAllItems();
            }
        });

        //Display the window.
        this.pack();
        this.setVisible(true);
    }

    public void init() {
        HttpClientAdapter adapter = new HttpClientAdapter();
        footballRoster = new FullRoster(new AnnabetStatParser(adapter, SportType.FOOTBALL));
        hockeyRoster = new FullRoster(new AnnabetStatParser(adapter,SportType.HOCKEY));
        LogReceiver.getInstance().info("All proxy found!");
        disciplineComboBox.setSelectedItem(SportType.FOOTBALL.name());
        List<String> dateList = footballRoster.getDayList();
        dateComboBox.removeAllItems();
        for(String s : dateList)
            dateComboBox.addItem(s);
        LogReceiver.getInstance().info("All dates found!");
        LogReceiver.getInstance().info("Choose date and compute stats...");
        button.setEnabled(true);
    }
}
