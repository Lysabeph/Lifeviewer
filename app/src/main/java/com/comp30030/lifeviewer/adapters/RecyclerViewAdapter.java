package com.comp30030.lifeviewer.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.comp30030.lifeviewer.R;
import com.comp30030.lifeviewer.data.Image;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
                                          implements ListPreloader.PreloadModelProvider<Uri> {

    private static final int IMAGE_SIZE = 1000; // In pixels.
    private final int screenWidth;

    private final LayoutInflater inflater;
    private final Context context;
    private final ViewPreloadSizeProvider sizeProvider;
    private List<Image> data;

    private final ImageClickListener imageClickListener;

    public interface ImageClickListener {
        void onImageFavouriteToggled(Image image, ToggleButton toggleButton);
    }

    // Stores and recycles ImageViews as they are scrolled off the screen.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private WeakReference<ImageClickListener> listenerRef;

        ImageView imageView;
        ToggleButton favouriteButton;
        ToggleButton commentButton;
        ToggleButton shareButton;
        EditText commentText;

        ViewHolder(final View itemView, ImageClickListener clickListener) {
            super(itemView);

            listenerRef = new WeakReference<>(clickListener);

            imageView = itemView.findViewById(R.id.recycler_image_view);
            favouriteButton = itemView.findViewById(R.id.favourite_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            shareButton = itemView.findViewById(R.id.share_button);

            commentText = itemView.findViewById(R.id.comment_text);

            final GestureDetector gestureDetector
                    = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.i("OnDoubleTapListener", "onDoubleTap");
                    favouriteButton.performClick();
                    return true;
                }

                @Override public void onLongPress(MotionEvent e) { super.onLongPress(e); }
                @Override public boolean onDoubleTapEvent(MotionEvent e) { return true; }
                @Override public boolean onDown(MotionEvent e) { return true; }
            });


            imageView.setOnTouchListener((v, event) -> {
                v.performClick();
                return gestureDetector.onTouchEvent(event);
            });

            favouriteButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                compoundButton.startAnimation(getButtonAnimation(0.0f));
                Log.i("buttonToggled", "favouriteButton");
            });
            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("favouriteButton", "onClick");
                    listenerRef.get().onImageFavouriteToggled(data.get(getLayoutPosition()), favouriteButton);
                }
            });

            commentButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                compoundButton.startAnimation(getButtonAnimation(0.9f));
                Log.i("buttonToggled", "commentButton");

                if (commentButton.isChecked()) {
                    Log.i("commentButtonToggled", "is checked");
                    commentText.setVisibility(View.VISIBLE);
                    commentText.requestFocus();
                    showKeyboard();
                } else {
                    Log.i("commentButtonToggled", "is not checked");
                    commentText.setVisibility(View.GONE);
                    commentText.clearFocus();
                    hideKeyboard();
                }
            });

            shareButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                compoundButton.startAnimation(getButtonAnimation(0.9f));
                Log.i("buttonToggled", "shareButton");
            });
        }

        private void showKeyboard(){
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        private void hideKeyboard(){
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }

        private ScaleAnimation getButtonAnimation(float shrinkValue) {
            final ScaleAnimation favouriteAnimation
                    = new ScaleAnimation(shrinkValue, 1.0f, shrinkValue, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            favouriteAnimation.setDuration(500);
            BounceInterpolator bounceInterpolator = new BounceInterpolator();
            favouriteAnimation.setInterpolator(bounceInterpolator);
            return favouriteAnimation;
        }
    }

    public RecyclerViewAdapter(Context context, List<Image> images, ImageClickListener clickListener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.sizeProvider = new ViewPreloadSizeProvider<>();
        this.data = images;

        this.imageClickListener = clickListener;

        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        Log.i("screenWidth", "" + screenWidth);
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.image_recycler, parent, false);
        return new ViewHolder(view, imageClickListener);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView imageView = holder.imageView;
        Image image = data.get(position);

        Uri imageUri = Uri.parse(image.getPath());
        Log.i("imageUri", "" + imageUri);

        Glide.with(context)
                .load(imageUri)
                .override(screenWidth, Target.SIZE_ORIGINAL)
                .placeholder(R.drawable.ic_refresh_black_24dp)
                .fitCenter()
                .into(imageView);

        holder.favouriteButton.setChecked(image.getIsFavourite());
        Log.i("imageIsFavourite", "" + image.getIsFavourite());
    }

    public void setImages(List<Image> images){
        this.data = images;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        else {
            return 0;
        }
    }

    @Override
    @NonNull
    public List<Uri> getPreloadItems(int position) {
        Uri uri = Uri.parse(data.get(position).getPath());

        if (uri == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(uri);
    }

    @Override
    public RequestBuilder<Drawable> getPreloadRequestBuilder(@NonNull Uri uri) {
        return Glide.with(context)
                .load(uri)
                .override(IMAGE_SIZE, IMAGE_SIZE);
    }

    public ListPreloader.PreloadSizeProvider<Uri> getSizeProvider() {
        return sizeProvider;
    }
}
