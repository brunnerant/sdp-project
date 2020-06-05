package ch.epfl.qedit.view.QR;

import static android.Manifest.permission.CAMERA;
import static ch.epfl.qedit.view.home.HomeActivity.USER;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.backend.database.Util;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.view.util.ConfirmDialog;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/** * this class is largely inspired from priyanka pakhale work available on github ** */
@SuppressWarnings("SpellCheckingInspection")
public class ScannerActivity extends AppCompatActivity
        implements ZXingScannerView.ResultHandler, ConfirmDialog.ConfirmationListener {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private DatabaseService db;
    private String quizId;
    private ConfirmDialog resultDialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DatabaseFactory.getInstance(this);
        user = (User) getIntent().getExtras().getSerializable(USER);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(
                                getApplicationContext(),
                                "Permission already granted!",
                                Toast.LENGTH_LONG)
                        .show();
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                checkNullScanner();
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    public void checkNullScanner() {
        if (scannerView == null) {
            scannerView = new ZXingScannerView(this);
            setContentView(scannerView);
        }
    }

    // for tests purpose
    public ZXingScannerView getScannerView() {
        return scannerView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CAMERA && grantResults.length > 0) {

            boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraAccepted) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                super.onBackPressed();
            }
        }
    }

    @Override
    public void handleResult(Result result) {
        quizId = result.getText();
        Log.d(result.getText(), "hey");

        ConfirmDialog.create("Do you want to load the quiz " + result.getText(), this)
                .show(getSupportFragmentManager(), null);
    }

    @Override
    public void onConfirm(ConfirmDialog dialog) {

        Util.getQuiz(db, quizId, this)
                .whenComplete(
                        (pair, throwable) -> {
                            if (throwable != null) {
                                Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                launchQuizActivity(pair.first, pair.second);
                            }
                        });
    }

    private void launchQuizActivity(Quiz quiz, StringPool pool) {
        Intent intent = new Intent(ScannerActivity.this, QuizActivity.class);
        quiz.instantiateLanguage(pool);
        user.addQuiz(quizId, quiz.getTitle());
        intent.putExtra(QUIZ_ID, quiz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
