// Generated by view binder compiler. Do not edit!
package com.bulat.soshicon2.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bulat.soshicon2.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RowCardEventBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView ContentMessage;

  @NonNull
  public final TextView NameMessage;

  @NonNull
  public final TextView Time;

  @NonNull
  public final CircleImageView avatar;

  @NonNull
  public final TextView distance;

  private RowCardEventBinding(@NonNull ConstraintLayout rootView, @NonNull TextView ContentMessage,
      @NonNull TextView NameMessage, @NonNull TextView Time, @NonNull CircleImageView avatar,
      @NonNull TextView distance) {
    this.rootView = rootView;
    this.ContentMessage = ContentMessage;
    this.NameMessage = NameMessage;
    this.Time = Time;
    this.avatar = avatar;
    this.distance = distance;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowCardEventBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowCardEventBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_card_event, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowCardEventBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ContentMessage;
      TextView ContentMessage = ViewBindings.findChildViewById(rootView, id);
      if (ContentMessage == null) {
        break missingId;
      }

      id = R.id.NameMessage;
      TextView NameMessage = ViewBindings.findChildViewById(rootView, id);
      if (NameMessage == null) {
        break missingId;
      }

      id = R.id.Time;
      TextView Time = ViewBindings.findChildViewById(rootView, id);
      if (Time == null) {
        break missingId;
      }

      id = R.id.avatar;
      CircleImageView avatar = ViewBindings.findChildViewById(rootView, id);
      if (avatar == null) {
        break missingId;
      }

      id = R.id.distance;
      TextView distance = ViewBindings.findChildViewById(rootView, id);
      if (distance == null) {
        break missingId;
      }

      return new RowCardEventBinding((ConstraintLayout) rootView, ContentMessage, NameMessage, Time,
          avatar, distance);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
