/*
 * Copyright (C) 2024 Halcyon Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.halcyon.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.batteryusage.PowerUsageAdvanced;
import com.android.settings.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Progress bar preference with a usage summary and a total summary.
 *
 * <p>This preference shows number in usage summary with enlarged font size.
 */
public class BatteryUsageProgressBarPreference extends Preference {

    private final Pattern mNumberPattern = Pattern.compile("[\\d]*[\\٫.,]?[\\d]+");

    private CharSequence mUsageSummary;
    private CharSequence mBottomSummary;
    private CharSequence mBottomSummaryContentDescription;
    private ImageView mCustomImageView;
    private int mPercent = -1;

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The {@link Context} this is associated with, through which it can access the
     *     current theme, resources, {@link SharedPreferences}, etc.
     * @param attrs The attributes of the XML tag that is inflating the preference
     * @param defStyle An attribute in the current theme that contains a reference to a style
     *     resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    public BatteryUsageProgressBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.battery_preference_usage_progress_bar);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The {@link Context} this is associated with, through which it can access the
     *     current theme, resources, {@link SharedPreferences}, etc.
     * @param attrs The attributes of the XML tag that is inflating the preference
     */
    public BatteryUsageProgressBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.battery_preference_usage_progress_bar);
    }

    /**
     * Constructor to create a preference.
     *
     * @param context The Context this is associated with.
     */
    public BatteryUsageProgressBarPreference(Context context) {
        this(context, null);
    }

    /** Set usage summary, number in the summary will show with enlarged font size. */
    public void setUsageSummary(CharSequence usageSummary) {
        if (TextUtils.equals(mUsageSummary, usageSummary)) {
            return;
        }
        mUsageSummary = usageSummary;
        notifyChanged();
    }

    /** Set bottom summary. */
    public void setBottomSummary(CharSequence bottomSummary) {
        if (TextUtils.equals(mBottomSummary, bottomSummary)) {
            return;
        }
        mBottomSummary = bottomSummary;
        notifyChanged();
    }

    /** Set content description for the bottom summary. */
    public void setBottomSummaryContentDescription(CharSequence contentDescription) {
        if (!TextUtils.equals(mBottomSummaryContentDescription, contentDescription)) {
            mBottomSummaryContentDescription = contentDescription;
            notifyChanged();
        }
    }

    /** Set percentage of the progress bar. */
    public void setPercent(long usage, long total) {
        if (usage > total) {
            return;
        }
        if (total == 0L) {
            if (mPercent != 0) {
                mPercent = 0;
                notifyChanged();
            }
            return;
        }
        final int percent = (int) (usage / (double) total * 100);
        if (mPercent == percent) {
            return;
        }
        mPercent = percent;
        notifyChanged();
    }

    /** Set custom ImageView to the right side of total summary. */
    public <T extends ImageView> void setCustomContent(T imageView) {
        if (imageView == mCustomImageView) {
            return;
        }
        mCustomImageView = imageView;
        notifyChanged();
    }

    /**
     * Binds the created View to the data for this preference.
     *
     * <p>This is a good place to grab references to custom Views in the layout and set properties
     * on them.
     *
     * <p>Make sure to call through to the superclass's implementation.
     *
     * @param holder The ViewHolder that provides references to the views to fill in. These views
     *     will be recycled, so you should not hold a reference to them after this method returns.
     */
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        holder.setDividerAllowedAbove(false);
        holder.setDividerAllowedBelow(false);

        final TextView usageSummary = (TextView) holder.findViewById(R.id.usage_summary);
        if (usageSummary != null && mUsageSummary != null) {
            usageSummary.setText(enlargeFontOfNumber(mUsageSummary));
        }

        final TextView bottomSummary = (TextView) holder.findViewById(R.id.bottom_summary);
        if (bottomSummary != null) {
            if (TextUtils.isEmpty(mBottomSummary)) {
                bottomSummary.setVisibility(View.GONE);
            } else {
                bottomSummary.setVisibility(View.VISIBLE);
                bottomSummary.setMovementMethod(LinkMovementMethod.getInstance());
                bottomSummary.setText(mBottomSummary);
                if (!TextUtils.isEmpty(mBottomSummaryContentDescription)) {
                    bottomSummary.setContentDescription(mBottomSummaryContentDescription);
                }
            }
        }

        final ProgressBar progressBar = (ProgressBar) holder.findViewById(android.R.id.progress);
        if (progressBar != null) {
            if (mPercent < 0) {
                progressBar.setIndeterminate(true);
            } else {
                progressBar.setIndeterminate(false);
                animateBatteryLevel(progressBar, 0, mPercent);
            }
        }

        final TextView viewDetails = (TextView) holder.findViewById(R.id.view_details);
        if (viewDetails != null) {
            viewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SubSettingLauncher(getContext())
                        .setDestination(PowerUsageAdvanced.class.getName())
                        .setSourceMetricsCategory(MetricsProto.MetricsEvent.HALCYON)
                        .setTitleRes(R.string.advanced_battery_title)
                        .launch();
                }
            });
        }
    }


    private CharSequence enlargeFontOfNumber(CharSequence summary) {
        if (TextUtils.isEmpty(summary)) {
            return "";
        }

        final Matcher matcher = mNumberPattern.matcher(summary);
        if (matcher.find()) {
            final SpannableString spannableSummary = new SpannableString(summary);
            spannableSummary.setSpan(
                    new AbsoluteSizeSpan(64, true /* dip */),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableSummary;
        }
        return summary;
    }
    
    private void animateBatteryLevel(final ProgressBar progressbar, final int startValue, final int endValue) {
        final ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                progressbar.setProgress(animatedValue);
            }
        });
        animator.start();
    }
}
