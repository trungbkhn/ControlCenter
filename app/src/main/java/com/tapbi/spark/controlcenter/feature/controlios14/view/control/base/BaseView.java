package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewDebug;
import android.view.ViewParent;
import android.widget.RemoteViews.RemoteView;

import com.tapbi.spark.controlcenter.R;

import timber.log.Timber;

@RemoteView
public class BaseView extends AppCompatImageView {
    private int a;
    private int b;
    protected int c;
    protected int d;
    protected int e;
    protected int f;
    protected ViewParent g;
    protected int h;
    protected int i;
    protected int j;
    protected int k;
    Bitmap f5253l;
    private int f5254m;
    protected Drawable f5255n;
    protected Drawable f5256o;
    private boolean f5257p;
    private C0039a f5258q;
    private long f5259r;
    protected float ratioProgress = 0;
    protected String colorBackgroundSeekbar = "#000000";
    protected float cornerBackground = 100;
    private boolean isHorizontal = false;

    class C0039a implements Runnable {
        int f379a;
        int f380b;
        boolean f381c;
        final BaseView f382d;

        C0039a(BaseView BaseView, int i, int i2, boolean z) {
            this.f382d = BaseView;
            this.f379a = i;
            this.f380b = i2;
            this.f381c = z;
        }

        public final void run() {
            this.f382d.m4861a(this.f379a, this.f380b, this.f381c);
            this.f382d.f5258q = this;
        }
    }

    static class C0041b extends BaseSavedState {
        public static final Creator<C0041b> CREATOR = new C00401();
        int f383a;
        int f384b;

        static class C00401 implements Creator<C0041b> {
            C00401() {
            }

            public final C0041b createFromParcel(Parcel parcel) {
                return new C0041b(parcel);
            }

            public final C0041b[] newArray(int i) {
                return new C0041b[i];
            }
        }

        private C0041b(Parcel parcel) {
            super(parcel);
            this.f383a = parcel.readInt();
            this.f384b = parcel.readInt();
        }

        C0041b(Parcelable parcelable) {
            super(parcelable);
        }

        public final void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            if (this.f383a != this.f384b){
                parcel.writeInt(this.f383a);
                parcel.writeInt(this.f384b);
            } else {
                parcel.writeInt(this.f383a);
            }
        }
    }

    public BaseView(Context context) {
        this(context, null);
        setBackgroundColor(0);
    }

    public BaseView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842871);
        setBackgroundColor(0);
    }

    public BaseView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setBackgroundColor(0);
        this.f5259r = Thread.currentThread().getId();
        this.f5254m = 100;
        this.a = 0;
        this.b = 0;
        this.h = 24;
        this.i = 48;
        this.j = 24;
        this.k = 48;
        int[] progressBar = new int[]{16843039, 16843040, 16843062, 16843063, 16843064, 16843068, 16843071, 16843072};
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, progressBar, i, 0);
        this.f5257p = true;
        Drawable drawable = obtainStyledAttributes.getDrawable((int) 5);
        if (drawable != null) {
            setProgressDrawable(m4860a(drawable, false));
        }
        this.h = obtainStyledAttributes.getDimensionPixelSize((int) 6, this.h);
        this.i = obtainStyledAttributes.getDimensionPixelSize((int) 0, this.i);
        this.j = obtainStyledAttributes.getDimensionPixelSize((int) 7, this.j);
        this.k = obtainStyledAttributes.getDimensionPixelSize((int) 1, this.k);
        setMax(obtainStyledAttributes.getInt((int) 2, this.f5254m));
        setProgress(obtainStyledAttributes.getInt((int) 3, this.a));
        setSecondaryProgress(obtainStyledAttributes.getInt((int) 4, this.b));
        this.f5257p = false;
        obtainStyledAttributes.recycle();
    }

    private Drawable m4860a(Drawable drawable, boolean z) {
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            int numberOfLayers = layerDrawable.getNumberOfLayers();
            Drawable[] drawableArr = new Drawable[numberOfLayers];
            for (int i = 0; i < numberOfLayers; i++) {
                boolean z2;
                int id = layerDrawable.getId(i);
                Drawable drawable2 = layerDrawable.getDrawable(i);
                if (id != 16908301) {
                    if (id != 16908303) {
                        z2 = false;
                        drawableArr[i] = m4860a(drawable2, z2);
                    }
                }
                z2 = true;
                drawableArr[i] = m4860a(drawable2, z2);
            }
            Drawable layerDrawable2 = new LayerDrawable(drawableArr);
            for (int i2 = 0; i2 < numberOfLayers; i2++) {
                layerDrawable.setId(i2, layerDrawable.getId(i2));
            }
            return layerDrawable2;
        } else if (drawable instanceof StateListDrawable) {
            return new StateListDrawable();
        } else {
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (this.f5253l == null) {
                    this.f5253l = bitmap;
                }
                drawable = new ShapeDrawable(getDrawableShape());
                if (z) {
                    return new ClipDrawable(drawable, 3, 1);
                }
            }

            return drawable;
        }
    }

    private synchronized void m4861a(int i, int i2, boolean z) {
        float f = this.f5254m > 0 ? ((float) i2) / ((float) this.f5254m) : 0.0f;
        ratioProgress = f;
        Drawable drawable = this.f5256o;
        if (drawable != null) {
            Drawable drawable2 = null;
            if (drawable instanceof LayerDrawable) {
                drawable2 = ((LayerDrawable) drawable).findDrawableByLayerId(i);
            }
            int i3 = (int) (10000.0f * f);
            if (drawable2 != null) {
                drawable = drawable2;
            }

            drawable.setLevel(i3);
        } else {
            invalidate();
        }
        if (i == 16908301) {
            mo2184a(f, z);
        }
    }

    private synchronized void m4863b(int i, int i2, boolean z) {
        if (this.f5259r == Thread.currentThread().getId()) {
            m4861a(i, i2, z);
            return;
        }
        C0039a runnable;
        if (this.f5258q != null) {
            runnable = this.f5258q;
            this.f5258q = null;
            runnable.f379a = i;
            runnable.f380b = i2;
            runnable.f381c = z;
        } else {
            runnable = new C0039a(this, i, i2, z);
        }
        post(runnable);
    }

    protected void mo2184a(float f, boolean z) {
    }

    public final synchronized void m4865a(int i, boolean z) {
        if (i < 0) {
            i = 0;
        }
        if (i > this.f5254m) {
            i = this.f5254m;
        }
        if (i != this.a) {
            this.a = i;
            m4863b(16908301, this.a, z);
        }
    }

    public void drawableStateChanged() {
        super.drawableStateChanged();
        int[] drawableState = getDrawableState();
        if (this.f5255n != null && this.f5255n.isStateful()) {
            this.f5255n.setState(drawableState);
        }
    }

    public Drawable getCurrentDrawable() {
        return this.f5256o;
    }

    Shape getDrawableShape() {
        return new RoundRectShape(new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f}, null, null);
    }

    @ViewDebug.ExportedProperty
    public synchronized int getMax() {
        return this.f5254m;
    }

    @ViewDebug.ExportedProperty
    public synchronized int getProgress() {
        return this.a;
    }

    public Drawable getProgressDrawable() {
        return this.f5255n;
    }

    @ViewDebug.ExportedProperty
    public synchronized int getSecondaryProgress() {
        return this.b;
    }


    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = this.f5256o;
        if (drawable != null) {
            canvas.save();
            if (colorBackgroundSeekbar != null) {
                drawable.setTint(Color.parseColor(colorBackgroundSeekbar));
            }
            canvas.translate((float) this.c, (float) this.e);
            drawable.draw(canvas);
            canvas.restore();
        }
        drawThumb(canvas);
    }

    protected void drawThumb(Canvas canvas){}

    public void changeColorAndCornerProgess(String colorBackgroundSeekbar, Float cornerBackground, boolean isHorizontal){
        this.colorBackgroundSeekbar = colorBackgroundSeekbar;
        this.cornerBackground = cornerBackground;
        this.isHorizontal = isHorizontal;
        checkBackroundProgress();
    }

    private void checkBackroundProgress(){
        int idDrawable = R.drawable.progress_seekbar_horizontal;
        if (!isHorizontal){
            idDrawable = R.drawable.progress_seekbar;
        }
        if (this.cornerBackground == 200){
           idDrawable = R.drawable.progress_seekbar_horizontal_200;
        }
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getContext().getDrawable(idDrawable);
        setProgressDrawable(drawable);
    }

    protected synchronized void onMeasure(int i, int i2) {
        int max;
        Drawable drawable = this.f5256o;
        int i3 = 0;
        if (drawable != null) {
            i3 = Math.max(this.h, Math.min(this.i, drawable.getIntrinsicWidth()));
            max = Math.max(this.j, Math.min(this.k, drawable.getIntrinsicHeight()));
        } else {
            max = 0;
        }
        setMeasuredDimension(BaseView.resolveSize(i3 + (this.c + this.d), i), BaseView.resolveSize(max + (this.e + this.f), i2));
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        C0041b c0041b = (C0041b) parcelable;
        super.onRestoreInstanceState(c0041b.getSuperState());
        setProgress(c0041b.f383a);
        setSecondaryProgress(c0041b.f384b);
    }

    public Parcelable onSaveInstanceState() {
        C0041b c0041b = new C0041b(super.onSaveInstanceState());
        c0041b.f383a = this.a;
        c0041b.f384b = this.b;
        return c0041b;
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        i = (i - this.d) - this.c;
        i2 = (i2 - this.f) - this.e;
        Timber.e("Duongcv " + i +":"+i2);
        if (this.f5255n != null) {
            this.f5255n.setBounds(0, 0, i, i2);
        }
    }

    public void postInvalidate() {
        if (!this.f5257p) {
            super.postInvalidate();
        }
    }

    public synchronized void setMax(int i) {
        if (i < 0) {
            i = 0;
        }
        if (i != this.f5254m) {
            this.f5254m = i;
            postInvalidate();
            if (this.a > i) {
                this.a = i;
                m4863b(16908301, this.a, false);
            }
        }
    }

    public synchronized void setProgress(int i) {
        m4865a(i, false);
    }

    public void setProgressDrawable(Drawable drawable) {
        if (drawable != null) {
            drawable.setCallback(this);
            int minimumHeight = drawable.getMinimumHeight();
            if (this.k < minimumHeight) {
                this.k = minimumHeight;
                requestLayout();
            }
        }
        this.f5255n = drawable;
        this.f5256o = drawable;
        postInvalidate();
    }

    public synchronized void setSecondaryProgress(int i) {
        if (i < 0) {
            i = 0;
        }
        if (i > this.f5254m) {
            i = this.f5254m;
        }
        if (i != this.b) {
            this.b = i;
            m4863b(16908303, this.b, false);
        }
    }

    public void setVisibility(int i) {
        if (getVisibility() != i) {
            super.setVisibility(i);
        }
    }

    protected boolean verifyDrawable(Drawable drawable) {
        if (drawable != this.f5255n) {
            if (!super.verifyDrawable(drawable)) {
                return false;
            }
        }
        return true;
    }
}
