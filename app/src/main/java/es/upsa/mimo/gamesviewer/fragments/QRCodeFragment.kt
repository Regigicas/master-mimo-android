package es.upsa.mimo.gamesviewer.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.datamodule.models.QRModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.BackFragment

class QRCodeFragment : BackFragment()
{
    companion object
    {
        val bundleJuegoInfoKey = "JuegoInfoFragmentJuegoInfo";

        @JvmStatic
        fun newInstance(owner: Fragment, bundle: Bundle?): QRCodeFragment
        {
            val nuevoFrag = QRCodeFragment();
            nuevoFrag.arguments = bundle;
            nuevoFrag.ownerFragment = owner;
            return nuevoFrag;
        }
    }

    private var juegoInfo: JuegoModel? = null;

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        juegoInfo = arguments?.getSerializable(bundleJuegoInfoKey) as JuegoModel?;
        if (juegoInfo == null)
            throw AssertionError(R.string.assert_needed_data_not_present);

        return inflater.inflate(R.layout.fragment_qrcode, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);

        val textNombreJuego = view.findViewById<TextView>(R.id.textViewNombreJuego);
        val imageQR = view.findViewById<ImageView>(R.id.imageViewQRCode);
        val buttonClose = view.findViewById<ImageButton>(R.id.imageButtonClose);
        buttonClose.setOnClickListener {
            onFragmentBack();
        }

        textNombreJuego.text = juegoInfo!!.name;
        generateQRCode(imageQR);
    }

    private fun generateQRCode(imageView: ImageView)
    {
        val qrModel = QRModel(juegoInfo!!.id!!, juegoInfo!!.name!!);
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(Gson().toJson(qrModel), BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width)
            for (y in 0 until height)
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)

        imageView.setImageBitmap(bitmap)
    }
}
