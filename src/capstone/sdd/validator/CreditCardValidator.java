package capstone.sdd.validator;

import capstone.sdd.core.Utility;

/**
 * Created by lieyongzou on 6/27/16.
 */
public class CreditCardValidator implements Validator {

    @Override
    public boolean validate(String data) {

        data = Utility.removeNonNumeric(data);
        int len = data.length();

        int odd_sum = 0, even_sum = 0;
        for (int i = 1; i <= len; i++) {
            int index = len - i;    // From right to left

            int num = data.charAt(index) - '0';
            if ((i & 1) == 1) {     // Odd index
                odd_sum += num;
            } else {    // Even index
                even_sum += Utility.sumAllDigit(num * 2);
            }

        }

        int sum = odd_sum + even_sum;
        return sum % 10 == 0;
    }

}
