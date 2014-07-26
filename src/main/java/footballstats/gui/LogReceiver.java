package footballstats.gui;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Tomasz Lichon on 24.07.14.
 */
public class LogReceiver extends JScrollPane {

    private static LogReceiver instance;
    private static JTextArea textArea;
    private LogReceiver(){
        super(textArea = new JTextArea());
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.setAutoscrolls(true);
        textArea.setRows(10);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    public static LogReceiver getInstance(){
        return instance==null ? instance = new LogReceiver() : instance;
    }

    public void info(String msg){
        log("[info] " + msg + "\n");
    }

    public void warning(String msg) {
        log("[warning] " + msg + "\n");
    }

    public void error(String msg){
        log("[error] " + msg + "\n");
    }

    private synchronized void log(String msg) {
        textArea.append(msg);
        System.out.println(msg);
        textArea.setCaretPosition(textArea.getText().length() - 1);
    }


}
