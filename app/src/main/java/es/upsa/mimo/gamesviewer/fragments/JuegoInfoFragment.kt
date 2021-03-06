package es.upsa.mimo.gamesviewer.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import es.upsa.mimo.datamodule.controllers.UsuarioController
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.gamesviewer.BuildConfig
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.HomeActivity
import es.upsa.mimo.gamesviewer.misc.*
import es.upsa.mimo.networkmodule.controllers.JuegoNetworkController
import kotlinx.coroutines.launch

class JuegoInfoFragment : BackFragment()
{
    companion object
    {
        val bundleJuegoInfoKey = "JuegoInfoFragmentJuegoId"

        @JvmStatic
        fun newInstance(owner: TitleFragment, bundle: Bundle?): JuegoInfoFragment
        {
            val nuevoFrag = JuegoInfoFragment()
            nuevoFrag.arguments = bundle
            nuevoFrag.ownerFragment = owner
            return nuevoFrag
        }
    }

    private var juegoId: Int = -1
    private lateinit var juegoInfo: JuegoModel
    private val saveJuegoIdKey = "JuegoIdKey"
    private val saveJuegoInfoKey = "JuegoInfoKey"
    private val saveParentFragmentIdKey = "FragmentParentPlatformKey"
    private val saveEsFavoritoKey = "EsFavoritoKey"
    private var esFavorito = false
    private var blockedUpdate = true
    private lateinit var imagenJuego: ImageView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
        {
            val savedPlatId = savedInstanceState.getInt(saveJuegoIdKey, -1)
            if (savedPlatId >= 0)
                juegoId = savedPlatId
            juegoInfo = savedInstanceState.getSerializable(saveJuegoInfoKey) as JuegoModel
            val savedFragmentName = savedInstanceState.getString(saveParentFragmentIdKey)
            if (activity != null && savedFragmentName != null)
                ownerFragment = findFragmentByClassName(savedFragmentName, requireActivity().supportFragmentManager) // La vista solo puede ser creada por esta clase
            if (savedInstanceState.containsKey(saveEsFavoritoKey))
                esFavorito = savedInstanceState.getBoolean(saveEsFavoritoKey)
        }
    }

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        if (juegoId == -1)
            juegoId = arguments?.getInt(bundleJuegoInfoKey, -1) ?: -1
        if (juegoId == -1)
            throw AssertionError(R.string.assert_needed_data_not_present)

        return inflater.inflate(R.layout.fragment_juego_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        if (this::juegoInfo.isInitialized)
        {
            setupView(view)
            return
        }

        activity?.let {
            JuegoNetworkController.getJuegoInfo(juegoId, it) { info ->
                juegoInfo = info
                blockedUpdate = false
                setupView(view)
            }
        }
    }

    private fun setupView(view: View)
    {
        val homeActivity = activity as? HomeActivity
        homeActivity?.supportActionBar?.title = juegoInfo.name

        imagenJuego = view.findViewById(R.id.imageViewJuego)
        val textoPlataformas = view.findViewById<TextView>(R.id.textViewPlatforms)
        val textoValoracion = view.findViewById<TextView>(R.id.textViewRatingValue)
        val textoFechaSalida = view.findViewById<TextView>(R.id.textViewReleaseValue)
        val textoDescripcion = view.findViewById<TextView>(R.id.textViewDescriptionValue)
        val buttonQR = view.findViewById<ImageButton>(R.id.imageButtonQRCode)
        val buttonFavorite = view.findViewById<ImageButton>(R.id.imageButtonFavorito)
        val buttonClip = view.findViewById<Button>(R.id. buttonClip)

        imagenJuego.clipToOutline = true
        if (juegoInfo.background_image != null)
            imagenJuego.loadFromURL(juegoInfo.background_image!!)
        if (!TextUtils.isEmpty(juegoInfo.description))
            textoDescripcion.text = HtmlCompat.fromHtml(juegoInfo.description!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
        else
            textoDescripcion.text = getString(R.string.text_description_not_found)
        textoPlataformas.text = juegoInfo.getPlatformString()
        textoValoracion.text = juegoInfo.rating.toString()
        textoFechaSalida.text = juegoInfo.released

        if (esFavorito == false)
        {
            blockedUpdate = true
            lifecycleScope.launch {
                activity?.let {
                    esFavorito = UsuarioController.hasFavorite(juegoId, null, it)
                    updateFavoriteIcon(buttonFavorite)
                }
                blockedUpdate = false
            }
        }
        else
            updateFavoriteIcon(buttonFavorite)

        if (BuildConfig.FLAVOR == "paid")
        {
            buttonQR.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable(GenerateQRCodeFragment.bundleJuegoInfoKey, juegoInfo)
                val nextFrag = GenerateQRCodeFragment.newInstance(this, bundle)
                launchChildFragment(this, nextFrag)
            }
        }
        else
            buttonQR.visibility = View.GONE

        buttonClip.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(ClipFragment.bundleJuegoInfoKey, juegoInfo)
            val nextFrag = ClipFragment.newInstance(this, bundle)
            launchChildFragment(this, nextFrag)
        }

        buttonFavorite.setOnClickListener {
            if (blockedUpdate)
                return@setOnClickListener

            blockedUpdate = true
            lifecycleScope.launch {
                if (esFavorito == true)
                    UsuarioController.removeJuegoFavorito(juegoInfo.id, requireActivity())
                else
                    UsuarioController.addJuegoFavorito(juegoInfo, requireActivity())

                blockedUpdate = false
                esFavorito = !esFavorito
                updateFavoriteIcon(buttonFavorite)
            }
        }

        if (BuildConfig.FLAVOR == "paid")
        {
            imagenJuego.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    saveImageToGallery()
                else
                {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PlatformInfoFragment.permissionWriteExternalStorageId)
                    else
                    {
                        val permissionDialog = PermissionDialogFragment.newInstance(this)
                        permissionDialog.show(requireActivity().supportFragmentManager, null)
                    }
                }
            }
        }
    }

    private fun updateFavoriteIcon(buttonFavorite: ImageButton)
    {
        if (esFavorito)
            buttonFavorite.setImageDrawable(requireActivity().getDrawable(R.drawable.icono_favorito_on))
        else
            buttonFavorite.setImageDrawable(requireActivity().getDrawable(R.drawable.icono_favorito_off))
    }

    override fun getFragmentTitle(context: Context): String
    {
        return juegoInfo.name
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putInt(saveJuegoIdKey, juegoId)
        outState.putSerializable(saveJuegoInfoKey, juegoInfo)
        if (ownerFragment != null)
            outState.putString(saveParentFragmentIdKey, ownerFragment!!::class.java.name)
        outState.putBoolean(saveEsFavoritoKey, esFavorito)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PlatformInfoFragment.permissionWriteExternalStorageId && grantResults.isNotEmpty())
        {
            if (grantResults.get(0) != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(requireContext(), R.string.no_write_permission, Toast.LENGTH_LONG).show()
            else
                saveImageToGallery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionDialogFragment.requestCode && resultCode == PermissionDialogFragment.requestCodeOk)
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PlatformInfoFragment.permissionWriteExternalStorageId)
        else if (requestCode == SaveConfirmationFragment.requestCode && resultCode == SaveConfirmationFragment.requestCodeOk)
            imagenJuego.saveToGallery(juegoId.toString(), juegoInfo.name)
    }

    private fun saveImageToGallery()
    {
        val confirmDialog = SaveConfirmationFragment.newInstance(this)
        confirmDialog.show(requireActivity().supportFragmentManager, null)
    }
}
