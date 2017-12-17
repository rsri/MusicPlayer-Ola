package com.olatests.musicplayer.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.olatests.musicplayer.R;

/**
 * A custom horizontal indeterminate progress bar which displays a smooth colored animation.
 */
public class RefreshProgressBar extends View {

    private static final float PROGRESS_BAR_HEIGHT = 4f;

    private static final int DURATION_APPEAR_ANIMATION = 150;
    private static final int DURATION_DISAPPEAR_ANIMATION = 400;

    private final Handler handler;
    private boolean mIsRefreshing = false;
    private SwipeProgressBar mProgressBar;

    /**
     * Current view height.
     */
    private int progressBarHeight;

    /**
     * Maximum height.
     */
    private final int maxHeight;

    public RefreshProgressBar(Context context) {
        this(context, null, 0);
    }

    public RefreshProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        handler = new Handler();
        if (!isInEditMode()) {
            mProgressBar = new SwipeProgressBar(this);
        }
        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        maxHeight = (int) (metrics.density * PROGRESS_BAR_HEIGHT + 0.5f);

        // Keep view hidden if not refreshing.
        progressBarHeight = 0;

        setColorSchemeResources(
                R.color.progress_1,
                R.color.progress_2,
                R.color.progress_3,
                R.color.progress_4
        );
    }

    /**
     * Starts or tops the refresh animation. Animation is stopped by default. After the stop
     * animation has completed, the progress bar will be fully transparent.
     */
    private void setRefreshing(boolean refreshing) {
        if (mIsRefreshing != refreshing) {
            mIsRefreshing = refreshing;
            handler.removeCallbacks(mRefreshUpdateRunnable);
            handler.post(mRefreshUpdateRunnable);
        }
    }

    public void show() {
        setRefreshing(true);
    }

    public void dismiss() {
        setRefreshing(false);
    }

    private final Runnable mRefreshUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsRefreshing) {
                mProgressBar.start();
                setVisible(true);
            } else {
                mProgressBar.stop();
                setVisible(false);
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacks(mRefreshUpdateRunnable);
        super.onDetachedFromWindow();
    }

    /**
     * Set the four colors used in the progress animation from color resources.
     */
    @SuppressWarnings("WeakerAccess")
    public void setColorSchemeResources(int colorRes1, int colorRes2, int colorRes3, int
            colorRes4) {
        setColorSchemeColors(ContextCompat.getColor(getContext(), colorRes1),
                ContextCompat.getColor(getContext(), colorRes2),
                ContextCompat.getColor(getContext(), colorRes3),
                ContextCompat.getColor(getContext(), colorRes4));
    }

    /**
     * Set the four colors used in the progress animation.
     */
    @SuppressWarnings("WeakerAccess")
    public void setColorSchemeColors(int color1, int color2, int color3, int color4) {
        mProgressBar.setColorScheme(color1, color2, color3, color4);
    }

    /**
     * @return Whether the progress bar is actively showing refresh progress.
     */
    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        if (mProgressBar != null) {
            mProgressBar.draw(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mProgressBar != null) {
            mProgressBar.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                progressBarHeight);
    }

    /**
     * Property setter to be used with {@code ObjectAnimator}.
     */
    public void setProgressBarHeight(int progressBarHeight) {
        this.progressBarHeight = progressBarHeight;
        requestLayout();
    }

    /**
     * Property getter to be used with {@code ObjectAnimator}.
     */
    public int getProgressBarHeight() {
        return progressBarHeight;
    }

    private void setVisible(boolean visible) {
        if (visible) {
            ObjectAnimator.ofInt(RefreshProgressBar.this, "progressBarHeight", 0, maxHeight)
                    .setDuration(DURATION_APPEAR_ANIMATION)
                    .start();
        } else {
            ObjectAnimator.ofInt(RefreshProgressBar.this, "progressBarHeight", maxHeight, 0)
                    .setDuration(DURATION_DISAPPEAR_ANIMATION)
                    .start();
        }

    }
}
