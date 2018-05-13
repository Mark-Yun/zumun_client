package com.mark.zumo.server.store.view.signup.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.server.store.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 13.
 */
public class SignInFragment extends Fragment {
    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.button_sign_in)
    void onClickSignin() {

    }

    @OnClick(R.id.go_to_sign_up)
    void goToSignUp() {
        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, Fragment.instantiate(getActivity(), SignUpFragment.class.getName()))
                .commit();
    }
}
