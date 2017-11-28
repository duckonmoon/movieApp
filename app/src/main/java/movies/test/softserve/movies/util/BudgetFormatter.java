package movies.test.softserve.movies.util;

/**
 * Created by rkrit on 27.11.17.
 */

public final class BudgetFormatter {
    private BudgetFormatter() {

    }
    public static String toMoney(Integer number){
        String stringNumber = number.toString();
        if (stringNumber.length() >= 10){
            stringNumber = stringNumber.substring(0,stringNumber.length()-10) + "b";
        } else if (stringNumber.length() >= 7){
            stringNumber = stringNumber.substring(0,stringNumber.length()-7) + "m";
        } else if (stringNumber.length() >= 4){
            stringNumber = stringNumber.substring(0,stringNumber.length()-4) + "k";
        }
        if (stringNumber.length()== 1){
            stringNumber = 1 + stringNumber;
        }
        return stringNumber;
    }
}
