package com.example.practice16;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

public class AnimImageFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_anim_image, container, false);
        view.findViewById(R.id.imageForLoaded).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.logo_rotate_animation));
        return view;
    }
}
