package company.librate.view;

import android.graphics.drawable.Drawable;


interface SimpleRatingBar {


    void setRating(float rating);

    float getRating();



    void setEmptyDrawable(Drawable drawable);


    void setFilledDrawable(Drawable drawable);



}
