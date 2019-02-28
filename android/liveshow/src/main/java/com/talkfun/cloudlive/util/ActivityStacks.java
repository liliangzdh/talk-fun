package com.talkfun.cloudlive.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2017/9/29.
 */

public class ActivityStacks {
    private static ActivityStacks instance = new ActivityStacks();
    private static List<Activity> activityStack = new ArrayList<Activity>();
    public static ActivityStacks getInstance() {
        return instance;
    }

    public void addActivity(Activity aty) {
        activityStack.add(aty);
    }

    public void removeActivity(Activity aty) {
        activityStack.remove(aty);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (Activity activity : activityStack) {
            if (null != activity) {
                activity.finish();
            }
        }
        activityStack.clear();
     /*   for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();*/
    }
}
