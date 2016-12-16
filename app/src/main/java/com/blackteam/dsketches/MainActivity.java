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
import android.widget.TextView;

import com.blackteam.dsketches.utils.ExceptionHandler;
import com.blackteam.dsketches.utils.Vector2;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnTouchListener {
    public static String VERSION;

    public static String ACHIEVEMENT_DATA = "achievement_data";
    public static String SKETCHES_DATA = "sketches_data";

    private static final int MAIN_MENU_ACTIVITY = 0;

    private static final String MR_HEADLINES_FONT_NAME = "fonts/mr_headlines.ttf";
    private static Typeface sMrHeadlinesFont;

    private GLSurfaceView mGameView;
    private GameRenderer mGameRenderer;
    private Player mPlayer;
    private Game mGame;
    private AchievementsManager mAchievementsManager;
    private SketchesManager mSketchesManager;
    private ContentManager mContents;

    private TextView mScoreTextView;
    private TextView mSkillShuffleTextView;
    private TextView mSkillFriendsTextView;
    private TextView mSkillChasmTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        VERSION = getApplicationContext().getResources().getString(R.string.version_str);

        this.mContents = new ContentManager(getApplicationContext());

        try {
            mPlayer = new Player(mContents);
            mPlayer.load(getApplicationContext());
            mAchievementsManager = new AchievementsManager(mPlayer, getApplicationContext());
            mSketchesManager = new SketchesManager(getApplicationContext());
        } catch (XmlPullParserException e) {
            printException(e);
        } catch (IOException e) {
            printException(e);
        }

        mGame = new Game(mPlayer, mSketchesManager, mContents);
        mGame.loadLevel();
        mGame.addObserver(mAchievementsManager);

        setContentView(R.layout.main);

        mGameView = (GLSurfaceView) findViewById(R.id.gameview);
        mGameView.setOnTouchListener(this);
        mGameView.setEGLContextClientVersion(2);

        // Делает возможным отрисовки элементов, находящихся позади(глубже) mGameView:
        mGameView.setZOrderOnTop(true);
        mGameView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGameView.getHolder().setFormat(PixelFormat.RGBA_8888);
        //

        mGameRenderer = new GameRenderer(getApplicationContext(), mGame, mContents);
        mGameView.setRenderer(mGameRenderer);

        mScoreTextView = (TextView)findViewById(R.id.tv_score);
        mScoreTextView.setText(String.valueOf(mPlayer.getScore()));

        mSkillShuffleTextView = ((TextView)findViewById(R.id.tv_skill_shuffle));
        mSkillShuffleTextView.setText(String.valueOf(
                mPlayer.getSkill(Skill.Type.RESHUFFLE).getAmount())
        );

        mSkillFriendsTextView = ((TextView)findViewById(R.id.tv_skill_friends));
        mSkillFriendsTextView.setText(String.valueOf(
                mPlayer.getSkill(Skill.Type.FRIENDS).getAmount())
        );

        mSkillChasmTextView = ((TextView)findViewById(R.id.tv_skill_chasm));
        mSkillChasmTextView.setText(String.valueOf(
                mPlayer.getSkill(Skill.Type.CHASM).getAmount())
        );

        setCustomFonts();
    }

    private void printException(Exception e) {
        e.printStackTrace();
        Log.e("XmlPullParserException", e.getMessage());
        Intent intent = new Intent(getBaseContext(), ErrorActivity.class);
        intent.putExtra(ErrorActivity.ERROR_DATA, e.getMessage());
        startActivity(intent);
    }

    private void setCustomFonts() {
        sMrHeadlinesFont = Typeface.createFromAsset(getAssets(), MR_HEADLINES_FONT_NAME);
        mScoreTextView.setTypeface(sMrHeadlinesFont);
        mSkillShuffleTextView.setTypeface(sMrHeadlinesFont);
        mSkillFriendsTextView.setTypeface(sMrHeadlinesFont);
        mSkillChasmTextView.setTypeface(sMrHeadlinesFont);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayer.save(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("MainActivity", "onActivityResult start");

        if (requestCode == MAIN_MENU_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                boolean restart = data.getBooleanExtra(MainMenuActivity.CMD_RESTART_LVL, false);
                if (restart) {
                    Log.i("MainActivity", "restart");
                    mGame.restartLevel();
                    mPlayer.setScore(0);
                    mScoreTextView.setText(String.valueOf(mPlayer.getScore()));
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
                    mGame.touchUp(getWorldCoords(event.getX(),event.getY()));
                    mScoreTextView.setText(String.valueOf(mPlayer.getScore()));
                    return true;
                case (MotionEvent.ACTION_MOVE):
                    mGame.hit(getWorldCoords(event.getX(), event.getY()));
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
                screenX * GameRenderer.sUppX,
                (GameRenderer.sHeight - screenY) * GameRenderer.sUppY
        );
    }

    public void menuOpenOnClick(View view) {
        Bundle achievementsBundle = new Bundle();
        achievementsBundle.putSerializable("objects", mAchievementsManager.getAchiviements());
        Bundle sketchesBundle = new Bundle();
        sketchesBundle.putSerializable("objects", mSketchesManager.getSketches());

        Intent menuIntent = new Intent(getBaseContext(), MainMenuActivity.class);
        menuIntent.putExtra(ACHIEVEMENT_DATA, achievementsBundle);
        menuIntent.putExtra(SKETCHES_DATA, sketchesBundle);
        startActivityForResult(menuIntent, MAIN_MENU_ACTIVITY);
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

        if (mPlayer.getSkill(skillType).canUse()) {
            mGame.applySkill(skillType);
            clickedSkillTextView.setText(
                    String.valueOf(mPlayer.getSkill(skillType).getAmount())
            );
        }
        // В противном случае открываем окно покупки.
        else {
            FragmentManager fragmentManager = getFragmentManager();
            PaymentSkillDialogFragment paymentDialogFragment = new PaymentSkillDialogFragment(skillType, mPlayer);
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
                mPlayer.removeScore(Skill.COST_POINTS);
                mScoreTextView.setText(String.valueOf(mPlayer.getScore()));
                break;
            case REAL_MONEY:
                // TODO: Сделать оплату по реальным деньгам.
                break;
        }

        // Окно покупки открывается только когда закончится skill,
        // поэтому после покупки оно станет 1.
        Skill boughtSkill = mPlayer.getSkill(skillType);
        boughtSkill.add();
        switch (skillType) {
            case RESHUFFLE:
                mSkillShuffleTextView.setText(String.valueOf(boughtSkill.getAmount()));
                break;
            case FRIENDS:
                mSkillFriendsTextView.setText(String.valueOf(boughtSkill.getAmount()));
                break;
            case CHASM:
                mSkillChasmTextView.setText(String.valueOf(boughtSkill.getAmount()));
                break;
        }
    }
}
