package es.upsa.mimo.gamesviewer.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import es.upsa.mimo.gamesviewer.R

class ClipService : Service()
{
    companion object
    {
        val clipServiceURL = "clipServiceURL";

        fun newIntent(url: String, context: Context): Intent
        {
            val intent = Intent(context, ClipService::class.java);
            intent.putExtra(clipServiceURL, url);
            return intent;
        }
    }

    private lateinit var exoPlayer: SimpleExoPlayer;

    override fun onCreate()
    {
        super.onCreate();
        exoPlayer = SimpleExoPlayer.Builder(this).build();
    }

    override fun onBind(p0: Intent): IBinder
    {
        exoPlayer.playWhenReady = true;
        val url = p0.extras?.getString(clipServiceURL);
        if (url != null)
            initializePlayer(url);
        return ClipServiceBinder();
    }

    inner class ClipServiceBinder : Binder()
    {
        fun getExoPlayer() = exoPlayer;
    }

    private fun initializePlayer(url: String)
    {
        val userAgent = Util.getUserAgent(this, getString(R.string.app_name));
        val mediaSource = ProgressiveMediaSource
            .Factory(DefaultDataSourceFactory(this, userAgent), DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(url));

        exoPlayer.prepare(mediaSource, true, false);
        exoPlayer.playWhenReady = true;
    }
}
