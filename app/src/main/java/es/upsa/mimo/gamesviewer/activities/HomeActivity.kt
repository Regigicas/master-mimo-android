package es.upsa.mimo.gamesviewer.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.fragments.*


class HomeActivity : AppCompatActivity()
{
    val homeFragment: HomeFragment = HomeFragment();
    var platformsFragment: PlatformsFragment? = null;
    var searchFragment: SearchFragment? = null;
    var favoriteFragment: FavoriteFragment? = null;
    var activeFragment: Fragment? = null;
    private val saveMenuIdKey = "HomeSelectedMenuId";
    private var bottomBar: BottomNavigationView? = null;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomBar = findViewById(R.id.bottomBar);

        if (bottomBar == null)
            throw AssertionError(getString(R.string.assert_view_not_created));

        bottomBar!!.setOnNavigationItemSelectedListener {
            when (it.itemId)
            {
                R.id.optionHome -> openFragment(homeFragment);
                R.id.optionPlatforms ->
                {
                    if (platformsFragment == null)
                        platformsFragment = PlatformsFragment();
                    openFragment(platformsFragment!!);
                }
                R.id.optionSearch ->
                {
                    if (searchFragment == null)
                        searchFragment = SearchFragment();
                    openFragment(searchFragment!!);
                }
                R.id.optionFavorites ->
                {
                    if (favoriteFragment == null)
                        favoriteFragment = FavoriteFragment();
                    openFragment(favoriteFragment!!);
                }
                else -> throw AssertionError(getString(R.string.assert_invalid_bottombar_item));
            }

            true
        }

        bottomBar!!.selectedItemId = R.id.optionHome;
    }

    private fun openFragment(fragment: Fragment)
    {
        if (fragment == activeFragment)
            return;

        activeFragment = fragment;
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentFrame, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit();
    }

    override fun onBackPressed()
    {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentFrame)
        if (fragment is MenuFragment)
            moveTaskToBack(false);
        else
            super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?)
    {
        super.onSaveInstanceState(outState, outPersistentState);
        outState?.let { saveState ->
            bottomBar?.let {
                saveState.putInt(saveMenuIdKey, it.selectedItemId);
            };
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?)
    {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState?.let { saveState ->
            bottomBar?.let {
                val selected = saveState.getInt(saveMenuIdKey);
                it.selectedItemId = selected;
            };
        }
    }
}
