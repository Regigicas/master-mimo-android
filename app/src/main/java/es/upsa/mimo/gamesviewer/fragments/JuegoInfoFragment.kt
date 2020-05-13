package es.upsa.mimo.gamesviewer.fragments

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import es.upsa.mimo.datamodule.controllers.UsuarioController
import es.upsa.mimo.datamodule.database.entities.Usuario
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.HomeActivity
import es.upsa.mimo.gamesviewer.misc.BackFragment
import es.upsa.mimo.gamesviewer.misc.TitleFragment
import es.upsa.mimo.gamesviewer.misc.Util
import es.upsa.mimo.networkmodule.controllers.JuegoNetworkController
import kotlinx.coroutines.launch

class JuegoInfoFragment : BackFragment()
{
    companion object
    {
        val bundleJuegoInfoKey = "JuegoInfoFragmentJuegoId";

        @JvmStatic
        fun newInstance(owner: TitleFragment, bundle: Bundle?): JuegoInfoFragment
        {
            val nuevoFrag = JuegoInfoFragment();
            nuevoFrag.arguments = bundle;
            nuevoFrag.ownerFragment = owner;
            return nuevoFrag;
        }
    }

    private var juegoId: Int? = null;
    private var juegoInfo: JuegoModel? = null;
    private val saveJuegoIdKey = "JuegoIdKey";
    private val saveJuegoInfoKey = "JuegoInfoKey";
    private val saveParentFragmentIdKey = "FragmentParentPlatformKey";
    private val saveEsFavoritoKey = "EsFavoritoKey";
    private var esFavorito: Boolean? = null;
    private var blockedUpdate = false;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            val savedPlatId = savedInstanceState.getInt(saveJuegoIdKey, -1);
            if (savedPlatId >= 0)
                juegoId = savedPlatId;
            juegoInfo = savedInstanceState.getSerializable(saveJuegoInfoKey) as JuegoModel;
            val savedFragmentName = savedInstanceState.getString(saveParentFragmentIdKey);
            if (activity != null && savedFragmentName != null)
                ownerFragment = Util.findFragmentByClassName(savedFragmentName, activity!!.supportFragmentManager); // La vista solo puede ser creada por esta clase
            if (savedInstanceState.containsKey(saveEsFavoritoKey))
                esFavorito = savedInstanceState.getBoolean(saveEsFavoritoKey);
        }
    }

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        if (juegoId == null)
            juegoId = arguments?.getInt(bundleJuegoInfoKey);
        if (juegoId == null)
            throw AssertionError(R.string.assert_needed_data_not_present);

        return inflater.inflate(R.layout.fragment_juego_info, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);

        if (juegoInfo != null)
        {
            setupView(view);
            return;
        }

        activity?.let {
            JuegoNetworkController.getJuegoInfo(juegoId!!, it) { info ->
                juegoInfo = info;
                setupView(view);
            };
        };
    }

    private fun setupView(view: View)
    {
        val homeActivity = activity as? HomeActivity;
        homeActivity?.supportActionBar?.title = juegoInfo!!.name;

        val imagenJuego = view.findViewById<ImageView>(R.id.imageViewJuego);
        val textoPlataformas = view.findViewById<TextView>(R.id.textViewPlatforms);
        val textoValoracion = view.findViewById<TextView>(R.id.textViewRatingValue);
        val textoFechaSalida = view.findViewById<TextView>(R.id.textViewReleaseValue);
        val textoDescripcion = view.findViewById<TextView>(R.id.textViewDescriptionValue);
        val buttonQR = view.findViewById<ImageButton>(R.id.imageButtonQRCode);
        val buttonFavorite = view.findViewById<ImageButton>(R.id.imageButtonFavorito);

        Picasso.get().load(juegoInfo!!.background_image).fit().centerCrop().into(imagenJuego);
        textoDescripcion.text = HtmlCompat.fromHtml(juegoInfo!!.description!!, HtmlCompat.FROM_HTML_MODE_LEGACY);
        textoPlataformas.text = juegoInfo!!.getPlatformString();
        textoValoracion.text = juegoInfo!!.rating.toString();
        textoFechaSalida.text = juegoInfo!!.released;

        if (esFavorito == null)
        {
            blockedUpdate = true;
            lifecycleScope.launch {
                esFavorito = UsuarioController.hasFavorite(juegoId!!, activity!!);
                updateFavoriteIcon(buttonFavorite);
                blockedUpdate = false;
            }
        }

        buttonQR.setOnClickListener {
            val bundle = Bundle();
            bundle.putSerializable(GenerateQRCodeFragment.bundleJuegoInfoKey, juegoInfo);
            val nextFrag = GenerateQRCodeFragment.newInstance(this, bundle);
            Util.launchChildFragment(this, nextFrag, activity!!.supportFragmentManager);
        }

        buttonFavorite.setOnClickListener {
            if (esFavorito == null || blockedUpdate)
                return@setOnClickListener;

            blockedUpdate = true;
            lifecycleScope.launch {
                if (esFavorito == true)
                    UsuarioController.removeJuegoFavorito(juegoInfo!!.id!!, activity!!);
                else
                    UsuarioController.addJuegoFavorito(juegoInfo!!, activity!!);

                blockedUpdate = false;
                esFavorito = !esFavorito!!;
                updateFavoriteIcon(buttonFavorite);
            }
        }
    }

    private fun updateFavoriteIcon(buttonFavorite: ImageButton)
    {
        if (esFavorito!!)
            buttonFavorite.setImageDrawable(activity!!.getDrawable(R.drawable.icono_favorito_on));
        else
            buttonFavorite.setImageDrawable(activity!!.getDrawable(R.drawable.icono_favorito_off));
    }

    override fun getFragmentTitle(context: Context): String
    {
        if (juegoInfo != null)
            return juegoInfo!!.name!!;

        return getString(R.string.assert_needed_data_not_present);
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(saveJuegoIdKey, juegoId!!);
        outState.putSerializable(saveJuegoInfoKey, juegoInfo);
        outState.putString(saveParentFragmentIdKey, ownerFragment!!::class.qualifiedName!!);
        esFavorito?.let { outState.putBoolean(saveEsFavoritoKey, it) };
    }
}
