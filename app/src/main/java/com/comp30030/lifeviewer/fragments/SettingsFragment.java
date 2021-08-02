package com.comp30030.lifeviewer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.comp30030.lifeviewer.R;
import com.comp30030.lifeviewer.data.Image;
import com.comp30030.lifeviewer.viewmodels.ImageViewModel;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int GET_IMAGE_DIRECTORY_REQUEST_CODE = 1;

    private SharedPreferences sharedPref;

    private Preference imageDirectoryPathPreference;

    private String imageKey = "image_directory_path";

    private ImageViewModel imageViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        imageDirectoryPathPreference = findPreference(imageKey);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        imageDirectoryPathPreference.setSummary(sharedPref.getString(imageKey, "Image path not found"));

        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);

        imageDirectoryPathPreference.setOnPreferenceClickListener(preference -> {
            selectNewDirectory(GET_IMAGE_DIRECTORY_REQUEST_CODE);
            return true;
        });
    }

    private void selectNewDirectory(int requestCode) {
        // Choose a directory using the system's file picker.
        Intent directoryPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Provide read access to files and sub-directories in the user-selected directory.
        directoryPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        directoryPickerIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        startActivityForResult(directoryPickerIntent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.i("onActivityResult", "Getting result of button click");

        if (resultCode == Activity.RESULT_OK) {
            Log.i("resultCode", "Result was successful");

            if (requestCode == GET_IMAGE_DIRECTORY_REQUEST_CODE) {
                Log.i("requestCode", "is get directory code");

                try {
                    Uri uri = resultData.getData();

                    getActivity().getContentResolver()
                            .takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Log.i("Before commit","imagePath: "
                                               + sharedPref.getString(imageKey,null));

                    Log.i("imageDirectory", "Changing image directory");

                    SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                    sharedPrefEditor.putString(imageKey, uri.toString());
                    sharedPrefEditor.apply();

                    populateDatabase(uri);

                    imageDirectoryPathPreference.setSummary(uri.toString());
                    showSuccessfulToast();

                    Log.i("After commit",
                            "imagePath: " + sharedPref.getString(imageKey,null));


                }
                catch (Exception e) {
                    Log.w("Could not get uri path", "Exception msg: " + e.getMessage());
                    showUnsuccessfulToast();
                }
            }
            else {
                Log.w("requestCode", "Unknown request code");
                showUnsuccessfulToast();
            }
        }
        else {
            Log.w("resultCode", "Result was unsuccessful");
            showUnsuccessfulToast();
        }
    }

    private void showUnsuccessfulToast() {
        Toast unsuccessfulToast = Toast.makeText(getActivity(),
                                            "Failed to set new image folder",
                                                 Toast.LENGTH_LONG);
        unsuccessfulToast.show();
    }

    private void showSuccessfulToast() {
        Toast successfulToast = Toast.makeText(getActivity(), "New folder set",
                                               Toast.LENGTH_SHORT);
        successfulToast.show();
    }

    private void populateDatabase(Uri directoryUri) {
        DocumentFile directory = DocumentFile.fromTreeUri(getContext(), directoryUri);

        List<Image> images = getImagesSlow(directory);
        imageViewModel.deleteAll();
        imageViewModel.insert(images);
    }

    private List<Image> getImagesSlow(DocumentFile rootDocument) {
        List<Image> images = new ArrayList<>();

        if (rootDocument == null) {
            Log.d("directory", "is null");
        }
        else if (rootDocument.isDirectory()) {
            Log.i("directory", "type == directory");

            DocumentFile[] listOfSubDocuments = rootDocument.listFiles();

            for(DocumentFile subDocument : listOfSubDocuments) {
                images.addAll(getImagesSlow(subDocument));
            }
        }
        else if (rootDocument.getType() == null) {
            Log.d("directory", "type == null");
        }
        else if (rootDocument.getType().contains("image")) {
            Log.d("directory", "type == image");
            Uri uri = rootDocument.getUri();

            Log.i("documentUri", "" + uri);

            Image image = new Image(uri.toString(), rootDocument.lastModified(), false);
            images.add(image);
        }
        else {
            Log.d("directory", "type == " + rootDocument.getType());
        }

        return images;
    }
}