package es.upsa.mimo.gamesviewer.activities

import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.fragments.FavoriteFragment
import es.upsa.mimo.gamesviewer.fragments.HomeFragment
import es.upsa.mimo.gamesviewer.fragments.PlatformsFragment
import es.upsa.mimo.gamesviewer.fragments.SearchFragment
import es.upsa.mimo.gamesviewer.misc.AppCompatActivityTopBar
import es.upsa.mimo.gamesviewer.fragments.BackFragment
import es.upsa.mimo.gamesviewer.fragments.MenuFragment

class HomeActivity : AppCompatActivityTopBar()
{
    private lateinit var homeFragment: HomeFragment;
    private lateinit var platformsFragment: PlatformsFragment;
    private lateinit var searchFragment: SearchFragment;
    private lateinit var favoriteFragment: FavoriteFragment;
    private lateinit var activeFragment: Fragment;
    private val saveMenuIdKey = "HomeSelectedMenuId";
    private val saveInitialSetupKey = "HomeInitialSetupDone";
    private lateinit var bottomBar: BottomNavigationView;
    private var initialSetup = true;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomBar = findViewById(R.id.bottomBar);

        var selected = R.id.optionHome;
        if (savedInstanceState != null)
        {
            supportFragmentManager.getFragment(savedInstanceState, HomeFragment::class.java.name)?.let {
                homeFragment = it as HomeFragment;
            }
            supportFragmentManager.getFragment(savedInstanceState, PlatformsFragment::class.java.name)?.let {
                platformsFragment = it as PlatformsFragment;
            }
            supportFragmentManager.getFragment(savedInstanceState, SearchFragment::class.java.name)?.let {
                searchFragment = it as SearchFragment;
            }
            supportFragmentManager.getFragment(savedInstanceState, FavoriteFragment::class.java.name)?.let {
                favoriteFragment = it as FavoriteFragment;
            }
            selected = savedInstanceState.getInt(saveMenuIdKey);
            initialSetup = savedInstanceState.getBoolean(saveInitialSetupKey);
        }

        if (!this::homeFragment.isInitialized)
            homeFragment = HomeFragment();
        if (!this::platformsFragment.isInitialized)
            platformsFragment = PlatformsFragment();
        if (!this::searchFragment.isInitialized)
            searchFragment = SearchFragment();
        if (!this::favoriteFragment.isInitialized)
            favoriteFragment = FavoriteFragment();

        bottomBar.setOnNavigationItemSelectedListener {
            when (it.itemId)
            {
                R.id.optionHome -> openFragment(homeFragment, false);
                R.id.optionPlatforms ->
                {
                    openFragment(platformsFragment, !platformsFragment.isAdded());
                }
                R.id.optionSearch ->
                {
                    openFragment(searchFragment, !searchFragment.isAdded());
                }
                R.id.optionFavorites ->
                {
                    openFragment(favoriteFragment, !favoriteFragment.isAdded());
                }
                else -> throw AssertionError(getString(R.string.assert_invalid_bottombar_item));
            }

            true
        }

        // Ponemos en home la primera o la guardada
        if (initialSetup)
        {
            initialSetup = false;
            activeFragment = homeFragment;
            bottomBar.selectedItemId = selected;
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentFrame, homeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        }
        else
        {
            val showFragment: MenuFragment = when (selected)
            {
                R.id.optionPlatforms -> platformsFragment;
                R.id.optionSearch -> searchFragment;
                R.id.optionFavorites -> favoriteFragment;
                else -> homeFragment;
            }

            activeFragment = showFragment;
            bottomBar.selectedItemId = selected;
            supportActionBar?.title = showFragment.getFragmentTitle(this);
        }
    }

    private fun openFragment(fragment: MenuFragment, initial: Boolean)
    {
        if (fragment == activeFragment)
            return;

        val visibleFragment = supportFragmentManager.findFragmentById(R.id.fragmentFrame);
        val isMenuFragment = visibleFragment is MenuFragment;
        val fragmentToRemove = if (!isMenuFragment && visibleFragment != null) visibleFragment else activeFragment;

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
            ft.hide(activeFragment);
            if (initial)
                ft.add(R.id.fragmentFrame, fragment);
            else
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
        else if (fragment is FavoriteFragment)
            fragment.fragmentBackPressed();
        else if (fragment is MenuFragment)
            moveTaskToBack(false);
        else
            super.onBackPressed();
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(saveMenuIdKey, bottomBar.selectedItemId);

        if (homeFragment.isAdded())
            supportFragmentManager.putFragment(outState, HomeFragment::class.java.name, homeFragment);

        if (platformsFragment.isAdded())
            supportFragmentManager.putFragment(outState, PlatformsFragment::class.java.name, platformsFragment);

        if (searchFragment.isAdded())
            supportFragmentManager.putFragment(outState, SearchFragment::class.java.name, searchFragment);

        if (favoriteFragment.isAdded())
            supportFragmentManager.putFragment(outState, FavoriteFragment::class.java.name, favoriteFragment);

        for (oldFragment in supportFragmentManager.fragments)
            if (oldFragment is BackFragment)
                supportFragmentManager.putFragment(outState, oldFragment::class.java.name, oldFragment);

        outState.putBoolean(saveInitialSetupKey, initialSetup);
    }
}
