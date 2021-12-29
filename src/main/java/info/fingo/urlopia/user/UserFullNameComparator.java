package info.fingo.urlopia.user;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class UserFullNameComparator implements Comparator<User> {

    @Override
    public int compare(User u1,
                       User u2) {
        var collator = Collator.getInstance(new Locale("pl","PL"));
        collator.setStrength(Collator.TERTIARY);
        var compareLastName = collator.compare(u1.getLastName(), u2.getLastName());
        if (compareLastName != 0){
            return compareLastName;
        }
        return collator.compare(u1.getFirstName(), u2.getFirstName());
    }
}
