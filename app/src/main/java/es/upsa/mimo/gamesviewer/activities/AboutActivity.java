package es.upsa.mimo.gamesviewer.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import es.upsa.mimo.gamesviewer.R;
import es.upsa.mimo.gamesviewer.misc.AppCompatActivityTopBar;
import es.upsa.mimo.gamesviewer.views.ImageTextView;

public class AboutActivity extends AppCompatActivityTopBar
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageTextView upsaLogo = findViewById(R.id.logoUpsaView);
        upsaLogo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.mimo_url))));
            }
        });

        ImageTextView appLogo = findViewById(R.id.logoGamesViewerView);
        appLogo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_source_url))));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item != null)
        {
            if (item.getItemId() == android.R.id.home)
            {
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
