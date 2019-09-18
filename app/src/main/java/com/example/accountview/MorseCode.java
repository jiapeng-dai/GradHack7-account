package com.example.accountview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

final public class MorseCode {
    final static Map<Character, String> map = new HashMap<>();
    final static Long LONG_DAH = new Long(375);
    final static Long SHORT_DIT = new Long(125);
    final static Long PAUSE = new Long(3375);
    final static Long BLANK = new Long(1125);
    final static Long NOSIG = new Long(0);
    final static String[][] code = {
            {"0", "----- "}, {"1", ".---- "},{"2", "..--- "}, {"3", "...-- "}, {"4", "....- "},
            {"5", "..... "}, {"6", "-.... "}, {"7", "--... "}, {"8", "---.. "}, {"9", "----. "},
            {".",".-.-.-"}
    };

    static void initMap() {
        for (String[] pair : code)
            map.put(pair[0].charAt(0), pair[1]);
    }
    static String getMorseC(Character input) {
        String s = "";
        s = map.get(input);
        return s;
    }
    static Long[] getSig(String s) {
        Long[] res = {NOSIG, NOSIG, NOSIG, NOSIG, NOSIG, NOSIG, NOSIG, NOSIG, NOSIG, NOSIG, NOSIG, NOSIG};
        for (int i = 0; i < 6; i++) {
            String c = s.substring(i,i+1);
            if (c.equals(".")) {
                res[2*i] = SHORT_DIT;
            } else if (c.equals("-")){
                res[2*i] = LONG_DAH;
            }
            if (i<6) {
                res[2 * i + 1] = BLANK;
            } else {
                res[2 * i + 1] = PAUSE;
            }
        }
        return res;
    }

    static ArrayList<Long> getPattern(String input) {
        ArrayList res = new ArrayList();
        res.add(BLANK);
        for (char c : input.toCharArray()) {
            String s = getMorseC(c);
            res.addAll(Arrays.asList(getSig(s)));
            //res.add(PAUSE);
        }
        return res;
    }

}