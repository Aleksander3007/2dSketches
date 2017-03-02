package com.blackteam.dsketches.ui;

import android.app.Activity;
import android.app.DialogFragment;
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
