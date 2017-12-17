package com.olatests.musicplayer.ui.audiolist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.olatests.musicplayer.R;
import com.olatests.musicplayer.audio.Audio;
import com.olatests.musicplayer.audio.IAudio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikaram on 17-Dec-17.
 */

public class AudioListAdapter extends RecyclerView.Adapter<AudioViewHolder> implements Filterable {

    private List<Audio> mAllAudios;
    private List<Audio> mAudiosToShow;

    private List<Audio> mAudiosPreSearch;

    private OnItemClickListener listener;

    public void setAudios(List<Audio> audios) {
        this.mAllAudios = audios;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void showAll() {
        mAudiosToShow = mAllAudios;
        notifyDataSetChanged();
    }

    public void setPage(int page, int maxPerPage) {
        mAudiosToShow = mAllAudios.subList((page * maxPerPage),
                Math.min((page * maxPerPage) + maxPerPage, mAllAudios.size()));
        notifyDataSetChanged();
    }

    @Override
    public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AudioViewHolder(inflater.inflate(R.layout.audio_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(AudioViewHolder holder, int position) {
        Audio audio = mAudiosToShow.get(position);
        holder.render(audio, listener);
    }

    @Override
    public int getItemCount() {
        return mAudiosToShow.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String constraintStr = constraint.toString();
                List<Audio> audios = new ArrayList<>();
                if (!constraintStr.isEmpty()) {
                    for (Audio audio : mAllAudios) {
                        if (audio.getSong().startsWith(constraintStr)) {
                            audios.add(audio);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = audios;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<Audio> searchResults = (List<Audio>) results.values;
                if (!searchResults.isEmpty()) {
                    if (mAudiosPreSearch == null) {
                        mAudiosPreSearch = mAudiosToShow;
                    }
                    mAudiosToShow = searchResults;
                } else if (mAudiosPreSearch != null) {
                    mAudiosToShow = mAudiosPreSearch;
                    mAudiosPreSearch = null;
                }
                notifyDataSetChanged();
            }
        };
    }


    public interface OnItemClickListener {
        void onItemClick(int position, Audio audio);
        void onDownloadButtonClicked(int position, Audio audio);
        void onAudioFavorited(int position, Audio audio);
    }
}
