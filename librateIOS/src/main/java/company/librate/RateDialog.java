package company.librate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import company.librate.view.BaseRatingBar;
import company.librate.view.RotationRatingBar;

public class RateDialog extends Dialog {
    private Activity context;
    private RotationRatingBar rateBar;
    private SharedPreferences sharedPrefs;
    private MaterialRatingBar materialRatingBar;
    private static int upperBound = 4;
    private static final String KEY_IS_RATE = "key_is_rate";
    private boolean isRateAppTemp = false;
    private IResultClickDialog iResultClickDialog;
    private String appName = "";

    public RateDialog(Activity context, String appName, IResultClickDialog iResultClickDialog) {
        super(context);
        this.iResultClickDialog = iResultClickDialog;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rate_ios);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        this.context = context;
        this.appName = appName;
        sharedPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        initDialog();
    }

    private void initDialog() {
        TextView tvAppName = findViewById(R.id.txt_name_app);
        TextView btnOk = findViewById(R.id.btn_ok);
        TextView btnNotnow = findViewById(R.id.btn_not_now);
        rateBar = findViewById(R.id.simpleRatingBar);
        materialRatingBar = findViewById(R.id.materialRatingBar);

        tvAppName.setText(appName);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (materialRatingBar.getRating() > 0) {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean(KEY_IS_RATE, true);
                    editor.apply();

                    if (materialRatingBar.getRating() > upperBound) {
                        openMarket();
                    }

                    dismiss();
                    iResultClickDialog.rateNow();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.please_rate_5_stars), Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnNotnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                iResultClickDialog.later();
            }
        });
        rateBar.setOnRatingChangeListener((ratingBar, rating) -> isRateAppTemp = true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public boolean isRate() {
        return sharedPrefs.getBoolean(KEY_IS_RATE, false);
    }

    private void openMarket() {
        if (context == null) {
            return;
        }
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            try {
                context.startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException exception) {
                Toast.makeText(context, "Couldn't find PlayStore on this device", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
            iResultClickDialog.onCancel();
    }

    public interface IResultClickDialog {
        void onCancel();
        void later();

        void rateNow();
    }


}
