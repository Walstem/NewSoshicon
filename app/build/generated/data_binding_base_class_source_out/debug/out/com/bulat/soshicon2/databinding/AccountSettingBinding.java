// Generated by view binder compiler. Do not edit!
package com.bulat.soshicon2.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bulat.soshicon2.R;
import com.google.android.material.textview.MaterialTextView;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class AccountSettingBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout FAQ;

  @NonNull
  public final ImageView FAQImage;

  @NonNull
  public final ConstraintLayout SecondPanelSetting;

  @NonNull
  public final ConstraintLayout ThirdPanelSetting;

  @NonNull
  public final ConstraintLayout aboutUs;

  @NonNull
  public final ImageView aboutUsImage;

  @NonNull
  public final Button button;

  @NonNull
  public final ImageView cancel;

  @NonNull
  public final ConstraintLayout constraintLayout;

  @NonNull
  public final ConstraintLayout containerSetting;

  @NonNull
  public final ConstraintLayout firstPanelSetting;

  @NonNull
  public final ConstraintLayout languages;

  @NonNull
  public final ImageView languagesImage;

  @NonNull
  public final ConstraintLayout lightMode;

  @NonNull
  public final ImageView lightModeImage;

  @NonNull
  public final SwitchCompat lightModeSwitch;

  @NonNull
  public final ImageView logOut;

  @NonNull
  public final ConstraintLayout logOutSetting;

  @NonNull
  public final ConstraintLayout notification;

  @NonNull
  public final ImageView notificationImage;

  @NonNull
  public final CircleImageView profileAvatarSetting;

  @NonNull
  public final ConstraintLayout settingLogOut;

  @NonNull
  public final ConstraintLayout textSize;

  @NonNull
  public final ImageView textSizeImage;

  @NonNull
  public final MaterialTextView usernameBottomAvatar;

  private AccountSettingBinding(@NonNull ConstraintLayout rootView, @NonNull ConstraintLayout FAQ,
      @NonNull ImageView FAQImage, @NonNull ConstraintLayout SecondPanelSetting,
      @NonNull ConstraintLayout ThirdPanelSetting, @NonNull ConstraintLayout aboutUs,
      @NonNull ImageView aboutUsImage, @NonNull Button button, @NonNull ImageView cancel,
      @NonNull ConstraintLayout constraintLayout, @NonNull ConstraintLayout containerSetting,
      @NonNull ConstraintLayout firstPanelSetting, @NonNull ConstraintLayout languages,
      @NonNull ImageView languagesImage, @NonNull ConstraintLayout lightMode,
      @NonNull ImageView lightModeImage, @NonNull SwitchCompat lightModeSwitch,
      @NonNull ImageView logOut, @NonNull ConstraintLayout logOutSetting,
      @NonNull ConstraintLayout notification, @NonNull ImageView notificationImage,
      @NonNull CircleImageView profileAvatarSetting, @NonNull ConstraintLayout settingLogOut,
      @NonNull ConstraintLayout textSize, @NonNull ImageView textSizeImage,
      @NonNull MaterialTextView usernameBottomAvatar) {
    this.rootView = rootView;
    this.FAQ = FAQ;
    this.FAQImage = FAQImage;
    this.SecondPanelSetting = SecondPanelSetting;
    this.ThirdPanelSetting = ThirdPanelSetting;
    this.aboutUs = aboutUs;
    this.aboutUsImage = aboutUsImage;
    this.button = button;
    this.cancel = cancel;
    this.constraintLayout = constraintLayout;
    this.containerSetting = containerSetting;
    this.firstPanelSetting = firstPanelSetting;
    this.languages = languages;
    this.languagesImage = languagesImage;
    this.lightMode = lightMode;
    this.lightModeImage = lightModeImage;
    this.lightModeSwitch = lightModeSwitch;
    this.logOut = logOut;
    this.logOutSetting = logOutSetting;
    this.notification = notification;
    this.notificationImage = notificationImage;
    this.profileAvatarSetting = profileAvatarSetting;
    this.settingLogOut = settingLogOut;
    this.textSize = textSize;
    this.textSizeImage = textSizeImage;
    this.usernameBottomAvatar = usernameBottomAvatar;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static AccountSettingBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static AccountSettingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.account_setting, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static AccountSettingBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.FAQ;
      ConstraintLayout FAQ = ViewBindings.findChildViewById(rootView, id);
      if (FAQ == null) {
        break missingId;
      }

      id = R.id.FAQ_image;
      ImageView FAQImage = ViewBindings.findChildViewById(rootView, id);
      if (FAQImage == null) {
        break missingId;
      }

      id = R.id.SecondPanelSetting;
      ConstraintLayout SecondPanelSetting = ViewBindings.findChildViewById(rootView, id);
      if (SecondPanelSetting == null) {
        break missingId;
      }

      id = R.id.ThirdPanelSetting;
      ConstraintLayout ThirdPanelSetting = ViewBindings.findChildViewById(rootView, id);
      if (ThirdPanelSetting == null) {
        break missingId;
      }

      id = R.id.aboutUs;
      ConstraintLayout aboutUs = ViewBindings.findChildViewById(rootView, id);
      if (aboutUs == null) {
        break missingId;
      }

      id = R.id.aboutUs_image;
      ImageView aboutUsImage = ViewBindings.findChildViewById(rootView, id);
      if (aboutUsImage == null) {
        break missingId;
      }

      id = R.id.button;
      Button button = ViewBindings.findChildViewById(rootView, id);
      if (button == null) {
        break missingId;
      }

      id = R.id.cancel;
      ImageView cancel = ViewBindings.findChildViewById(rootView, id);
      if (cancel == null) {
        break missingId;
      }

      id = R.id.constraintLayout;
      ConstraintLayout constraintLayout = ViewBindings.findChildViewById(rootView, id);
      if (constraintLayout == null) {
        break missingId;
      }

      ConstraintLayout containerSetting = (ConstraintLayout) rootView;

      id = R.id.firstPanelSetting;
      ConstraintLayout firstPanelSetting = ViewBindings.findChildViewById(rootView, id);
      if (firstPanelSetting == null) {
        break missingId;
      }

      id = R.id.languages;
      ConstraintLayout languages = ViewBindings.findChildViewById(rootView, id);
      if (languages == null) {
        break missingId;
      }

      id = R.id.languages_image;
      ImageView languagesImage = ViewBindings.findChildViewById(rootView, id);
      if (languagesImage == null) {
        break missingId;
      }

      id = R.id.lightMode;
      ConstraintLayout lightMode = ViewBindings.findChildViewById(rootView, id);
      if (lightMode == null) {
        break missingId;
      }

      id = R.id.lightMode_image;
      ImageView lightModeImage = ViewBindings.findChildViewById(rootView, id);
      if (lightModeImage == null) {
        break missingId;
      }

      id = R.id.lightModeSwitch;
      SwitchCompat lightModeSwitch = ViewBindings.findChildViewById(rootView, id);
      if (lightModeSwitch == null) {
        break missingId;
      }

      id = R.id.log_out;
      ImageView logOut = ViewBindings.findChildViewById(rootView, id);
      if (logOut == null) {
        break missingId;
      }

      id = R.id.logOutSetting;
      ConstraintLayout logOutSetting = ViewBindings.findChildViewById(rootView, id);
      if (logOutSetting == null) {
        break missingId;
      }

      id = R.id.notification;
      ConstraintLayout notification = ViewBindings.findChildViewById(rootView, id);
      if (notification == null) {
        break missingId;
      }

      id = R.id.notification_image;
      ImageView notificationImage = ViewBindings.findChildViewById(rootView, id);
      if (notificationImage == null) {
        break missingId;
      }

      id = R.id.profile_avatar_setting;
      CircleImageView profileAvatarSetting = ViewBindings.findChildViewById(rootView, id);
      if (profileAvatarSetting == null) {
        break missingId;
      }

      id = R.id.setting_log_out;
      ConstraintLayout settingLogOut = ViewBindings.findChildViewById(rootView, id);
      if (settingLogOut == null) {
        break missingId;
      }

      id = R.id.text_size;
      ConstraintLayout textSize = ViewBindings.findChildViewById(rootView, id);
      if (textSize == null) {
        break missingId;
      }

      id = R.id.text_size_image;
      ImageView textSizeImage = ViewBindings.findChildViewById(rootView, id);
      if (textSizeImage == null) {
        break missingId;
      }

      id = R.id.username_bottom_avatar;
      MaterialTextView usernameBottomAvatar = ViewBindings.findChildViewById(rootView, id);
      if (usernameBottomAvatar == null) {
        break missingId;
      }

      return new AccountSettingBinding((ConstraintLayout) rootView, FAQ, FAQImage,
          SecondPanelSetting, ThirdPanelSetting, aboutUs, aboutUsImage, button, cancel,
          constraintLayout, containerSetting, firstPanelSetting, languages, languagesImage,
          lightMode, lightModeImage, lightModeSwitch, logOut, logOutSetting, notification,
          notificationImage, profileAvatarSetting, settingLogOut, textSize, textSizeImage,
          usernameBottomAvatar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
