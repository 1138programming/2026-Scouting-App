package com.scouting_app_2026.fragments;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.scouting_app_2026.MainActivity.ftm;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.databinding.QrCodeFragmentBinding;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QrCodeFragment extends Fragment {
    private QrCodeFragmentBinding binding;
    private String contents = "";
    private int dimen;

    public QrCodeFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = QrCodeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calcDimen();

        binding.closeMenu.setOnClickListener(view1 -> ftm.qrCodeClose());
    }

    public void updateQrCode() {
        String contents = ((MainActivity)requireActivity()).getQrCodeContents();
        Bitmap bitmap;
        int borderSize = 20;

        if(contents.equals(this.contents)) {
            return;
        }
        this.contents = contents;

        if(contents.isEmpty()) {
            binding.errorText.setVisibility(VISIBLE);
            bitmap = Bitmap.createBitmap(dimen, dimen, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
        }
        else {
            binding.errorText.setVisibility(INVISIBLE);

            QRGEncoder qrgEncoder = new QRGEncoder(contents,null, QRGContents.Type.TEXT, dimen);
            qrgEncoder.setColorBlack(Color.WHITE);
            qrgEncoder.setColorWhite(Color.BLACK);
            bitmap = Bitmap.createBitmap(qrgEncoder.getBitmap(), borderSize, borderSize,
                    dimen - borderSize * 2, dimen - borderSize * 2);
        }
        binding.QRcode.setImageBitmap(bitmap);
    }

    private void calcDimen() {
        final WindowMetrics metrics = requireActivity().getWindowManager().getCurrentWindowMetrics();
        final WindowInsets windowInsets = metrics.getWindowInsets();
        Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars()
                | WindowInsets.Type.displayCutout());

        int insetsWidth = insets.right + insets.left;
        int insetsHeight = insets.top + insets.bottom;

        final Rect bounds = metrics.getBounds();
        final Size size = new Size(bounds.width() - insetsWidth,
                bounds.height() - insetsHeight);

        // generating dimension from width and height.
        int dimen = Math.min(size.getWidth(), size.getHeight());
        this.dimen = dimen * 3 / 4;
    }

    @NonNull
    @Override
    public String toString() {
        return "QrCodeFragment";
    }
}
