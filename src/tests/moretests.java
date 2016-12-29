package tests;

/**
 * Created by Keno on 12/28/2016.
 */
public class moretests {

    public static void main(String[] args) {

        String message = ":PM username hello, how are you man?";

        String data[] = message.split(" ", 3);

        for(String s : data ){
            System.out.println(s);
        }


    }
}
