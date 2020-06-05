package es.upsa.mimo.gamesviewer.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import es.upsa.mimo.datamodule.models.PlatformModel
import es.upsa.mimo.gamesviewer.BuildConfig
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.HomeActivity
import es.upsa.mimo.gamesviewer.misc.*
import es.upsa.mimo.networkmodule.controllers.PlataformaNetworkController

class PlatformInfoFragment : BackFragment()
{
    companion object
    {
        val bundlePlatformInfoKey = "PlatformInfoFragmentPlatformId"
        val permissionWriteExternalStorageId = 100

        @JvmStatic
        fun newInstance(owner: PlatformsFragment, bundle: Bundle?): PlatformInfoFragment
        {
            val nuevoFrag = PlatformInfoFragment()
            nuevoFrag.arguments = bundle
            nuevoFrag.ownerFragment = owner
            return nuevoFrag
        }
    }

    private var platformId: Int = -1
    private lateinit var platformInfo: PlatformModel
    private val savePlatformIdKey = "PlatformIdKey"
    private val savePlatformInfoKey = "PlatformInfoKey"
    private lateinit var imagenPlataforma: ImageView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
        {
            val savedPlatId = savedInstanceState.getInt(savePlatformIdKey, -1)
            if (savedPlatId >= 0)
                platformId = savedPlatId
            platformInfo = savedInstanceState.getSerializable(savePlatformInfoKey) as PlatformModel
            if (activity != null)
                ownerFragment = findFragmentByClassName(PlatformsFragment::class.java.name, requireActivity().supportFragmentManager) // La vista solo puede ser creada por esta clase
        }
    }

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        if (platformId == -1)
            platformId = arguments?.getInt(bundlePlatformInfoKey, -1) ?: -1
        if (platformId == -1)
            throw AssertionError(R.string.assert_needed_data_not_present)

        return inflater.inflate(R.layout.fragment_platform_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        if (this::platformInfo.isInitialized)
        {
            setupView(view)
            return
        }

        activity?.let {
            PlataformaNetworkController.getPlataformaInfo(platformId, it) { fetchedInfo ->
                platformInfo = fetchedInfo
                setupView(view)
            }
        }
    }

    private fun setupView(view: View)
    {
        imagenPlataforma = view.findViewById(R.id.imageViewImgPlataforma)
        val textPlataforma = view.findViewById<TextView>(R.id.textPlatformDescription)
        val button = view.findViewById<Button>(R.id.buttonJuegosPlat)

        val homeActivity = activity as? HomeActivity
        homeActivity?.supportActionBar?.title = getString(R.string.platform_title_name, platformInfo.name)

        if (platformInfo.image_background != null)
            imagenPlataforma.loadFromURL(platformInfo.image_background!!)
        if (!TextUtils.isEmpty(platformInfo.description))
            textPlataforma.text = HtmlCompat.fromHtml(platformInfo.description!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
        else
            textPlataforma.text = getString(R.string.text_description_not_found)

        button.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(PlatformGamesFragment.bundlePlatformGamesKey, platformInfo)
            val nextFrag = PlatformGamesFragment.newInstance(this, bundle)
            launchChildFragment(this, nextFrag)
        }

        if (BuildConfig.FLAVOR == "paid")
        {
            imagenPlataforma.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    saveImageToGallery()
                else
                {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionWriteExternalStorageId)
                    else
                    {
                        val permissionDialog = PermissionDialogFragment.newInstance(this)
                        permissionDialog.show(requireActivity().supportFragmentManager, null)
                    }
                }
            }
        }
    }

    override fun getFragmentTitle(context: Context): String
    {
        return getString(R.string.platform_title_name, platformInfo.name)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putInt(savePlatformIdKey, platformId)
        outState.putSerializable(savePlatformInfoKey, platformInfo)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionWriteExternalStorageId && grantResults.isNotEmpty())
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
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionWriteExternalStorageId)
        else if (requestCode == SaveConfirmationFragment.requestCode && resultCode == SaveConfirmationFragment.requestCodeOk)
            imagenPlataforma.saveToGallery(platformId.toString(), platformInfo.name)
    }

    private fun saveImageToGallery()
    {
        val confirmDialog = SaveConfirmationFragment.newInstance(this)
        confirmDialog.show(requireActivity().supportFragmentManager, null)
    }
}
