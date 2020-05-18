package es.upsa.mimo.gamesviewer.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.jetbrains.annotations.NotNull;

import es.upsa.mimo.datamodule.models.JuegoModel;
import es.upsa.mimo.datamodule.models.QRModel;
import es.upsa.mimo.gamesviewer.R;
import es.upsa.mimo.gamesviewer.misc.Utils;

public class GenerateQRCodeFragment extends BackFragment
{
    public static final String bundleJuegoInfoKey = "JuegoInfoFragmentJuegoInfo";
    private static final String saveJuegoQRInfoKey = "JuegoQRInfoKey";
    private JuegoModel juegoInfo;

    static GenerateQRCodeFragment newInstance(JuegoInfoFragment owner, Bundle bundle)
    {
        GenerateQRCodeFragment nuevoFrag = new GenerateQRCodeFragment();
        nuevoFrag.setArguments(bundle);
        nuevoFrag.ownerFragment = owner;
        return nuevoFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            juegoInfo = (JuegoModel) savedInstanceState.getSerializable(saveJuegoQRInfoKey);
            if (getActivity() != null)
                ownerFragment = Utils.findFragmentByClassName(this, JuegoInfoFragment.class.getName(), getActivity().getSupportFragmentManager()); // La vista solo puede ser creada por esta clase
        }
    }

    @Override
    public View onCreateChildView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (juegoInfo == null && getArguments() != null)
            juegoInfo = (JuegoModel)getArguments().getSerializable(bundleJuegoInfoKey);
        if (juegoInfo == null)
            throw new AssertionError(R.string.assert_needed_data_not_present);

        return inflater.inflate(R.layout.fragment_qrcode, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        TextView textNombreJuego = view.findViewById(R.id.textViewNombreJuego);
        ImageView imageQR = view.findViewById(R.id.imageViewQRCode);
        ImageButton buttonClose = view.findViewById(R.id.imageButtonClose);
        buttonClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onFragmentBack();
            }
        });

        textNombreJuego.setText(juegoInfo.name);
        try
        {
            generateQRCode(imageQR);
        }
        catch (WriterException e)
        {
            Log.e("error", e.getLocalizedMessage());
        }
    }

    private void generateQRCode(ImageView imageView) throws WriterException
    {
        QRModel qrModel = new QRModel(juegoInfo.getId(), juegoInfo.getName());
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.encodeBitmap(new Gson().toJson(qrModel), BarcodeFormat.QR_CODE, 512, 512);
        imageView.setImageBitmap(bitmap);
    }

    @NotNull
    @Override
    public String getFragmentTitle(@NotNull Context context)
    {
        return juegoInfo.getName();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(saveJuegoQRInfoKey, juegoInfo);
    }
}
