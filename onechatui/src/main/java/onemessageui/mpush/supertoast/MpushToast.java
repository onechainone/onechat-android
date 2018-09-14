/*
 * Copyright 2013-2016 John Persano
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package onemessageui.mpush.supertoast;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import sdk.android.onechatui.R;
import onemessageui.mpush.supertoast.utils.BackgroundUtils;
import onemessageui.mpush.supertoast.utils.ListenerUtils;

/**
 * MpushToasts resemble stock {@link android.widget.Toast}s but are added
 * to an Activity's ViewGroup. MpushToasts can receive touch events and
 * be shown indefinitely. For usage information, check out the
 * <a href="https://github.com/JohnPersano/Supertoasts/wiki/MpushToast">MpushToast Wiki page</a>.
 */
@SuppressWarnings({"UnusedDeclaration", "RedundantCast", "UnusedReturnValue"})
public class MpushToast extends SuperToast {

    // Bundle tag with a hex as a string so it's highly unlikely to interfere with other keys in the bundle
    private static final String BUNDLE_KEY = "0x532e412e542e";

    /**
     * Listener that calls onClick() when a TYPE_BUTTON MpushToast receives a Button press event.
     */
    public interface OnButtonClickListener {

        /**
         * Called when a TYPE_BUTTON MpushToast's Button is pressed.
         *
         * @param view  The View that was clicked
         * @param token A Parcelable token that can hold data across orientation changes
         */
        void onClick(View view, Parcelable token);
    }

    private Context mContext;
    private View mView;
    private ProgressBar mProgressBar;
    private Style mStyle;
    private OnButtonClickListener mOnButtonClickListener;
    private boolean mFromOrientationChange;

    /**
     * Public constructor for a MpushToast.
     *
     * @param context An Activity Context
     */
    public MpushToast(@NonNull Context context) {
        super(context);

        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("MpushToast Context must be an Activity.");
        }

        this.mContext = context;
        this.mStyle = this.getStyle(); // Style is created in the super(context) call

    }

    /**
     * Public constructor for a MpushToast.
     *
     * @param context An Activity Context
     * @param style   The desired Style
     */
    public MpushToast(@NonNull Context context, @NonNull Style style) {
        super(context, style);

        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("MpushToast Context must be an Activity.");
        }

        this.mContext = context;
        this.mStyle = style;

    }

    /**
     * Public constructor for a MpushToast.
     *
     * @param context An Activity Context
     * @param type    The desired MpushToast type
     */
    public MpushToast(@NonNull Context context, @Style.Type int type) {
        super(context, type);

        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("MpushToast Context must be an Activity.");
        }

        this.mContext = context;
        this.mStyle = this.getStyle(); // Style is created in the super(context) call

    }

    /**
     * Public constructor for a MpushToast.
     *
     * @param context An Activity Context
     * @param style   The desired Style
     * @param type    The desired MpushToast type
     */
    public MpushToast(@NonNull Context context, @NonNull Style style, @Style.Type int type) {
        super(context, style, type);

//        if (!(context instanceof Activity)) {
//            throw new IllegalArgumentException("MpushToast Context must be an Activity.");
//        }

        this.mContext = context;
        this.mStyle = this.getStyle(); // Style is created in the super(context) call

    }

    /**
     * Public constructor for a MpushToast.
     *
     * @param context     An Activity Context
     * @param style       The desired Style
     * @param type        The desired MpushToast type
     * @param viewGroupId The id of the ViewGroup to attach the MpushToast to
     */
    public MpushToast(@NonNull Context context, @NonNull Style style,
                      @Style.Type int type, @IdRes int viewGroupId) {
        super(context, style, type, viewGroupId);

        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("MpushToast Context must be an Activity.");
        }

        this.mContext = context;
        this.mStyle = this.getStyle(); // Style is created in the super(context) call

    }

    /**
     * Handle the inflation of the appropriate View according to the desired type.
     *
     * @param context        An Activity Context
     * @param layoutInflater The LayoutInflater created from the Context
     * @param type           The desired MpushToast type
     * @return The MpushToast View
     */
    @Override
    protected View onCreateView(@NonNull Context context, LayoutInflater layoutInflater,
                                @Style.Type int type) {

        this.mView = layoutInflater.inflate(R.layout.push_simple_window_view, null);

        return this.mView;
    }

    /**
     */
    @Override
    public SuperToast setOnDismissListener(String tag, Parcelable token,
                                           @NonNull OnDismissListener onDismissListener) {
        return super.setOnDismissListener(tag, token, onDismissListener);
    }

    /**
     */
    @Override
    public SuperToast setOnDismissListener(String tag,
                                           @NonNull OnDismissListener onDismissListener) {
        return super.setOnDismissListener(tag, onDismissListener);
    }

    /**
     * Returns the dismiss tag of the MpushToast.
     *
     * @return The dismiss tag
     */
    @Override
    public String getDismissTag() {
        return super.getDismissTag();
    }

    /**
     * Returns the dismiss Parcelable token of the MpushToast.
     *
     * @return The dismiss Parcelable token
     */
    @Override
    public Parcelable getDismissToken() {
        return super.getDismissToken();
    }

    /**
     * Protected method used by the Toaster to know when not to use the
     * show animation.
     *
     * @return The current MpushToast instance
     */
    protected MpushToast fromOrientationChange() {
        this.mFromOrientationChange = true;
        return this;
    }

    /**
     * Protected method used by the Toaster to know when not to use the
     * show animation.
     *
     * @return true if coming from orientation change
     */
    protected boolean isFromOrientationChange() {
        return this.mFromOrientationChange;
    }

    /**
     */
    public MpushToast setIndeterminate(boolean indeterminate) {
        this.mStyle.isIndeterminate = indeterminate;

        // Make sure the MpushToast can be dismissed
        this.mStyle.touchToDismiss = true;
        return this;
    }

    /**
     * Returns true if the MpushToast is isIndeterminate.
     *
     * @return true if isIndeterminate
     */
    public boolean isIndeterminate() {
        return this.mStyle.isIndeterminate;
    }

    /**
     * Set a private OnTouchListener to the MpushToast which will dismiss
     * it if any part is touched.
     *
     * @param touchToDismiss true if should touch to dismiss
     * @return The current MpushToast instance
     */
    public MpushToast setTouchToDismiss(boolean touchToDismiss) {
        this.mStyle.touchToDismiss = touchToDismiss;
        return this;
    }

    /**
     * Returns true if the MpushToast is touch dismissible.
     *
     * @return true if touch dismissible
     */
    public boolean isTouchDismissible() {
        return this.mStyle.touchToDismiss;
    }

    /**
     * Set the text of the Button in a TYPE_BUTTON MpushToast. Generally,
     * this String should not exceed four characters. The String passed as the
     * parameter will be capitalized.
     *
     * @param buttonText The desired Button text
     * @return The current MpushToast instance
     */
    public MpushToast setButtonText(String buttonText) {
        this.mStyle.buttonText = buttonText;
        return this;
    }

    /**
     * Returns the Button text of a TYPE_BUTTON MpushToast.
     *
     * @return The Button text
     */
    public String getButtonText() {
        return this.mStyle.buttonText;
    }

    /**
     * Set the Typeface style of the Button text in a TYPE_BUTTON MpushToast.
     * In most cases, this should be {@link android.graphics.Typeface#BOLD}.
     *
     * @param buttonTypefaceStyle The desired Button text Typeface style
     * @return The current MpushToast instance
     */
    public MpushToast setButtonTypefaceStyle(@Style.TypefaceStyle
                                                     int buttonTypefaceStyle) {
        this.mStyle.buttonTypefaceStyle = buttonTypefaceStyle;
        return this;
    }

    /**
     * Returns the Button text Typeface style of a TYPE_BUTTON MpushToast.
     *
     * @return The Button text Typeface style
     */
    @Style.TypefaceStyle
    public int getButtonTypefaceStyle() {
        return this.mStyle.buttonTypefaceStyle;
    }

    /**
     * Set the color of the Button text in a TYPE_BUTTON MpushToast.
     *
     * @param buttonTextColor The desired Button text color
     * @return The current MpushToast instance
     */
    public MpushToast setButtonTextColor(@ColorInt int buttonTextColor) {
        this.mStyle.buttonTextColor = buttonTextColor;
        return this;
    }

    /**
     * Returns the Button text color of a TYPE_BUTTON MpushToast.
     *
     * @return The Button text color
     */
    @ColorInt
    public int getButtonTextColor() {
        return this.mStyle.buttonTextColor;
    }

    /**
     * Set the size of the Button text in a TYPE_BUTTON MpushToast.
     *
     * @param buttonTextSize The desired Button text size
     * @return The current MpushToast instance
     */
    public MpushToast setButtonTextSize(@Style.TextSize int buttonTextSize) {
        this.mStyle.buttonTextSize = buttonTextSize;
        return this;
    }

    /**
     * Returns the Button text size of a TYPE_BUTTON MpushToast.
     *
     * @return The Button text size
     */
    @Style.TextSize
    public int getButtonTextSize() {
        return this.mStyle.buttonTextSize;
    }

    /**
     * Set the color of the divider between the text and the Button in a TYPE_BUTTON
     * MpushToast.
     *
     * @param buttonDividerColor The desired divider color
     * @return The current MpushToast instance
     */
    public MpushToast setButtonDividerColor(@ColorInt int buttonDividerColor) {
        this.mStyle.buttonDividerColor = buttonDividerColor;
        return this;
    }

    /**
     * Returns the divider color of a TYPE_BUTTON MpushToast.
     *
     * @return The divider color
     */
    @ColorInt
    public int getButtonDividerColor() {
        return this.mStyle.buttonDividerColor;
    }

    /**
     * Set the Button icon resource in a TYPE_BUTTON MpushToast.
     *
     * @param buttonIconResource The desired icon resource
     * @return The current MpushToast instance
     */
    public MpushToast setButtonIconResource(@DrawableRes int buttonIconResource) {
        this.mStyle.buttonIconResource = buttonIconResource;
        return this;
    }

    /**
     * Returns the Button icon resource of a TYPE_BUTTON MpushToast.
     *
     * @return The Button icon resource
     */
    public int getButtonIconResource() {
        return this.mStyle.buttonIconResource;
    }

    /**
     */
    public MpushToast setOnButtonClickListener(@NonNull String tag, Parcelable token,
                                               @NonNull OnButtonClickListener onButtonClickListener) {
        this.mOnButtonClickListener = onButtonClickListener;
        this.mStyle.buttonTag = tag;
        this.mStyle.buttonToken = token;
        return this;
    }

    /**
     * Returns the button click tag of a TYPE_BUTTON MpushToast.
     *
     * @return The button click tag
     */
    public String getButtonTag() {
        return this.mStyle.buttonTag;
    }

    /**
     * Returns the button click Parcelable token of a TYPE_BUTTON MpushToast.
     *
     * @return The button click Parcelable token
     */
    public Parcelable getButtonToken() {
        return this.mStyle.buttonToken;
    }

    /**
     */
    public OnButtonClickListener getOnButtonClickListener() {
        return this.mOnButtonClickListener;
    }

    /**
     * Set the progress of the ProgressBar in a TYPE_PROGRESS_BAR MpushToast.
     * This can be called multiple times after the MpushToast is showing.
     *
     * @param progress The desired progress
     * @return The current MpushToast instance
     */
    public MpushToast setProgress(int progress) {
        if (this.mProgressBar == null) {
            Log.e(getClass().getName(), "Could not set MpushToast " +
                    "progress, are you sure you set the type to TYPE_PROGRESS_CIRCLE " +
                    "or TYPE_PROGRESS_BAR?");
            return this;
        }
        this.mStyle.progress = progress;
        this.mProgressBar.setProgress(progress);
        return this;
    }

    /**
     * Returns the ProgressBar progress of a TYPE_PROGRESS_BAR MpushToast.
     *
     * @return The progress
     */
    public int getProgress() {
        return this.mStyle.progress;
    }

    /**
     * REQUIRES API 21
     * <p>
     * Set the progress color of the ProgressBar in a TYPE_PROGRESS_BAR MpushToast.
     *
     * @param progressBarColor The desired progress color
     * @return The current MpushToast instance
     */
    public MpushToast setProgressBarColor(@ColorInt int progressBarColor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.w(getClass().getName(), "MpushToast.setProgressBarColor() requires API " +
                    "21 or newer.");
            return this;
        }
        this.mStyle.progressBarColor = progressBarColor;
        return this;
    }

    /**
     * REQUIRES API 21
     * <p>
     * Returns the ProgressBar progress of a TYPE_PROGRESS_BAR MpushToast.
     *
     * @return The progress color
     */
    @ColorInt
    public int getProgressBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.w(getClass().getName(), "MpushToast.getProgressBarColor() requires API " +
                    "21 or newer.");
            return 0;
        }
        return this.mStyle.progressBarColor;
    }

    /**
     * Set the maximum progress of the ProgressBar in a TYPE_PROGRESS_BAR MpushToast.
     *
     * @param progressMax The desired progress maximum
     * @return The current MpushToast instance
     */
    public MpushToast setProgressMax(int progressMax) {
        this.mStyle.progressMax = progressMax;
        return this;
    }

    /**
     * Returns the maximum progress of a TYPE_PROGRESS_BAR MpushToast.
     *
     * @return The maximum progress
     */
    public int getProgressMax() {
        return this.mStyle.progressMax;
    }

    /**
     * Set the ProgressBar to be isIndeterminate in a TYPE_PROGRESS_BAR MpushToast.
     *
     * @param progressIndeterminate true if progress should be isIndeterminate
     * @return The current MpushToast instance
     */
    public MpushToast setProgressIndeterminate(boolean progressIndeterminate) {
        this.mStyle.progressIndeterminate = progressIndeterminate;
        return this;
    }

    /**
     * Returns true if the MpushToast ProgressBar is isIndeterminate.
     *
     * @return true if isIndeterminate.
     */
    public boolean getProgressIndeterminate() {
        return this.mStyle.progressIndeterminate;
    }

    /**
     * Returns the ViewGroup that the MpushToast is being attached to.
     *
     * @return The ViewGroup
     */
    public View getViewGroup() {
        return this.mView;
    }

    /**
     * Returns the MpushToast's type.
     *
     * @return The type
     */
    @Style.Type
    public int getType() {
        return this.mStyle.type;
    }

    /**
     * Modify various attributes of the MpushToast before being shown.
     */
    @Override
    protected void onPrepareShow() {
        super.onPrepareShow(); // This will take care of many modifications

        final FrameLayout.LayoutParams layoutParams = new FrameLayout
                .LayoutParams(this.mStyle.width, this.mStyle.height);

        // If NOT Lollipop frame, give padding on each side
        if (this.mStyle.frame != Style.FRAME_LOLLIPOP) {
            this.mStyle.width = FrameLayout.LayoutParams.MATCH_PARENT;
            this.mStyle.xOffset = BackgroundUtils.convertToDIP(24);
            this.mStyle.yOffset = BackgroundUtils.convertToDIP(24);
        }

        // On a big screen device, show the MpushToast on the bottom left
        if ((this.mContext.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            this.mStyle.width = BackgroundUtils.convertToDIP(350);
            this.mStyle.gravity = Gravity.BOTTOM | Gravity.START;
        }

        // Set up the Button attributes
        final TextView button = (TextView) this.mView.findViewById(R.id.tv_refuse);
        button.setBackgroundResource(BackgroundUtils
                .getButtonBackgroundResource(this.mStyle.frame));
        button.setText(this.mStyle.buttonText != null ?
                this.mStyle.buttonText.toUpperCase() : "");
        button.setTypeface(button.getTypeface(), this.mStyle.buttonTypefaceStyle);
        button.setTextColor(this.mStyle.buttonTextColor);
        button.setTextSize(this.mStyle.buttonTextSize);

//        if (this.mStyle.frame != Style.FRAME_LOLLIPOP) {
//            this.mView.findViewById(R.id.divider).setBackgroundColor(this
//                    .mStyle.buttonDividerColor);
//
//            // Set an icon resource if desired
//            if (this.mStyle.buttonIconResource > 0) {
//                button.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat
//                                .getDrawable(mContext.getResources(),
//                                        this.mStyle.buttonIconResource,
//                                        mContext.getTheme()),
//                        null, null, null);
//            }
//        }

        if (this.mOnButtonClickListener != null) {
            button.setOnClickListener(new View.OnClickListener() {

                short clicked = 0;

                @Override
                public void onClick(View view) {
                    // Prevent button spamming
                    if (clicked > 0) return;
                    clicked++;

                    mOnButtonClickListener.onClick(view, getButtonToken());
                    MpushToast.this.dismiss();
                }
            });
        }

//        layoutParams.width = this.mStyle.width;
//        layoutParams.height = this.mStyle.height;
        layoutParams.gravity = this.mStyle.gravity;
        layoutParams.bottomMargin = this.mStyle.yOffset;
        layoutParams.topMargin = this.mStyle.yOffset;
        layoutParams.leftMargin = this.mStyle.xOffset;
        layoutParams.rightMargin = this.mStyle.xOffset;

        this.mView.setLayoutParams(layoutParams);

        // Set up touch to dismiss
//        if (this.mStyle.touchToDismiss) {
        mView.setOnTouchListener(new View.OnTouchListener() {

            int timesTouched;

            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                // Prevent repetitive touch events
                if (timesTouched == 0 && motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    dismiss();
                timesTouched++;

                return false; // Do not consume the event in case a Button listener is set
            }
        });
//        } else {
//            // Make sure no listener is set
//            mView.setOnTouchListener(null);
//        }
    }

    /**
     */
    @SuppressWarnings("unchecked")
    public static void onSaveState(Bundle bundle) {
        final ArrayList<Style> styleList = new ArrayList();

        // Create a list of every Style used by a SuperToast in the queue
        for (SuperToast superToast : Toaster.getInstance().getQueue()) {
            if (superToast instanceof MpushToast) {
                superToast.getStyle().isSuperActivityToast = true;
            }
            styleList.add(superToast.getStyle());
        }

        bundle.putParcelableArrayList(BUNDLE_KEY, styleList);

        // Let's avoid any erratic behavior and cancel any showing/pending MpushToasts manually
        Toaster.getInstance().cancelAllSuperToasts();
    }

    /**
     * Restores the state of all SuperToasts that were showing and/or pending.
     * <p>
     * This should be called in the {@link Activity#onCreate(Bundle)}
     * method of your Activity.
     *
     * @param context The Activity Context
     * @param bundle  The Bundle provided in onCreate()
     */
    public static void onRestoreState(Context context, Bundle bundle) {
        // The Bundle will be null sometimes
        if (bundle == null) return;

        // Get the List created in onSaveState()
        final ArrayList<Style> styleList = bundle.getParcelableArrayList(BUNDLE_KEY);

        if (styleList == null) {
            Log.e(MpushToast.class.getName(), "Cannot recreate " +
                    "MpushToasts onRestoreState(). Was onSaveState() called?");
            return;
        }

        // Create a flag that knows if the MpushToast is first in the List or not
        boolean firstInList = true;
        for (Style style : styleList) {
            if (!style.isSuperActivityToast) new SuperToast(context, style).show();
            else {
                // This MpushToast was most likely showing before the orientation change so ignore the show animation
                if (firstInList)
                    new MpushToast(context, style).fromOrientationChange().show();
                else new MpushToast(context, style).show();
            }
            firstInList = false;
        }
    }

    /**
     */
    public static void onRestoreState(Context context, Bundle bundle,
                                      ListenerUtils listenerUtils) {
        if (bundle == null) return; // The Bundle will be null sometimes

        // Get the List created in onSaveState()
        final ArrayList<Style> styleList = bundle.getParcelableArrayList(BUNDLE_KEY);

        if (styleList == null) {
            Log.e(MpushToast.class.getName(), "Cannot recreate MpushToasts onRestoreState(). Was " +
                    "onSaveState() called?");
            return;
        }

        // Create a flag that knows if the MpushToast is first in the List or not
        boolean firstInList = true;
        for (Style style : styleList) {
            if (!style.isSuperActivityToast) new SuperToast(context, style).show();
            else {
                final MpushToast MpushToast = new MpushToast(context, style);
                // This MpushToast was most likely showing before the orientation change so ignore the show animation
                if (firstInList) MpushToast.fromOrientationChange();

                final OnDismissListener onDismissListener = listenerUtils
                        .getOnDismissListenerHashMap().get(style.dismissTag);
                final OnButtonClickListener onButtonClickListener = (OnButtonClickListener) listenerUtils
                        .getOnButtonClickListenerHashMap().get(style.buttonTag);

                // The MpushToast had an OnDismissListener, reattach it
                if (onDismissListener != null) {
                    MpushToast.setOnDismissListener(style.dismissTag,
                            style.dismissToken, onDismissListener);
                }
                // The MpushToast had an OnButtonClickListener, reattach it
                if (onButtonClickListener != null) {
                    MpushToast.setOnButtonClickListener(style.buttonTag,
                            style.buttonToken, onButtonClickListener);
                }
                MpushToast.show();
            }
            firstInList = false;
        }
    }

    /**
     * Creates a simple MpushToast. Don't forget to call {@link SuperToast#show()}.
     *
     * @param context An Activity Context
     * @return The newly created MpushToast
     */
    public static MpushToast create(@NonNull Context context) {
        return (MpushToast) new MpushToast(context);
    }

    /**
     * Creates a simple MpushToast. Don't forget to call {@link SuperToast#show()}.
     *
     * @param context An Activity Context
     * @param style   The desired Style of the MpushToast
     * @return The newly created MpushToast
     */
    public static MpushToast create(@NonNull Context context, @NonNull Style style) {
        return (MpushToast) new MpushToast(context, style);
    }

    /**
     * Creates a simple MpushToast. Don't forget to call {@link SuperToast#show()}.
     *
     * @param context An Activity Context
     * @param type    The desired type of the MpushToast
     * @return The newly created MpushToast
     */
    public static MpushToast create(@NonNull Context context, @Style.Type int type) {
        return (MpushToast) new MpushToast(context, type);
    }

    /**
     * Creates a simple MpushToast. Don't forget to call {@link SuperToast#show()}.
     *
     * @param context An Activity Context
     * @param style   The desired Style of the MpushToast
     * @param type    The desired type of the MpushToast
     * @return The newly created MpushToast
     */
    public static MpushToast create(@NonNull Context context, @NonNull Style style,
                                    @Style.Type int type) {
        return (MpushToast) new MpushToast(context, style, type);
    }

    /**
     * Creates a simple MpushToast. Don't forget to call {@link SuperToast#show()}.
     *
     * @param context   An Activity Context
     * @param style     The desired Style of the MpushToast
     * @param type      The desired type of the MpushToast/
     * @param viewGroup the id of a ViewGroup to add the MpushToast to
     * @return The newly created MpushToast
     */
    public static MpushToast create(@NonNull Context context, @NonNull Style style,
                                    @Style.Type int type, @IdRes int viewGroup) {
        return (MpushToast) new MpushToast(context, style, type, viewGroup);
    }

    /**
     * Creates a simple MpushToast. Don't forget to call {@link SuperToast#show()}.
     *
     * @param context  An Activity Context
     * @param text     The desired text to be shown
     * @param duration The desired duration of the MpushToast
     * @return The newly created MpushToast
     */
    public static MpushToast create(@NonNull Context context, @NonNull String text, @Style.Duration int duration) {
        return (MpushToast) new MpushToast(context)
                .setText(text)
                .setDuration(duration);
    }

    /**
     * Creates a simple MpushToast. Don't forget to call {@link SuperToast#show()}.
     *
     * @param context  An Activity Context
     * @param text     The desired text to be shown
     * @param duration The desired duration of the MpushToast
     * @param style    The desired Style of the SuperToast
     * @return The newly created MpushToast
     */
    public static MpushToast create(@NonNull Context context, @NonNull String text, @Style.Duration int duration,
                                    @NonNull Style style) {
        return (MpushToast) new MpushToast(context, style)
                .setText(text)
                .setDuration(duration);
    }

    /**
     * Creates a simple MpushToast. Don't forget to call {@link SuperToast#show()}.
     *
     * @param context   An Activity Context
     * @param text      The desired text to be shown
     * @param duration  The desired duration of the MpushToast
     * @param style     The desired Style of the SuperToast
     * @param viewGroup The ViewGroup to attach the MpushToast to
     * @return The newly created MpushToast
     */
    public static MpushToast create(@NonNull Context context, @NonNull String text, @Style.Duration int duration,
                                    @NonNull Style style, @IdRes int viewGroup) {
        return (MpushToast) new MpushToast(context, style, Style.TYPE_STANDARD, viewGroup)
                .setText(text)
                .setDuration(duration);
    }
}
