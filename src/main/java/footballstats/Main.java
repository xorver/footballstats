package footballstats;

import footballstats.core.FullRoster;
import footballstats.statparser.AnnabetStatParser;
import footballstats.statparser.HttpClientAdapter;

import java.util.Scanner;

/**
 * Created by Tomasz Lichon on 13.07.14.
 */
public class Main {

    public static void main(String[] args){
        System.setProperty("jsse.enableSNIExtension", "false");
        Scanner scanner = new Scanner(System.in);
        FullRoster roster = new FullRoster(new AnnabetStatParser(new HttpClientAdapter()));

        System.out.println("Znalezione terminy:");
        for(String date : roster.getDayList())
            System.out.print(date+" ");
        System.out.println();

        String date = scanner.nextLine();

        roster.dayMatches(date,5);
    }
}
