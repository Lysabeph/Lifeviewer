package com.comp30030.lifeviewer.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.comp30030.lifeviewer.R;
import com.comp30030.lifeviewer.adapters.RecyclerViewAdapter;
import com.comp30030.lifeviewer.data.Image;
import com.comp30030.lifeviewer.databinding.FragmentHighlightsBinding;
import com.comp30030.lifeviewer.viewmodels.HighlightsViewModel;
import com.comp30030.lifeviewer.viewmodels.ImageViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HighlightsFragment extends Fragment {

    private static final int NUMBER_OF_IMAGES_TO_PRELOAD = 10;
    private ImageViewModel imageViewModel;
    private FragmentHighlightsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_highlights, container, false);
        binding.setHighlightsHandlers(this);

        View root = binding.getRoot(); // inflater.inflate(R.layout.fragment_highlights, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.highlights_recycler_view);

        RecyclerViewAdapter highlightsAdapter = new RecyclerViewAdapter(getContext(), new ArrayList<>(),
                new RecyclerViewAdapter.ImageClickListener() {
                    @Override
                    public void onImageFavouriteToggled(Image image, ToggleButton toggleButton) {
                        if (image != null) {
                            Log.i("onImageFavouriteToggled", "image != null");
                            showUnfavouriteDialog(image, toggleButton);
                        }
                        else {
                            Log.i("onImageFavouriteToggled", "image == null");
                        }
                    }
                });

        ListPreloader.PreloadSizeProvider<Uri> sizeProvider = highlightsAdapter.getSizeProvider();
        RecyclerViewPreloader<Uri> preloader
                = new RecyclerViewPreloader<>(Glide.with(this), highlightsAdapter,
                sizeProvider, NUMBER_OF_IMAGES_TO_PRELOAD);

        recyclerView.addOnScrollListener(preloader);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(highlightsAdapter);

        imageViewModel.getFavouriteImages().observe(getViewLifecycleOwner(), new Observer<List<Image>>() {
            @Override
            public void onChanged(@Nullable final List<Image> images) {
                // Update the cached copy of the words in the adapter.
                binding.setHasFavourites(!images.isEmpty());
                highlightsAdapter.setImages(images);
            }
        });

        return root;
    }

    private void showUnfavouriteDialog(Image image, ToggleButton toggleButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setCancelable(true);
        builder.setTitle("Are you sure you would like to unfavourite this image?");
        //builder.setMessage("Message");
        builder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        image.toggleIsFavourite();
                        imageViewModel.update(image.getPath(),
                                image.getIsFavourite());
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toggleButton.setChecked(true);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onClickAddFavourites(View view) {
        Log.i("onClickAddFavourites", "clicked");
        NavController navController = Navigation.findNavController(this.getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_gallery);
    }
}
