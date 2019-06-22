package com.google.codelabs.mdc.java.shrine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment representing the login screen for Shrine.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, TextView.OnKeyListener {

    private TextInputEditText passwordEditText;
    private TextInputLayout passwordLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shr_login_fragment, container, false);

        // Snippet from "Navigate to the next Fragment" section goes here.
        MaterialButton next = view.findViewById(R.id.nextBtn);
        MaterialButton cancel = view.findViewById(R.id.cancelBtn);
        passwordLayout = view.findViewById(R.id.password_text_input);
        passwordEditText = view.findViewById(R.id.password_edit_text);

        next.setOnClickListener(this);
        cancel.setOnClickListener(this);
        passwordEditText.setOnKeyListener(this);

        return view;
    }

    private boolean isPasswordvalid(String password) {
        return password != null && password.length() >= 8;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nextBtn) {
            if (isPasswordvalid(passwordEditText.getText().toString())) {
                // Navigate to the next Fragment
               startActivity(new Intent(getActivity(), ProductGridActivity.class));
            } else {
                passwordLayout.setError(getString(R.string.shr_error_password));
            }
        } else if (view.getId() == R.id.cancelBtn) {
            getActivity().finish();
        }
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (view.getId() == R.id.password_edit_text) {
            if (isPasswordvalid(passwordEditText.getText().toString()))
                passwordLayout.setError(null);
        }

        return false;
    }

    // "isPasswordValid" from "Navigate to the next Fragment" section method goes here
}
