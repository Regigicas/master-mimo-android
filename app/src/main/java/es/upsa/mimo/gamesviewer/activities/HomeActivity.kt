package es.upsa.mimo.gamesviewer.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.fragments.FavoriteFragment
import es.upsa.mimo.gamesviewer.fragments.HomeFragment
import es.upsa.mimo.gamesviewer.fragments.PlatformsFragment
import es.upsa.mimo.gamesviewer.fragments.SearchFragment
import es.upsa.mimo.gamesviewer.misc.AppCompatActivityTopBar
import es.upsa.mimo.gamesviewer.misc.BackFragment
import es.upsa.mimo.gamesviewer.misc.MenuFragment


class HomeActivity : AppCompatActivityTopBar()
{
    private var homeFragment: HomeFragment? = null;
    private var platformsFragment: PlatformsFragment? = null;
    private var searchFragment: SearchFragment? = null;
    private var favoriteFragment: FavoriteFragment? = null;
    private var activeFragment: Fragment? = null;
    private val saveMenuIdKey = "HomeSelectedMenuId";
    private val saveInitialSetupKey = "HomeInitialSetupDone";
    private var bottomBar: BottomNavigationView? = null;
    private var initialSetup = true;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomBar = findViewById(R.id.bottomBar);

        if (bottomBar == null)
            throw AssertionError(getString(R.string.assert_view_not_created));

        var selected = R.id.optionHome;
        if (savedInstanceState != null)
        {
            homeFragment = supportFragmentManager.getFragment(savedInstanceState, HomeFragment::class.qualifiedName!!) as HomeFragment?;
            platformsFragment = supportFragmentManager.getFragment(savedInstanceState, PlatformsFragment::class.qualifiedName!!) as PlatformsFragment?;
            searchFragment = supportFragmentManager.getFragment(savedInstanceState, SearchFragment::class.qualifiedName!!) as SearchFragment?;
            favoriteFragment = supportFragmentManager.getFragment(savedInstanceState, FavoriteFragment::class.qualifiedName!!) as FavoriteFragment?;
            selected = savedInstanceState.getInt(saveMenuIdKey);
            initialSetup = savedInstanceState.getBoolean(saveInitialSetupKey);
        }

        if (homeFragment == null)
            homeFragment = HomeFragment();

        bottomBar!!.setOnNavigationItemSelectedListener {
            when (it.itemId)
            {
                R.id.optionHome -> openFragment(homeFragment!!, false);
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

        val showFragment = when (selected)
        {
            R.id.optionPlatforms -> platformsFragment;
            R.id.optionSearch -> searchFragment;
            R.id.optionFavorites -> favoriteFragment;
            else -> homeFragment;
        }

        // Ponemos en home la primera o la guardada
        if (initialSetup)
        {
            initialSetup = false;
            activeFragment = homeFragment;
            bottomBar!!.selectedItemId = selected;
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentFrame, showFragment as Fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        }
        else
        {
            activeFragment = showFragment;
            bottomBar!!.selectedItemId = selected;
        }
    }

    private fun openFragment(fragment: MenuFragment, initial: Boolean)
    {
        if (fragment == activeFragment)
            return;

        val visibleFragment = supportFragmentManager.findFragmentById(R.id.fragmentFrame);
        val isMenuFragment = visibleFragment is MenuFragment;
        val fragmentToRemove = if (!isMenuFragment && visibleFragment != null) visibleFragment else activeFragment!!;

        if (initial && isMenuFragment)
        {
            supportFragmentManager
                .beginTransaction()
                .hide(fragmentToRemove)
                .add(R.id.fragmentFrame, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        }
        else if (isMenuFragment)
        {
            supportFragmentManager
                .beginTransaction()
                .hide(fragmentToRemove)
                .show(fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        }
        else
        {
            val ft = supportFragmentManager.beginTransaction();
            for (oldFragment in supportFragmentManager.fragments)
                if (oldFragment is BackFragment)
                    ft.remove(oldFragment);
            ft.hide(activeFragment!!);
            ft.show(fragment);
            ft.commit();
        }

        supportActionBar?.title = fragment.getFragmentTitle(this);

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

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState);
        bottomBar?.let {
            outState.putInt(saveMenuIdKey, it.selectedItemId);
        };

        homeFragment?.let {
            supportFragmentManager.putFragment(outState, HomeFragment::class.qualifiedName!!, it);
        };

        platformsFragment?.let {
            supportFragmentManager.putFragment(outState, PlatformsFragment::class.qualifiedName!!, it);
        };

        searchFragment?.let {
            supportFragmentManager.putFragment(outState, SearchFragment::class.qualifiedName!!, it);
        };

        favoriteFragment?.let {
            supportFragmentManager.putFragment(outState, FavoriteFragment::class.qualifiedName!!, it);
        };

        for (oldFragment in supportFragmentManager.fragments)
            if (oldFragment is BackFragment)
                supportFragmentManager.putFragment(outState, oldFragment::class.qualifiedName!!, oldFragment);

        outState.putBoolean(saveInitialSetupKey, initialSetup);
    }
}
