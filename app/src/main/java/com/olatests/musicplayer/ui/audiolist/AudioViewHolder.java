package com.olatests.musicplayer.ui.audiolist;

import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.olatests.musicplayer.R;
import com.olatests.musicplayer.audio.Audio;

/**
 * Created by srikaram on 17-Dec-17.
 */

class AudioViewHolder extends RecyclerView.ViewHolder {

    private ImageView mCoverPic;
    private TextView mTitleView;
    private TextView mArtistView;
    private ImageView mFavoriteView;
    private ImageView mDownloadView;

    AudioViewHolder(View itemView) {
        super(itemView);
        mCoverPic = itemView.findViewById(R.id.audio_cover_pic);
        mTitleView = itemView.findViewById(R.id.audio_title);
        mArtistView = itemView.findViewById(R.id.audio_artists);
        mFavoriteView = itemView.findViewById(R.id.fav_unfav_ib);
        mDownloadView = itemView.findViewById(R.id.download_ib);
    }

    void render(final Audio audio, final AudioListAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition(), audio);
                }
            }
        });
        Ion.with(mCoverPic)
                .error(R.drawable.ic_error_24dp)
                .load(audio.getCoverImage());
        mTitleView.setText(audio.getSong());
        mArtistView.setText(audio.getArtists());
        if (audio.getDownloadPath() == null) {
            mDownloadView.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.ic_file_download_24dp, null));
            mDownloadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onDownloadButtonClicked(getAdapterPosition(), audio);
                    }
                }
            });
        } else {
            mDownloadView.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.ic_done_black_24dp, null));
        }
        @DrawableRes int favIcon;
        if (audio.isFavorite()) {
            favIcon = R.drawable.ic_star_filled_24dp;
        } else {
            favIcon = R.drawable.ic_star_empty_24dp;
        }
        mFavoriteView.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), favIcon, null));
        mFavoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio.setFavorite(!audio.isFavorite());
                @DrawableRes int favIcon;
                if (audio.isFavorite()) {
                    favIcon = R.drawable.ic_star_filled_24dp;
                } else {
                    favIcon = R.drawable.ic_star_empty_24dp;
                }
                mFavoriteView.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), favIcon, null));
                if (listener != null) {
                    listener.onAudioFavorited(getAdapterPosition(), audio);
                }
            }
        });
    }
}
