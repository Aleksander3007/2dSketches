package com.blackteam.dsketches;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Диалоговое окно покупки.
 */
public class PaymentDialogFragment extends DialogFragment {

    private View paymentDialogView_;
    private Player player_;

    @SuppressLint("ValidFragment")
    public PaymentDialogFragment(Player player) {
        super();
        this.player_ = player;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        paymentDialogView_ = inflater.inflate(R.layout.payment_dialog, container, false);
        Button buyWithPointsButton = (Button) paymentDialogView_.findViewById(R.id.btn_buyWithPoints);
        // TODO: очков в strings.xml - локализация.
        buyWithPointsButton.setText(String.valueOf(Skill.COST_POINTS) + " очков");

        buyWithPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем достаточно ли средств.
                if (player_.getScore() >= Skill.COST_POINTS) {
                    // TODO: Заглушка Skill.Type.RESHUFFLE.
                    ((MainActivity)getActivity()).buySkill(Skill.Type.RESHUFFLE, PaymentType.POINTS);
                    dismiss();
                }
                else {
                    ((TextView)getView().findViewById(R.id.tv_payment_message))
                            .setText("Не достаточно средств.");
                }
            }
        });

        return paymentDialogView_;
    }
}
