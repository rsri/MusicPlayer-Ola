package com.olatests.musicplayer.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.olatests.musicplayer.MusicPlayerApp;
import com.olatests.musicplayer.R;
import com.olatests.musicplayer.audio.Audio;
import com.olatests.musicplayer.controller.NetworkCoordinator;
import com.olatests.musicplayer.room.AudioLocal;
import com.olatests.musicplayer.ui.audiolist.AudioListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AudioListActivity extends AppCompatActivity implements NetworkCoordinator.OnAudioListFetchListener,
        AudioListAdapter.OnItemClickListener {

    private static final int MAX_COUNT_PER_PAGE = 5;

    private PlayerFragment mPlayerFragment;
    private LinearLayout mPageNumContainer;
    private RecyclerView mAudiosListView;
    private RefreshProgressBar mRefreshProgressBar;

    private AudioListAdapter mAudioListAdapter;
    private int mCurrentPage = 0;
    private int mMaxNumOfPages;

    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mPlayerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.player_fragment);
        mPageNumContainer = findViewById(R.id.page_num_container);
        mAudiosListView = findViewById(R.id.audios_list);
        mRefreshProgressBar = findViewById(R.id.progress);

        mAudioListAdapter = new AudioListAdapter();
        mAudioListAdapter.setOnItemClickListener(this);
        mAudiosListView.setLayoutManager(new LinearLayoutManager(this));
        NetworkCoordinator networkCoordinator = MusicPlayerApp.getApp(this).getNetworkCoordinator();
        networkCoordinator.addOnAudioListFetchListener(this);
        mRefreshProgressBar.show();
        networkCoordinator.loadAudioList();
    }

    @Override
    protected void onDestroy() {
        MusicPlayerApp.getApp(this).getNetworkCoordinator().removeOnAudioListFetchListener(this);
        super.onDestroy();
    }

    @Override
    public void onAudioListFetched(List<Audio> audios) {
        mMaxNumOfPages = audios.size() / MAX_COUNT_PER_PAGE;
        if (audios.size() % MAX_COUNT_PER_PAGE > 0) {
            mMaxNumOfPages++;
        }
        MusicPlayerApp.getApp(this).getDbController().updateAudioWithLocal(audios);
        mAudioListAdapter.setAudios(audios);
        mAudiosListView.setAdapter(mAudioListAdapter);
        mRefreshProgressBar.dismiss();
        if (audios.size() <= MAX_COUNT_PER_PAGE) {
            refreshButtons();
            mAudioListAdapter.showAll();
        } else {
            mCurrentPage = 0;
            refreshButtons();
            mAudioListAdapter.setPage(mCurrentPage, MAX_COUNT_PER_PAGE);
        }
    }

    private void refreshButtons() {
        if (mCurrentPage % mPageNumContainer.getChildCount() == 0 ||
                (mCurrentPage + 1) % mPageNumContainer.getChildCount() == 0) {
            int i;
            if (mCurrentPage % mPageNumContainer.getChildCount() == 0) {
                i = mCurrentPage;
            } else {
                i = mCurrentPage - mPageNumContainer.getChildCount() + 1;
            }
            int count = i + mPageNumContainer.getChildCount();
            for (; i < count; i++) {
                int value = (i % count) + 1;
                Button child = (Button) mPageNumContainer.getChildAt(i % mPageNumContainer.getChildCount());
                if (value > mMaxNumOfPages) {
                    child.setVisibility(View.GONE);
                }
                child.setText(String.valueOf(value));
            }
        }
        for (int i = 0; i < mPageNumContainer.getChildCount(); i++) {
            mPageNumContainer.getChildAt(i).setSelected(false);
        }
        mPageNumContainer.getChildAt(mCurrentPage % mPageNumContainer.getChildCount()).setSelected(true);
        // TODO - Enable/disable prev/next buttons.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAudioListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAudioListAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_refresh) {
            mRefreshProgressBar.show();
            MusicPlayerApp.getApp(this).getNetworkCoordinator().loadAudioList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onAudioFetchFailed(Exception e) {
        e.printStackTrace();
        mRefreshProgressBar.dismiss();
        Toast.makeText(AudioListActivity.this, "Audio fetch failed. Please check internet connection.", Toast.LENGTH_SHORT).show();
        List<AudioLocal> savedAudios = MusicPlayerApp.getApp(this).getDbController().getAllSavedAudios();
        List<Audio> audios = new ArrayList<>();
        for (AudioLocal savedAudio : savedAudios) {
            Audio audio = new Audio(savedAudio);
            audios.add(audio);
        }
        onAudioListFetched(audios);
    }

    public void goPrev(View view) {
        if (mCurrentPage == 0) {
            return;
        }
        mCurrentPage--;
        refreshButtons();
        mAudioListAdapter.setPage(mCurrentPage, MAX_COUNT_PER_PAGE);
    }

    public void goNext(View view) {
        if (mCurrentPage == mMaxNumOfPages - 1) {
            return;
        }
        mCurrentPage++;
        refreshButtons();
        mAudioListAdapter.setPage(mCurrentPage, MAX_COUNT_PER_PAGE);
    }

    public void goToPage(View view) {
        String text = ((Button) view).getText().toString();
        mCurrentPage = Integer.valueOf(text) - 1;
        refreshButtons();
    }

    @Override
    public void onItemClick(int position, Audio audio) {
        mPlayerFragment.playAudio(audio);
    }

    @Override
    public void onDownloadButtonClicked(final int position, final Audio audio) {
        mRefreshProgressBar.show();
        MusicPlayerApp.getApp(this).getNetworkCoordinator().downloadAudio(audio, new NetworkCoordinator.OnDownloadListener() {
            @Override
            public void onDownloadSuccessful() {
                AudioLocal audioLocal = new AudioLocal(audio);
                MusicPlayerApp.getApp(AudioListActivity.this).getDbController().insertAll(audioLocal);
                mRefreshProgressBar.dismiss();
                mAudioListAdapter.notifyItemChanged(position);
                Toast.makeText(AudioListActivity.this, "Download succeeded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadFailed() {
                Toast.makeText(AudioListActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAudioFavorited(int position, Audio audio) {
        AudioLocal audioLocal = new AudioLocal(audio);
        MusicPlayerApp.getApp(this).getDbController().insertAll(audioLocal);
    }
}
