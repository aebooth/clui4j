import kotlin.Pair;

import org.boof.ListItemInput;
import org.boof.NumberInput;
import org.boof.Output;
import org.boof.UI;

public class IOTest {
    public static void main(String[] args){
        ListItemInput myInput = new ListItemInput(new Pair<>("a","cool"),new Pair<>("b","lame"));
        NumberInput myNum = new NumberInput("What is your favorite number? ");
        myNum.setNumberType("Integer");
        Output o1= new Output(()->myInput.get());
        o1.newLine();
        o1.append("TOTALLY");
        o1.newLine();

        Output o2 = new Output(()->myNum.get() + " is my favorite number too!");
        UI ios = new UI(myInput,o1,myNum,o2);
        ios.run();
    }
}
