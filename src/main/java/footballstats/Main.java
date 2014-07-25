package footballstats;

import footballstats.core.FullRoster;
import footballstats.gui.MainWindow;
import footballstats.statparser.AnnabetStatParser;
import footballstats.statparser.HttpClientAdapter;

import java.util.Scanner;

/**
 * Created by Tomasz Lichon on 13.07.14.
 */
public class Main {

    public static void main(String[] args){
        System.setProperty("jsse.enableSNIExtension", "false");

        MainWindow mainWindow;
        mainWindow = new MainWindow();
        mainWindow.init();

    }
}
