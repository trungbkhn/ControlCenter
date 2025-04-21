package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tapbi.spark.controlcenter.utils.DensityUtils;

import androidx.annotation.Nullable;

public class RoundImageView extends androidx.appcompat.widget.AppCompatImageView{

  private Path path = new Path();
  private Paint paintRadius;
  public float width;
  private RectF rectFRadius;
  private DensityUtils densityUtils;
  private float radius = 10f;
  private Bitmap bitmap= null;

  public RoundImageView(Context context) {
    super(context);
    init();
  }

  public RoundImageView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    width = getWidth();
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  private void init(){
    setLayerType(LAYER_TYPE_NONE, null);
    paintRadius = new Paint();
    paintRadius.setAntiAlias(true);
    rectFRadius = new RectF(0f, 0f, 0f, 0f);
    densityUtils = new DensityUtils();
    setRadius(10f);
  }


  public void setBm(Bitmap bitmap){
    if (bitmap != null){
      this.bitmap =resize(bitmap, (int)densityUtils.pxFromDp(getContext(),58),  (int)densityUtils.pxFromDp(getContext(),58)) ;
    }else {
      this.bitmap = bitmap;
    }
    invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    rectFRadius.bottom = getHeight();
    rectFRadius.right = width;
    path.addRoundRect(
        rectFRadius,
        densityUtils.pxFromDp(getContext(), radius),
        densityUtils.pxFromDp(getContext(), radius),
        Path.Direction.CW
    );
    canvas.clipPath(path);
    canvas.drawRect(rectFRadius, paintRadius);
    if (bitmap != null){
      this.bitmap =resize(bitmap,  (int)densityUtils.pxFromDp(getContext(),58), (int)densityUtils.pxFromDp(getContext(),58));
      canvas.drawBitmap(bitmap, 0f,0f, new Paint());
    }
  }

  private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
    if (image == null) return null;
    Bitmap newBitmap = image;
    if (maxHeight > 0 && maxWidth > 0) {
      float width = newBitmap.getWidth();
      float height = newBitmap.getHeight();
      float ratioBitmap = width / height;
      float ratioMax = maxWidth / maxHeight;
      int finalWidth = maxWidth;
      int finalHeight = maxHeight;
      if (ratioMax > ratioBitmap) {
        finalWidth = (int) (maxHeight * ratioBitmap);
      } else {
        finalHeight = (int) (maxWidth / ratioBitmap);
      }
      newBitmap = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
    }
    return newBitmap;
  }


  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (bitmap != null && !bitmap.isRecycled()) {
      bitmap.recycle();
      bitmap = null;
    }
  }
}
