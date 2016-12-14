package com.blackteam.dsketches;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blackteam.dsketches.utils.ExceptionHandler;
import com.blackteam.dsketches.utils.Vector2;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnTouchListener {
    public static String VERSION;

    public static String ACHIEVEMENT_DATA = "achievement_data";
    public static String SKETCHES_DATA = "sketches_data";

    private static final int MAIN_MENU_ACTIVITY_ = 0;

    private static final String MR_HEADLINES_FONT_NAME_ = "fonts/mr_headlines.ttf";
    private static Typeface mrHeadlinesFont_;

    private GLSurfaceView gameView_;
    private GameRenderer gameRenderer_;
    private Player player_;
    private Game game_;
    private AchievementsManager achievementsManager_;
    private SketchesManager sketchesManager_;
    private ContentManager contents_;

    private TextView scoreTextView_;
    private TextView skillShuffleTextView_;
    private TextView skillFriendsTextView_;
    private TextView skillChasmTextView_;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        VERSION = getApplicationContext().getResources().getString(R.string.version_str);

        this.contents_ = new ContentManager(getApplicationContext());

        try {
            player_ = new Player(contents_);
            player_.load(getApplicationContext());
            achievementsManager_ = new AchievementsManager(player_, getApplicationContext());
            sketchesManager_ = new SketchesManager(getApplicationContext());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e("XmlPullParserException", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.getMessage());
        }

        game_ = new Game(player_, sketchesManager_, contents_);
        game_.loadLevel();
        game_.addObserver(achievementsManager_);

        setContentView(R.layout.main);

        gameView_ = (GLSurfaceView) findViewById(R.id.gameview);
        gameView_.setOnTouchListener(this);
        gameView_.setEGLContextClientVersion(2);

        // Делает возможным отрисовки элементов, находящихся позади(глубже) gameView_:
        gameView_.setZOrderOnTop(true);
        gameView_.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        gameView_.getHolder().setFormat(PixelFormat.RGBA_8888);
        //

        gameRenderer_ = new GameRenderer(getApplicationContext(), game_, contents_);
        gameView_.setRenderer(gameRenderer_);

        scoreTextView_ = (TextView)findViewById(R.id.tv_score);
        scoreTextView_.setText(String.valueOf(player_.getScore()));

        skillShuffleTextView_ = ((TextView)findViewById(R.id.tv_skill_shuffle));
        skillShuffleTextView_.setText(String.valueOf(
                player_.getSkill(Skill.Type.RESHUFFLE).getAmount())
        );

        skillFriendsTextView_ = ((TextView)findViewById(R.id.tv_skill_friends));
        skillFriendsTextView_.setText(String.valueOf(
                player_.getSkill(Skill.Type.FRIENDS).getAmount())
        );

        skillChasmTextView_ = ((TextView)findViewById(R.id.tv_skill_chasm));
        skillChasmTextView_.setText(String.valueOf(
                player_.getSkill(Skill.Type.CHASM).getAmount())
        );

        setCustomFonts();
    }

    private void setCustomFonts() {
        mrHeadlinesFont_ = Typeface.createFromAsset(getAssets(), MR_HEADLINES_FONT_NAME_);
        scoreTextView_.setTypeface(mrHeadlinesFont_);
        skillShuffleTextView_.setTypeface(mrHeadlinesFont_);
        skillFriendsTextView_.setTypeface(mrHeadlinesFont_);
        skillChasmTextView_.setTypeface(mrHeadlinesFont_);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView_.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView_.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player_.save(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("MainActivity", "onActivityResult start");

        if (requestCode == MAIN_MENU_ACTIVITY_) {
            if (resultCode == RESULT_OK) {
                boolean restart = data.getBooleanExtra(MainMenuActivity.CMD_RESTART_LVL, false);
                if (restart) {
                    Log.i("MainActivity", "restart");
                    game_.restartLevel();
                    player_.setScore(0);
                    scoreTextView_.setText(String.valueOf(player_.getScore()));
                }
                else {
                    Log.i("MainActivity", "false");
                }
            }
            else
            {
                Log.i("MainActivity", "resultCode != RESULT_OK");
            }
        }

        Log.i("MainActivity", "onActivityResult end");
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                // TODO: Отдельный метод для каждого Events.
                case (MotionEvent.ACTION_UP):
                    if (BuildConfig.DEBUG) {
                        Log.i("MainActivity", "Action was UP");
                    }
                    // TODO: По идеи везде hit и необходимо передавать Action.
                    game_.touchUp(getWorldCoords(event.getX(),event.getY()));
                    scoreTextView_.setText(String.valueOf(player_.getScore()));
                    return true;
                case (MotionEvent.ACTION_MOVE):
                    game_.hit(getWorldCoords(event.getX(), event.getY()));
                    return true;
                default:
                    return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Vector2 getWorldCoords(float screenX, float screenY) {
        return new Vector2(
                screenX * GameRenderer.uppX,
                (GameRenderer.height - screenY) * GameRenderer.uppY
        );
    }

    public void menuOpenOnClick(View view) {
        Bundle achievementsBundle = new Bundle();
        achievementsBundle.putSerializable("objects", achievementsManager_.getAchiviements());
        Bundle sketchesBundle = new Bundle();
        sketchesBundle.putSerializable("objects", sketchesManager_.getSketches());

        Intent menuIntent = new Intent(getBaseContext(), MainMenuActivity.class);
        menuIntent.putExtra(ACHIEVEMENT_DATA, achievementsBundle);
        menuIntent.putExtra(SKETCHES_DATA, sketchesBundle);
        startActivityForResult(menuIntent, MAIN_MENU_ACTIVITY_);
    }

    /**
     * Обработчик нажатия на одну из skill.
     */
    public void skillOnClick(View view) {
        Skill.Type skillType = null;
        TextView clickedSkillTextView = null;
        switch (view.getId()) {
            case R.id.ll_skill_shuffle:
                skillType = Skill.Type.RESHUFFLE;
                clickedSkillTextView = (TextView) findViewById(R.id.tv_skill_shuffle);
                break;
            case R.id.ll_skill_friends:
                skillType = Skill.Type.FRIENDS;
                clickedSkillTextView = (TextView) findViewById(R.id.tv_skill_friends);
                break;
            case R.id.ll_skill_chasm:
                skillType = Skill.Type.CHASM;
                clickedSkillTextView = (TextView) findViewById(R.id.tv_skill_chasm);
                break;
            default:
                return;
        }

        if (player_.getSkill(skillType).canUse()) {
            game_.applySkill(skillType);
            clickedSkillTextView.setText(
                    String.valueOf(player_.getSkill(skillType).getAmount())
            );
        }
        // В противном случае открываем окно покупки.
        else {
            FragmentManager fragmentManager = getFragmentManager();
            PaymentSkillDialogFragment paymentDialogFragment = new PaymentSkillDialogFragment(skillType, player_);
            paymentDialogFragment.show(fragmentManager, "paymentDialog");
        }
    }

    /**
     * Покупка skill.
     * @param skillType Тип skill.
     * @param paymentType Тип оплаты.
     */
    public void buySkill(Skill.Type skillType, PaymentType paymentType) {
        Log.i("MainActivity", String.format("buySkill (%s, %s)",
                skillType.toString(), paymentType.toString()));

        // Производим оплату.
        switch (paymentType) {
            case POINTS:
                player_.removeScore(Skill.COST_POINTS);
                scoreTextView_.setText(String.valueOf(player_.getScore()));
                break;
            case REAL_MONEY:
                // TODO: Сделать оплату по реальным деньгам.
                break;
        }

        // Окно покупки открывается только когда закончится skill,
        // поэтому после покупки оно станет 1.
        Skill boughtSkill = player_.getSkill(skillType);
        boughtSkill.add();
        switch (skillType) {
            case RESHUFFLE:
                skillShuffleTextView_.setText(String.valueOf(boughtSkill.getAmount()));
                break;
            case FRIENDS:
                skillFriendsTextView_.setText(String.valueOf(boughtSkill.getAmount()));
                break;
            case CHASM:
                skillChasmTextView_.setText(String.valueOf(boughtSkill.getAmount()));
                break;
        }
    }
}
