package lib.grasp.util;

import java.util.Comparator;

/**
 * Created by GaQu_Dev on 2018/8/3.
 */
public class SortUtil {

    public static Comparator<String> COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            o1 = o1.toUpperCase();
            o2 = o2.toUpperCase();
            return o1.compareTo(o2);
        }
    };
}
