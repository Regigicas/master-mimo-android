package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import es.upsa.mimo.datamodule.models.PlatformModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.HomeActivity
import es.upsa.mimo.gamesviewer.misc.BackFragment
import es.upsa.mimo.gamesviewer.misc.RVBackButtonClickListener

class PlatformInfoFragment(private val ownerFragment: RVBackButtonClickListener) : BackFragment(ownerFragment)
{
    companion object
    {
        val bundlePlatformInfoKey = "PlatformInfoFragmentPlatformObject";
    }

    private var platformInfo: PlatformModel? = null;

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        platformInfo = arguments?.getSerializable(bundlePlatformInfoKey) as PlatformModel?;
        val homeActivity = activity as? HomeActivity;
        homeActivity?.supportActionBar?.title = getString(R.string.platform_title_name, platformInfo?.name);
        return inflater.inflate(R.layout.fragment_platform_info, container, false);
    }
}
