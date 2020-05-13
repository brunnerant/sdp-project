package ch.epfl.qedit.view.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ch.epfl.qedit.R;

/**
 * A Custom CardView that nicely display a title, a image and a integer value See an example of use
 * in fragment_home_info.mxl
 */
public class StatisticCardView extends androidx.cardview.widget.CardView {

    private int data;
    private int iconId;
    private int titleId;

    public StatisticCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a =
                context.getTheme()
                        .obtainStyledAttributes(attrs, R.styleable.StatisticCardView, 0, 0);

        // Get the custom attributes of this card
        try {
            data = a.getInt(R.styleable.StatisticCardView_data, 0);
            iconId = a.getResourceId(R.styleable.StatisticCardView_icon, 0);
            titleId = a.getResourceId(R.styleable.StatisticCardView_title, 0);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        this.setElevation(10);
        inflate(getContext(), R.layout.statistic_card, this);
        TextView titleView = findViewById(R.id.card_title);
        ImageView icon = findViewById(R.id.card_icon);
        titleView.setText(titleId);
        icon.setImageResource(iconId);
        updateDataView();
    }

    @SuppressLint("SetTextI18n")
    private void updateDataView() {
        TextView dataView = findViewById(R.id.card_data);
        dataView.setText(Integer.toString(data));
    }

    /** We want to be able to update this value programmatically from the HomeInfoFragment */
    public void setData(int data) {
        this.data = data;
        updateDataView();
    }
}
