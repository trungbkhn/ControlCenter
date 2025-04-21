package com.tapbi.spark.controlcenter.feature.controlios14.view.noty;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

public class MaskItemNotyView extends ConstraintLayout {

    private Context context;

    private Path path;
    private int radius;
    private int margin;
    private int widthLayoutSwipe;
    private int x = 0;
    private int more = 0;
    private int heightLayoutMore = 0;

    private View card1, card2;
    private int card1Height = 0, card2Height = 0;

    private int marginCard;

    public MaskItemNotyView(Context context) {
        super(context);
        init(context);
    }

    public MaskItemNotyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaskItemNotyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        margin = MethodUtils.dp2px(context, 10);
        radius = MethodUtils.dp2px(context, 10);
        marginCard = MethodUtils.dp2px(context, getResources().getDimension(R.dimen._8sdp));
        path = new Path();

        post(new Runnable() {
            @Override
            public void run() {
                path.addRoundRect(new RectF(
                                margin,
                                0,
                                getWidth() - margin,
                                getHeight() - heightLayoutMore),
                        radius, radius,
                        Path.Direction.CW);

                path.addRoundRect(new RectF(
                                getWidth(),
                                0,
                                getWidth() + widthLayoutSwipe,
                                getHeight() - heightLayoutMore),
                        radius, radius,
                        Path.Direction.CW);
            }
        });
    }
//
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }

    public void setCard(View card1, View card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

    public void setWidthLayout(int widthLayoutSwipe) {
        this.widthLayoutSwipe = widthLayoutSwipe;
    }

    public void update() {
        post(new Runnable() {
            @Override
            public void run() {
                setTranslationX(x);
            }
        });

    }

    public void setMoreNoty(int m) {
        this.more = m;

        if (card1 != null && card2 != null) {
            if (card1Height == 0 || card2Height == 0) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        card1Height = card1.getHeight();
                        card2Height = card2.getHeight();

                        setHeightLayoutMore();
                    }
                });
            } else {
                setHeightLayoutMore();
            }
        }
    }

    private void setHeightLayoutMore() {
        if (more == 1) {
            heightLayoutMore = 0;
        } else if (more == 2) {
            heightLayoutMore = card1Height;
        } else {
            heightLayoutMore = card1Height + card2Height;
        }
//        invalidate();
    }

    public void setTranslationX(int x) {
        this.x = x;
        path.reset();
        path.addRoundRect(new RectF(
                        margin - x,
                        0,
                        getWidth() - margin - x,
                        getHeight() - heightLayoutMore),
                radius, radius,
                Path.Direction.CW);

        path.addRoundRect(new RectF(
                        getWidth() - x,
                        0,
                        getWidth() + widthLayoutSwipe - x - margin,
                        getHeight() - heightLayoutMore),
                radius, radius,
                Path.Direction.CW);

        if (more == 1) {

        } else if (more == 2) {
            Path pathCard1 = new Path();
            pathCard1.moveTo(marginCard - x, getHeight() - card1Height);
            pathCard1.cubicTo(marginCard - x, getHeight() - card1Height,
                    marginCard - x + (int) (card1Height / 5), getHeight() - (int) (card1Height / 5),
                    marginCard - x + card1Height, getHeight()
            );

            pathCard1.lineTo(getWidth() - marginCard - x - card1Height, getHeight());
            pathCard1.cubicTo(getWidth() - marginCard - x - card1Height, getHeight(),
                    getWidth() - marginCard - x - (int) (card1Height / 5), getHeight() - (int) (card1Height / 5),
                    getWidth() - marginCard - x, getHeight() - card1Height
            );

            pathCard1.lineTo(marginCard - x, getHeight() - card1Height);
            pathCard1.close();

            path.addPath(pathCard1);

        } else {

            Path pathCard1 = new Path();
            pathCard1.moveTo(marginCard - x, getHeight() - (card1Height + card2Height));
            pathCard1.cubicTo(marginCard - x, getHeight() - (card1Height + card2Height),
                    marginCard - x + (int) (card1Height / 5), getHeight() - card2Height - (int) (card1Height / 5),
                    marginCard - x + card1Height, getHeight() - card2Height
            );

            pathCard1.lineTo(getWidth() - marginCard - x - card1Height, getHeight() - card2Height);
            pathCard1.cubicTo(getWidth() - marginCard - x - card1Height, getHeight() - card2Height,
                    getWidth() - marginCard - x - (int) (card1Height / 5), getHeight() - card2Height - (int) (card1Height / 5),
                    getWidth() - marginCard - x, getHeight() - (card1Height + card2Height)
            );

            pathCard1.lineTo(marginCard - x, getHeight() - (card1Height + card2Height));
            pathCard1.close();

            path.addPath(pathCard1);

            Path pathCard2 = new Path();
            pathCard2.moveTo((marginCard * 2) - margin - x, getHeight() - card2Height);
            pathCard2.cubicTo((marginCard * 2) - margin - x, getHeight() - card2Height,
                    (marginCard * 2) - margin - x + (int) (card2Height / 5), getHeight() - (int) (card2Height / 5),
                    (marginCard * 2) - margin - x + card2Height, getHeight()
            );

            pathCard2.lineTo(getWidth() - (marginCard * 2) + margin - x - card2Height, getHeight());
            pathCard2.cubicTo(getWidth() - (marginCard * 2) + margin - x - card2Height, getHeight(),
                    getWidth() - (marginCard * 2) + margin - x - (int) (card2Height / 5), getHeight() - (int) (card2Height / 5),
                    getWidth() - (marginCard * 2) + margin - x, getHeight() - card2Height
            );

            pathCard2.lineTo((marginCard * 2) - margin - x, getHeight() - card2Height);
            pathCard2.close();


            path.addPath(pathCard2);
        }

        invalidate();
    }
}
