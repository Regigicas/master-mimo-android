package es.upsa.mimo.gamesviewer.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import es.upsa.mimo.datamodule.models.PlatformModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.HomeActivity
import es.upsa.mimo.gamesviewer.misc.BackFragment
import es.upsa.mimo.gamesviewer.misc.TitleFragment
import es.upsa.mimo.gamesviewer.misc.Util
import es.upsa.mimo.networkmodule.controllers.PlataformaNetworkController
import java.io.Serializable

class PlatformInfoFragment : BackFragment()
{
    companion object
    {
        val bundlePlatformInfoKey = "PlatformInfoFragmentPlatformId";

        @JvmStatic
        fun newInstance(owner: PlatformsFragment, bundle: Bundle?): PlatformInfoFragment
        {
            val nuevoFrag = PlatformInfoFragment();
            nuevoFrag.arguments = bundle;
            nuevoFrag.ownerFragment = owner;
            return nuevoFrag;
        }
    }

    private var platformId: Int? = null;
    private var platformInfo: PlatformModel? = null;
    private val savePlatformIdKey = "PlatformIdKey";
    private val savePlatformInfoKey = "PlatformInfoKey";

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            val savedPlatId = savedInstanceState.getInt(savePlatformIdKey, -1);
            if (savedPlatId >= 0)
                platformId = savedPlatId;
            platformInfo = savedInstanceState.getSerializable(savePlatformInfoKey) as PlatformModel;
            if (activity != null)
                ownerFragment = Util.findFragmentByClassName(PlatformsFragment::class.qualifiedName!!, activity!!.supportFragmentManager); // La vista solo puede ser creada por esta clase
        }
    }

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        if (platformId == null)
            platformId = arguments?.getInt(bundlePlatformInfoKey);
        if (platformId == null)
            throw AssertionError(R.string.assert_needed_data_not_present);

        return inflater.inflate(R.layout.fragment_platform_info, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);

        if (platformInfo != null)
        {
            setupView(view);
            return;
        }

        activity?.let {
            PlataformaNetworkController.getPlataformaInfo(platformId!!, it) { fetchedInfo ->
                platformInfo = fetchedInfo;
                setupView(view);
            };
        };
    }

    private fun setupView(view: View)
    {
        val imagenPlataforma = view.findViewById<ImageView>(R.id.imageViewImgPlataforma);
        val textPlataforma = view.findViewById<TextView>(R.id.textPlatformDescription);
        val button = view.findViewById<Button>(R.id.buttonJuegosPlat);

        val homeActivity = activity as? HomeActivity;
        homeActivity?.supportActionBar?.title = getString(R.string.platform_title_name, platformInfo!!.name);

        Picasso.get().load(platformInfo!!.image_background).fit().centerCrop().into(imagenPlataforma);
        textPlataforma.text = HtmlCompat.fromHtml(platformInfo!!.description!!, HtmlCompat.FROM_HTML_MODE_LEGACY);
        button.setOnClickListener {
            val bundle = Bundle();
            bundle.putSerializable(PlatformGamesFragment.bundlePlatformGamesKey, platformInfo);
            val nextFrag = PlatformGamesFragment.newInstance(this, bundle);
            Util.launchChildFragment(this, nextFrag, activity!!.supportFragmentManager);
        }
    }

    override fun getFragmentTitle(context: Context): String
    {
        return getString(R.string.platform_title_name, platformInfo?.name);
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(savePlatformIdKey, platformId!!);
        outState.putSerializable(savePlatformInfoKey, platformInfo);
    }
}
