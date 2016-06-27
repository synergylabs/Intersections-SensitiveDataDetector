package capstone.sdd.validator;

/**
 * Created by lieyongzou on 6/27/16.
 */
public class CreditCardValidator implements Validator {

    @Override
    public boolean validate(String data) {

        data = purify(data);
        int len = data.length();

        int odd_sum = 0, even_sum = 0;
        for (int i = 1; i <= len; i++) {
            int index = len - i;    // From right to left

            int num = data.charAt(index) - '0';
            if ((i & 1) == 1) {     // Odd index
                odd_sum += num;
            } else {    // Even index
                even_sum += sumAllDigit(num * 2);
            }

        }

        int sum = odd_sum + even_sum;
        return sum % 10 == 0;
    }

    /**
     * A method to remove all the unrelated char, like apace, dash
     * @param data the data
     * @return the pure data
     */
    private String purify(String data) {
        StringBuilder res = new StringBuilder();

        for (char c : data.toCharArray()) {
            if (Character.isDigit(c)) {
                res.append(c);
            }
        }

        return res.toString();
    }

    /**
     * A method to sum up all digits in a number
     * @param num the number
     * @return the sum
     */
    private int sumAllDigit(int num) {
        int res = 0;

        while (num != 0) {
            res += num % 10;
            num /= 10;
        }

        return res;
    }
}
