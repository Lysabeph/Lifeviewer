package com.comp30030.lifeviewer.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader.PreloadSizeProvider;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.comp30030.lifeviewer.R;
import com.comp30030.lifeviewer.adapters.RecyclerViewAdapter;
import com.comp30030.lifeviewer.data.Image;
import com.comp30030.lifeviewer.databinding.FragmentGalleryBinding;
import com.comp30030.lifeviewer.viewmodels.ImageViewModel;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private static final int NUMBER_OF_IMAGES_TO_PRELOAD = 10;
    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i("GalleryFragment", "onCreateView");

        ImageViewModel imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false);
        binding.setGalleryHandlers(this);

        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.gallery_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerViewAdapter galleryAdapter = new RecyclerViewAdapter(getContext(), new ArrayList<>(),
                new RecyclerViewAdapter.ImageClickListener() {
                    @Override
                    public void onImageFavouriteToggled(Image image, ToggleButton toggleButton) {
                        if (image != null) {
                            Log.i("onImageFavouriteToggled", "image != null");

                            image.toggleIsFavourite();
                            imageViewModel.update(image.getPath(),
                                                  image.getIsFavourite());
                        }
                        else {
                            Log.i("onImageFavouriteToggled", "image == null");
                        }
                    }
                });

        PreloadSizeProvider<Uri> sizeProvider = galleryAdapter.getSizeProvider();
        RecyclerViewPreloader<Uri> preloader
                = new RecyclerViewPreloader<>(Glide.with(this), galleryAdapter,
                sizeProvider, NUMBER_OF_IMAGES_TO_PRELOAD);

        recyclerView.addOnScrollListener(preloader);
        recyclerView.setAdapter(galleryAdapter);

        imageViewModel.getAllImages().observe(getViewLifecycleOwner(), new Observer<List<Image>>() {
            @Override
            public void onChanged(@Nullable final List<Image> images) {
                Log.i("test", "TEST");
                // Update the cached copy of the words in the adapter.
                binding.setHasImages(images != null && !images.isEmpty());
                galleryAdapter.setImages(images);
            }
        });

        return root;
    }

    public void onClickAddImages(View view) {
        Log.i("onClickAddImages", "clicked");
        NavController navController = Navigation.findNavController(this.getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_settings);
    }
}
