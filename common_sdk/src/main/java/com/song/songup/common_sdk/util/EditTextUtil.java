package com.song.songup.common_sdk.util;

import android.widget.EditText;

/**
 * @Description：描述信息
 * @Author：Song UP
 * @Date：2019/8/10 18:43
 * 修改备注：
 */
public class EditTextUtil {
    public static void saveTwoPoint(EditText editText, CharSequence s) {
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                s = s.toString().subSequence(0,
                        s.toString().indexOf(".") + 3);
                editText.setText(s);
                editText.setSelection(s.length());
            }
        }
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            editText.setText(s);
            editText.setSelection(2);
        }

        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                editText.setText(s.subSequence(0, 1));
                editText.setSelection(1);
                return;
            }
        }
    }

    public static String saveIntLength(EditText editText, CharSequence s,int number) {
        String numberStr = s.toString();
        if (numberStr.length()>number){
            numberStr = numberStr.substring(0,number);
            editText.setText(numberStr);
            editText.setSelection(number);
        }
        return numberStr;
    }
}
