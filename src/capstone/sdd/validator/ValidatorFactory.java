package capstone.sdd.validator;

/**
 * Created by lieyongzou on 6/27/16.
 */
public class ValidatorFactory {


    public static Validator getValidator(String type) {
        switch (type) {
            case "CREDIT CARD":
                return new CreditCardValidator();

            default:
                return null;
        }
    }
}
