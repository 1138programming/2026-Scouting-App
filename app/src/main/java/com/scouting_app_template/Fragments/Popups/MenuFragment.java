package com.scouting_app_template.Fragments.Popups;

import static com.scouting_app_template.MainActivity.ftm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.scouting_app_template.Bluetooth.CaptureAct;
import com.scouting_app_template.Bluetooth.QrBtConnThread;
import com.scouting_app_template.databinding.MenuDropdownBinding;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
    private MenuDropdownBinding binding;
    private final ActivityResultLauncher<ScanOptions> barLauncher;
    private final ArrayList<Long> adminClicks = new ArrayList<>();
    private boolean adminNotActivated = true;
    public enum MenuOptions {
        connect,
        reset,
        archive,
        admin,
        outside
    }

    public MenuFragment() {
         barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if(result.getContents() == null) {
                Log.e("1138 SCApp", "QR-Code empty");
            }
            else {
                String contents = result.getContents();
                String[] results = contents.split(";");
                if(results.length == 2) {
                    String log = "MAC Address: " + results[0] + "  Port: " + results[1];
                    Log.d("1138 SCApp", log);
                    QrBtConnThread.bluetoothConnect(results[0], Integer.parseInt(results[1]));
                }
                else {
                    Log.e("1138 SCApp", "Error parsing QR-Code");
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = MenuDropdownBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.menuConnectButton.setOnClickListener(View1 -> menuSelected(MenuOptions.connect));
        binding.menuResetButton.setOnClickListener(View1 -> menuSelected(MenuOptions.reset));
        binding.menuArchiveButton.setOnClickListener(View1 -> menuSelected(MenuOptions.archive));
        binding.menuAdminButton.setOnClickListener(View1 -> menuSelected(MenuOptions.admin));
        binding.backgroundDetect.setOnClickListener(View1 -> menuSelected(MenuOptions.outside));

    }

    private void menuSelected(MenuOptions selectedOption) {
        switch(selectedOption) {
            case connect:
                ftm.menuClose();
                scanCode();
                break;
            case reset:
                ftm.menuReset();
                break;
            case archive:
                ftm.menuArchive();
                break;
            case admin:
                long now = System.currentTimeMillis();
                adminClicks.add(now);
                for(int i = 0; i < adminClicks.size(); i++) {
                    if((now - adminClicks.get(i)) > 1500) {
                        adminClicks.remove(i);
                        i--;
                    }
                }
                if(adminNotActivated && adminClicks.size() >= 5) {
                    adminSelected();

                }
                break;
            case outside:
                ftm.menuClose();
        }
    }

    public void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR-Code on Computer to Connect");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private void adminSelected() {

    }

    @NonNull
    @Override
    public String toString() {
        return "MenuFragment";
    }
}
