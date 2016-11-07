package com.blackteam.dsketches.gui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.dsketches.R;

/**
 * Для отображения, что достижение получено.
 */
public class AchievementToast extends Toast {

    private static String achivementName_ = "";

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public AchievementToast(Context context) {
        super(context);

        LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View achievementView = layoutInflater.inflate(R.layout.ach_unlocked, null);
        TextView achievementName = (TextView) achievementView.findViewById(R.id.ach_name);
        achievementName.setText(achivementName_);
        ImageView achievementImage = (ImageView) achievementView.findViewById(R.id.ach_image);
        achievementImage.setImageResource(R.drawable.star);
        setView(achievementView);
    }

    /*
    * Метод вызова сообщения без установки длительности существования
    * с передачей сообщению текстовой информации в качестве последовательности
    * текстовых символов или строки.
    */
    public static AchievementToast makeText(Context context, String achievementName) {
        achivementName_ = achievementName;
        AchievementToast result = new AchievementToast(context);
        result.setDuration(LENGTH_SHORT);
        result.setGravity(Gravity.TOP, 0, 0);

        return result;
    }
}
