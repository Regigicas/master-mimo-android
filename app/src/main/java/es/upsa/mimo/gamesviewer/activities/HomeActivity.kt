package es.upsa.mimo.gamesviewer.activities

import android.os.Bundle
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
import kotlin.reflect.KClass


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
            homeFragment = supportFragmentManager.getFragment(savedInstanceState, HomeFragment::javaClass.name) as HomeFragment;
            platformsFragment = supportFragmentManager.getFragment(savedInstanceState, PlatformsFragment::javaClass.name) as PlatformsFragment;
            searchFragment = supportFragmentManager.getFragment(savedInstanceState, SearchFragment::javaClass.name) as SearchFragment;
            favoriteFragment = supportFragmentManager.getFragment(savedInstanceState, FavoriteFragment::javaClass.name) as FavoriteFragment;
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
            val showFragment = when (selected)
            {
                R.id.optionPlatforms -> platformsFragment;
                R.id.optionSearch -> searchFragment;
                R.id.optionFavorites -> favoriteFragment;
                else -> homeFragment;
            }

            activeFragment = showFragment;
            bottomBar.selectedItemId = selected;
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
        if (isMenuFragment)
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
        outState.putInt(saveMenuIdKey, bottomBar.selectedItemId);

        if (homeFragment.isAdded())
            supportFragmentManager.putFragment(outState, HomeFragment::javaClass.name, homeFragment);

        if (platformsFragment.isAdded())
            supportFragmentManager.putFragment(outState, PlatformsFragment::javaClass.name, platformsFragment);

        if (searchFragment.isAdded())
            supportFragmentManager.putFragment(outState, SearchFragment::javaClass.name, searchFragment);

        if (favoriteFragment.isAdded())
            supportFragmentManager.putFragment(outState, FavoriteFragment::javaClass.name, favoriteFragment);

        for (oldFragment in supportFragmentManager.fragments)
            if (oldFragment is BackFragment)
                supportFragmentManager.putFragment(outState, oldFragment::javaClass.name, oldFragment);

        outState.putBoolean(saveInitialSetupKey, initialSetup);
    }
}
