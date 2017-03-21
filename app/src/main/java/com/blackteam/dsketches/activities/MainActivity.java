package com.blackteam.dsketches.activities;

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

import com.blackteam.dsketches.models.Achievement;
import com.blackteam.dsketches.managers.AchievementsManager;
import com.blackteam.dsketches.BuildConfig;
import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.Game;
import com.blackteam.dsketches.GameRenderer;
import com.blackteam.dsketches.PaymentType;
import com.blackteam.dsketches.models.Player;
import com.blackteam.dsketches.R;
import com.blackteam.dsketches.managers.SketchesManager;
import com.blackteam.dsketches.models.Skill;
import com.blackteam.dsketches.fragments.PaymentSkillDialogFragment;
import com.blackteam.dsketches.utils.ExceptionHandler;
import com.blackteam.dsketches.utils.Vector2;
import com.blackteam.dsketches.utils.xml.XmlLoaderInternal;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends Activity
        implements View.OnTouchListener, PaymentSkillDialogFragment.NoticeDialogListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int MAIN_MENU_ACTIVITY = 0;

    private static final String MAIN_FONT_NAME = "fonts/aquatico_regular.otf";
    private static Typeface sMainFont;

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

        this.mContents = new ContentManager(getApplicationContext());

        try {
            mPlayer = new Player(mContents);
            new XmlLoaderInternal().load(this, Player.FILE_NAME, mPlayer);
            mAchievementsManager = new AchievementsManager(mPlayer, getApplicationContext());
            mSketchesManager = new SketchesManager(getApplicationContext());
        } catch (XmlPullParserException | IOException e) {
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

        mGameRenderer = new GameRenderer(mGame, mContents);
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
        Log.e(TAG, e.getMessage());
        Intent intent = new Intent(getBaseContext(), ErrorActivity.class);
        intent.putExtra(ErrorActivity.EXTRA_ERROR_DATA, e.getMessage());
        startActivity(intent);
    }

    private void setCustomFonts() {
        sMainFont = Typeface.createFromAsset(getAssets(), MAIN_FONT_NAME);
        mScoreTextView.setTypeface(sMainFont);
        mSkillShuffleTextView.setTypeface(sMainFont);
        mSkillFriendsTextView.setTypeface(sMainFont);
        mSkillChasmTextView.setTypeface(sMainFont);
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
        try {
            new XmlLoaderInternal().save(this, Player.FILE_NAME, mPlayer);
        }
        catch (IOException ioex) {
            Log.e(TAG, ioex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult start");

        if (requestCode == MAIN_MENU_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                boolean restart = data.getBooleanExtra(MainMenuActivity.CMD_RESTART_LVL, false);
                if (restart) restartLevel();
            }
            else
            {
                Log.i(TAG, "resultCode != RESULT_OK");
            }
        }

        Log.i(TAG, "onActivityResult end");
    }

    private void restartLevel() {
        Log.i(TAG, "restart");

        mGame.restartLevel();

        mPlayer.setScore(0);

        // Если у игрока закончились skill и он делает restart,
        // то даём игроку 1 штуку)))
        // если же у него больше 1-го, то он не должен потерять их.
        if (mPlayer.getSkill(Skill.Type.RESHUFFLE).getAmount() <= 0)
            mPlayer.getSkill(Skill.Type.RESHUFFLE).setAmount(1);
        if (mPlayer.getSkill(Skill.Type.FRIENDS).getAmount() <= 0)
            mPlayer.getSkill(Skill.Type.FRIENDS).setAmount(1);
        if (mPlayer.getSkill(Skill.Type.CHASM).getAmount() <= 0)
            mPlayer.getSkill(Skill.Type.CHASM).setAmount(1);

        mScoreTextView.setText(String.valueOf(
                mPlayer.getScore())
        );
        mSkillShuffleTextView.setText(String.valueOf(
                mPlayer.getSkill(Skill.Type.RESHUFFLE).getAmount())
        );
        mSkillFriendsTextView.setText(String.valueOf(
                mPlayer.getSkill(Skill.Type.FRIENDS).getAmount())
        );
        mSkillChasmTextView.setText(String.valueOf(
                mPlayer.getSkill(Skill.Type.CHASM).getAmount())
        );
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                // TODO: Отдельный метод для каждого Events.
                case (MotionEvent.ACTION_UP):
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "Action was UP");
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
        achievementsBundle.putSerializable(AchievementsActivity.BUNDLE_ACHIEVEMENT_ARRAY,
                mAchievementsManager.getAchiviements());

        for (Achievement achievement : mAchievementsManager.getAchiviements()) {
            Log.i(TAG, "name = " + achievement.getName());
            Log.i(TAG, "descr = " + achievement.getDescription());
        }

        Bundle sketchesBundle = new Bundle();
        sketchesBundle.putSerializable(SketchesActivity.BUNDLE_SKETCHES_ARRAY,
                mSketchesManager.getSketches());

        Intent menuIntent = new Intent(getBaseContext(), MainMenuActivity.class);
        menuIntent.putExtra(MainMenuActivity.EXTRA_ACHIEVEMENT_DATA, achievementsBundle);
        menuIntent.putExtra(MainMenuActivity.EXTRA_SKETCHES_DATA, sketchesBundle);
        startActivityForResult(menuIntent, MAIN_MENU_ACTIVITY);
    }

    /**
     * Обработчик нажатия на одну из skill.
     */
    public void skillOnClick(View view) {
        Skill.Type skillType;
        TextView clickedSkillTextView;
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
            PaymentSkillDialogFragment paymentDialogFragment =
                    PaymentSkillDialogFragment.newInstance(skillType, mPlayer.getScore());
            paymentDialogFragment.show(fragmentManager, "paymentDialog");
        }
    }

    /**
     * Покупка skill.
     * @param skillType Тип skill.
     * @param paymentType Тип оплаты.
     */
    @Override
    public void buySkill(Skill.Type skillType, PaymentType paymentType) {
        Log.i(TAG, String.format("buySkill (%s, %s)",
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
