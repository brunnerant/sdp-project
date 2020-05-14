package ch.epfl.qedit.view.QR;

import static android.content.Context.WINDOW_SERVICE;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import com.google.zxing.WriterException;

public class QRGeneratorFragment extends Fragment {
    public static final String QUIZ_ID = "ch.epfl.qedit.view.quiz_id";
    String quizId;
    ImageView qrCode;
    Bitmap bitmap;
    QRGEncoder encoder;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q_r_generator, container, false);

        qrCode = (ImageView) view.findViewById(R.id.qr_code);
        quizId = requireArguments().getString(QUIZ_ID);
        if (quizId.length() > 0) {
            WindowManager manager =
                    (WindowManager) requireActivity().getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension *= 3 / 4;
            encoder = new QRGEncoder(quizId, null, QRGContents.Type.TEXT, smallerDimension);
            try {
                bitmap = encoder.encodeAsBitmap();
                qrCode.setImageBitmap(bitmap);
            } catch (WriterException e) {
                Log.v("QRGeneration", e.toString());
            }
        }
        return view;
    }
}
