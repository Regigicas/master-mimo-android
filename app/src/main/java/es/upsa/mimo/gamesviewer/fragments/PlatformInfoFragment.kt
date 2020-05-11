package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.HomeActivity
import es.upsa.mimo.gamesviewer.misc.BackFragment
import es.upsa.mimo.gamesviewer.misc.Util
import es.upsa.mimo.networkmodule.controllers.PlataformaNetworkController

class PlatformInfoFragment(ownerFragment: Fragment) : BackFragment(ownerFragment)
{
    companion object
    {
        val bundlePlatformInfoKey = "PlatformInfoFragmentPlatformId";
    }

    private var platformId: Int? = null;

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        platformId = arguments?.getInt(bundlePlatformInfoKey);
        if (platformId == null)
            throw AssertionError(R.string.assert_needed_data_not_present);

        return inflater.inflate(R.layout.fragment_platform_info, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);

        activity?.let {
            PlataformaNetworkController.getPlataformaInfo(platformId!!, it) {
                val homeActivity = activity as? HomeActivity;
                homeActivity?.supportActionBar?.title = getString(R.string.platform_title_name, it.name);

                val imagenPlataforma = view.findViewById<ImageView>(R.id.imageViewImgPlataforma);
                val textPlataforma = view.findViewById<TextView>(R.id.textPlatformDescription);
                val button = view.findViewById<Button>(R.id.buttonJuegosPlat);

                Picasso.get().load(it.image_background).fit().centerCrop().into(imagenPlataforma);
                textPlataforma.text = HtmlCompat.fromHtml(it.description!!, HtmlCompat.FROM_HTML_MODE_LEGACY);
                button.setOnClickListener {
                    val nextFrag = PlatformGamesFragment(this);
                    val bundle = Bundle();
                    bundle.putInt(PlatformGamesFragment.bundlePlatformGamesKey, platformId!!);
                    nextFrag.arguments = bundle;
                    Util.launchChildFragment(this, nextFrag, activity!!.supportFragmentManager);
                }
            };
        };
    }
}
