package com.blackteam.dsketches.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blackteam.dsketches.PaymentType;
import com.blackteam.dsketches.R;
import com.blackteam.dsketches.Skill;

/**
 * Диалоговое окно покупки skill.
 */
public class PaymentSkillDialogFragment extends DialogFragment {

    public static final String ARG_SKILL_TYPE = "ARG_SKILL_TYPE";
    public static final String ARG_PLAYER_SCORE = "ARG_PLAYER_SCORE";

    /**
     * Activity, которое создаст данное диалоговое окно должна реализовать
     * этот интерфейс (чтобы получать и обрабатывать callback-и диалогового окна).
     */
    public interface NoticeDialogListener {
        void buySkill(Skill.Type skillType, PaymentType paymentType);
    }
    // Для отправки callback-ов Activity.
    NoticeDialogListener mListener;

    /**
     * Создание экземпляра класса {@link PaymentSkillDialogFragment}.
     * @param skillType тип skill-а.
     * @param playerScore количество очков у игрока.
     */
    public static PaymentSkillDialogFragment newInstance(Skill.Type skillType, int playerScore) {
        PaymentSkillDialogFragment paymentSkillDialog = new PaymentSkillDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_SKILL_TYPE, skillType);
        args.putInt(ARG_PLAYER_SCORE, playerScore);
        paymentSkillDialog.setArguments(args);

        return paymentSkillDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mPaymentDialogView = inflater.inflate(R.layout.payment_dialog, container, false);
        Button buyWithPointsButton = (Button) mPaymentDialogView.findViewById(R.id.btn_buyWithPoints);

        final Skill.Type skillType = (Skill.Type) getArguments().getSerializable(ARG_SKILL_TYPE);
        final int playerScore = getArguments().getInt(ARG_PLAYER_SCORE);

        // TODO: очков в strings.xml - локализация.
        buyWithPointsButton.setText(String.valueOf(Skill.COST_POINTS)
                + getResources().getString(R.string.points));

        buyWithPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем достаточно ли средств.
                if (playerScore >= Skill.COST_POINTS) {
                    mListener.buySkill(skillType, PaymentType.POINTS);
                    dismiss();
                }
                else {
                    // TODO: Не достаточно средств в strings.xml - локализация.
                    ((TextView)getView().findViewById(R.id.tv_payment_message))
                            .setText(getResources().getString(R.string.money_is_not_enough));
                }
            }
        });

        return mPaymentDialogView;
    }

    // Проблема заключалась в том, что onAttach(Context context) в версиях API < 23,
    // не вызывается. А вызывается onAttach(Activity activity), который является Deprecated,
    // но используется внутри super.onAttach(context);
    // Issue android framework track: https://code.google.com/p/android/issues/detail?id=183358

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // не убраем вызов onAttachToContext() здесь для наглядности и понимания, а также если
        // в случае когда данный метод вызвал НЕ activity.
        // хотя по идеи super.onAttach(Activity activity) вызывает внутри super.onAttach(context).
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Для версий > 23(marshmallow) вызывается onAttach(Context context),
        // внутри которого вызывается onAttach(Activity activity), поэтому
        // здесь проверяем версию, чтобы не было двойного вызова onAttachToContext().
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    protected void onAttachToContext(Context context) {
        try {
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement PaymentSkillDialogFragment.NoticeDialogListener");
        }
    }
}
