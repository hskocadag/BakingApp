package com.example.android.bakingapp.utilities;

import android.content.Context;
import android.widget.ImageView;

import com.example.android.bakingapp.R;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class ImageUtils {
    public static void insertImageIntoView(ImageView imageView, Context context, String imageUrl)
    {
        if(imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(R.mipmap.default_baking_img);
            return;
        }
        Picasso.with(context).load(imageUrl.toString()).placeholder(R.mipmap.default_baking_img).into(imageView);
    }
}
