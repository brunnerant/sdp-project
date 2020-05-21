package ch.epfl.qedit.view.QR;

import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRGeneratorFragment extends Fragment {
    String quizId;
    ImageView qrCode;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q_r_generator, container, false);
        qrCode = (ImageView) view.findViewById(R.id.qr_code);

        quizId = requireArguments().getString(QUIZ_ID);

        if (quizId.length() > 0) {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix =
                        multiFormatWriter.encode(quizId, BarcodeFormat.QR_CODE, 200, 200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                qrCode.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
