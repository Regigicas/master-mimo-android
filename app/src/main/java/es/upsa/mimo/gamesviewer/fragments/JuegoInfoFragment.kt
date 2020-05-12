package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.HomeActivity
import es.upsa.mimo.gamesviewer.misc.BackFragment
import es.upsa.mimo.gamesviewer.misc.Util
import es.upsa.mimo.networkmodule.controllers.JuegoNetworkController

class JuegoInfoFragment : BackFragment()
{
    companion object
    {
        val bundleJuegoInfoKey = "JuegoInfoFragmentJuegoId";

        @JvmStatic
        fun newInstance(owner: Fragment, bundle: Bundle?): JuegoInfoFragment
        {
            val nuevoFrag = JuegoInfoFragment();
            nuevoFrag.arguments = bundle;
            nuevoFrag.ownerFragment = owner;
            return nuevoFrag;
        }
    }

    private var juegoId: Int? = null;
    private var juegoInfo: JuegoModel? = null;

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        juegoId = arguments?.getInt(bundleJuegoInfoKey);
        if (juegoId == null)
            throw AssertionError(R.string.assert_needed_data_not_present);

        return inflater.inflate(R.layout.fragment_juego_info, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);

        activity?.let {
            JuegoNetworkController.getJuegoInfo(juegoId!!, it) { info ->
                juegoInfo = info;
                val homeActivity = activity as? HomeActivity;
                homeActivity?.supportActionBar?.title = juegoInfo!!.name;

                val imagenJuego = view.findViewById<ImageView>(R.id.imageViewJuego);
                val textoPlataformas = view.findViewById<TextView>(R.id.textViewPlatforms);
                val textoValoracion = view.findViewById<TextView>(R.id.textViewRatingValue);
                val textoFechaSalida = view.findViewById<TextView>(R.id.textViewReleaseValue);
                val textoDescripcion = view.findViewById<TextView>(R.id.textViewDescriptionValue);
                val buttonQR = view.findViewById<ImageButton>(R.id.imageButtonQRCode);

                Picasso.get().load(juegoInfo!!.background_image).fit().centerCrop().into(imagenJuego);
                textoDescripcion.text = HtmlCompat.fromHtml(juegoInfo!!.description!!, HtmlCompat.FROM_HTML_MODE_LEGACY);
                textoPlataformas.text = juegoInfo!!.getPlatformString();
                textoValoracion.text = juegoInfo!!.rating.toString();
                textoFechaSalida.text = juegoInfo!!.released;

                buttonQR.setOnClickListener {
                    val bundle = Bundle();
                    bundle.putSerializable(QRCodeFragment.bundleJuegoInfoKey, juegoInfo);
                    val nextFrag = QRCodeFragment.newInstance(this, bundle);
                    Util.launchChildFragment(this, nextFrag, activity!!.supportFragmentManager);
                }
            };
        };
    }
}
