// Generated by view binder compiler. Do not edit!
package com.bulat.soshicon2.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bulat.soshicon2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RegistrationNameBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout containerRegistrationName;

  @NonNull
  public final MaterialTextView createUsernameInfo;

  @NonNull
  public final MaterialTextView createUsernameText;

  @NonNull
  public final MaterialTextView errorText;

  @NonNull
  public final ImageView imageView;

  @NonNull
  public final MaterialButton loginBtn;

  @NonNull
  public final TextInputLayout username;

  private RegistrationNameBinding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout containerRegistrationName,
      @NonNull MaterialTextView createUsernameInfo, @NonNull MaterialTextView createUsernameText,
      @NonNull MaterialTextView errorText, @NonNull ImageView imageView,
      @NonNull MaterialButton loginBtn, @NonNull TextInputLayout username) {
    this.rootView = rootView;
    this.containerRegistrationName = containerRegistrationName;
    this.createUsernameInfo = createUsernameInfo;
    this.createUsernameText = createUsernameText;
    this.errorText = errorText;
    this.imageView = imageView;
    this.loginBtn = loginBtn;
    this.username = username;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RegistrationNameBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RegistrationNameBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.registration_name, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RegistrationNameBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      ConstraintLayout containerRegistrationName = (ConstraintLayout) rootView;

      id = R.id.create_username_info;
      MaterialTextView createUsernameInfo = ViewBindings.findChildViewById(rootView, id);
      if (createUsernameInfo == null) {
        break missingId;
      }

      id = R.id.create_username_text;
      MaterialTextView createUsernameText = ViewBindings.findChildViewById(rootView, id);
      if (createUsernameText == null) {
        break missingId;
      }

      id = R.id.error_text;
      MaterialTextView errorText = ViewBindings.findChildViewById(rootView, id);
      if (errorText == null) {
        break missingId;
      }

      id = R.id.imageView;
      ImageView imageView = ViewBindings.findChildViewById(rootView, id);
      if (imageView == null) {
        break missingId;
      }

      id = R.id.login_btn;
      MaterialButton loginBtn = ViewBindings.findChildViewById(rootView, id);
      if (loginBtn == null) {
        break missingId;
      }

      id = R.id.username;
      TextInputLayout username = ViewBindings.findChildViewById(rootView, id);
      if (username == null) {
        break missingId;
      }

      return new RegistrationNameBinding((ConstraintLayout) rootView, containerRegistrationName,
          createUsernameInfo, createUsernameText, errorText, imageView, loginBtn, username);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
