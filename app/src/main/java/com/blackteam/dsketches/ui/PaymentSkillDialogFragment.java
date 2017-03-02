package com.blackteam.dsketches.ui;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blackteam.dsketches.PaymentType;
import com.blackteam.dsketches.Player;
import com.blackteam.dsketches.R;
import com.blackteam.dsketches.Skill;
import com.blackteam.dsketches.ui.MainActivity;

/**
 * Диалоговое окно покупки skill.
 */
public class PaymentSkillDialogFragment extends DialogFragment {

    private View mPaymentDialogView;
    private Player mPlayer;
    private Skill.Type mSkillType;

    @SuppressLint("ValidFragment")
    public PaymentSkillDialogFragment(Skill.Type skillType, Player player) {
        super();
        this.mSkillType = skillType;
        this.mPlayer = player;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPaymentDialogView = inflater.inflate(R.layout.payment_dialog, container, false);
        Button buyWithPointsButton = (Button) mPaymentDialogView.findViewById(R.id.btn_buyWithPoints);
        // TODO: очков в strings.xml - локализация.
        buyWithPointsButton.setText(String.valueOf(Skill.COST_POINTS) + " очков");

        buyWithPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем достаточно ли средств.
                if (mPlayer.getScore() >= Skill.COST_POINTS) {
                    // TODO: Заглушка Skill.Type.RESHUFFLE.
                    ((MainActivity)getActivity()).buySkill(mSkillType, PaymentType.POINTS);
                    dismiss();
                }
                else {
                    // TODO: Не достаточно средств в strings.xml - локализация.
                    ((TextView)getView().findViewById(R.id.tv_payment_message))
                            .setText("Не достаточно средств.");
                }
            }
        });

        return mPaymentDialogView;
    }
}
