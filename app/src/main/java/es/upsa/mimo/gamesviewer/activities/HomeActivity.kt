package es.upsa.mimo.gamesviewer.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.fragments.*
import es.upsa.mimo.gamesviewer.misc.BackFragment
import es.upsa.mimo.gamesviewer.misc.MenuFragment


class HomeActivity : AppCompatActivity()
{
    val homeFragment: HomeFragment = HomeFragment();
    var platformsFragment: PlatformsFragment? = null;
    var searchFragment: SearchFragment? = null;
    var favoriteFragment: FavoriteFragment? = null;
    var activeFragment: Fragment = homeFragment;
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
                R.id.optionHome -> openFragment(homeFragment, false);
                R.id.optionPlatforms ->
                {
                    if (platformsFragment == null)
                    {
                        platformsFragment = PlatformsFragment();
                        openFragment(platformsFragment!!, true);
                    }
                    else
                        openFragment(platformsFragment!!, false);
                }
                R.id.optionSearch ->
                {
                    if (searchFragment == null)
                    {
                        searchFragment = SearchFragment();
                        openFragment(searchFragment!!, true);
                    }
                    else
                        openFragment(searchFragment!!, false);
                }
                R.id.optionFavorites ->
                {
                    if (favoriteFragment == null)
                    {
                        favoriteFragment = FavoriteFragment();
                        openFragment(favoriteFragment!!, true);
                    }
                    else
                        openFragment(favoriteFragment!!, false);
                }
                else -> throw AssertionError(getString(R.string.assert_invalid_bottombar_item));
            }

            true
        }

        // Ponemos en home la primera
        bottomBar!!.selectedItemId = R.id.optionHome;
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentFrame, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit();
    }

    private fun openFragment(fragment: Fragment, initial: Boolean)
    {
        if (fragment == activeFragment)
            return;

        if (initial)
        {
            getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .add(R.id.fragmentFrame, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        }
        else
        {
            getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        }

        activeFragment = fragment;
    }

    override fun onBackPressed()
    {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentFrame)
        if (fragment is BackFragment)
            fragment.onFragmentBack();
        else if (fragment is MenuFragment)
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
