package com.example.tacademy.networkexample;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by dongja94 on 2016-02-05.
 */
public class ItemView extends FrameLayout {
    public ItemView(Context context) {
        super(context);
        init();
    }

    ImageView iconView;
    TextView titleView, directorView;

    private void init() {
        inflate(getContext(), R.layout.view_item, this);
        iconView = (ImageView)findViewById(R.id.image_icon);
        titleView = (TextView)findViewById(R.id.text_title);
        directorView = (TextView)findViewById(R.id.text_director);
    }

    Region_Item item;

    public void setMovieItem(Region_Item item) {
        this.item = item;
        titleView.setText(Html.fromHtml(item.region_name));
//        directorView.setText(item.director);
        if (!TextUtils.isEmpty(item.region_photo_url)) {
//            Picasso.with(getContext())
//                    .load(Uri.parse(item.image))
//                    .into(iconView);
            Glide.with(getContext())
                    .load(item.region_photo_url)
                    .into(iconView);
        } else {
            iconView.setImageResource(R.mipmap.ic_launcher);
        }
    }
}
