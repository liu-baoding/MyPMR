package com.example.todolist.ui;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import java.util.ArrayList;

public class ToastUtil {
    private static ArrayList<Toast> toastList = new ArrayList<Toast>();

    public static void newToast(Context context, String content) {
        cancelAll();
        try {
            Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
            toastList.add(toast);
            toast.show();
        } catch (Exception e) {
            // to make toast in child thread
            Looper.prepare();
            Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
            toastList.add(toast);
            toast.show();
            Looper.loop();
        }

    }

    public static void cancelAll() {
        if (!toastList.isEmpty()) {
            for (Toast t : toastList) {
                t.cancel();
            }
            toastList.clear();
        }
    }
}
