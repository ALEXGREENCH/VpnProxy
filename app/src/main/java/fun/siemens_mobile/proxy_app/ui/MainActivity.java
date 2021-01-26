package fun.siemens_mobile.proxy_app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import fun.siemens_mobile.proxy_app.R;
import fun.siemens_mobile.proxy_app.core.Constant;
import fun.siemens_mobile.proxy_app.core.LocalVpnService;
import fun.siemens_mobile.proxy_app.ui.adapter.MyAdapter;
import fun.siemens_mobile.proxy_app.ui.model.ProxyItem;

public class MainActivity extends AppCompatActivity implements
        OnCheckedChangeListener,
        LocalVpnService.onStatusChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int START_VPN_SERVICE_REQUEST_CODE = 1985;
    private static String GL_HISTORY_LOGS;
    private SwitchCompat switchProxy;
    private TextView textViewLog;
    private ScrollView scrollViewLog;
    private Calendar mCalendar;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 32;

    private void updateTilte() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (LocalVpnService.IsRunning) {
                actionBar.setTitle(getString(R.string.connected));
            } else {
                actionBar.setTitle(getString(R.string.disconnected));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scrollViewLog = findViewById(R.id.scrollViewLog);
        textViewLog = findViewById(R.id.textViewLog);
        recyclerView = findViewById(R.id.list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assert textViewLog != null;
        textViewLog.setText(GL_HISTORY_LOGS);
        scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
        scrollViewLog.setScrollbarFadingEnabled(false);

        mCalendar = Calendar.getInstance();
        LocalVpnService.addOnStatusChangedListener(this);


        addReadFile();
    }

    private void addReadFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique

                return;
            } else {
                testReadText();
            }
        }
    }

    private void testReadText() {

        ArrayList<ProxyItem> proxyItemArrayList = new ArrayList<>();


        String state = Environment.getExternalStorageState();
        Log.d("TAG", "ExternalStorageState : " + state);

        File sdcard;

        // Make sure it's available
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            sdcard = getExternalFilesDir(null);
        } else {
            // Load another directory, probably local memory
            sdcard = getFilesDir();
        }

        Log.d("TAG", "AbsolutePath : " + sdcard.getAbsolutePath());


        for (File c : sdcard.listFiles()) {
            Log.d("TAG", "child : " + c.getName());

        }

        File fileHttp = new File(sdcard, "http.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileHttp));


            String line;
            while ((line = br.readLine()) != null) {
                Log.d("TAG", "http line: " + line);

                String[] values = line.split(",");
                StringBuilder name = new StringBuilder();
                String tempLast = "";
                for (String s : values) {
                    name.append(s).append(" ");
                    tempLast = s;
                }

                String n = name.toString();
                String urlHttp = "http://" + tempLast.trim().replaceAll("\\s+", "");

                System.out.println("PROXY NAME : " + n);
                System.out.println("PROXY IP: " + urlHttp);

                ProxyItem p = new ProxyItem();
                p.setUrl(urlHttp);
                p.setName(n);
                proxyItemArrayList.add(p);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

        File fileHttps = new File(sdcard, "https.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileHttps));
            String line;
            while ((line = br.readLine()) != null) {
                Log.d("TAG", "http line: " + line);

                String[] values = line.split(",");
                StringBuilder name = new StringBuilder();
                String tempLast = "";
                for (String s : values) {
                    name.append(s).append(" ");
                    tempLast = s;
                }

                String n = name.toString();
                String urlHttps = "https://" + tempLast.trim().replaceAll("\\s+", "");

                System.out.println("PROXY NAME : " + n);
                System.out.println("PROXY IP: " + urlHttps);

                ProxyItem p = new ProxyItem();
                p.setUrl(urlHttps);
                p.setName(n);
                proxyItemArrayList.add(p);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }


        mAdapter = new MyAdapter(proxyItemArrayList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                testReadText();
            } else {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTilte();
    }

    String getVersionName() {
        PackageManager packageManager = getPackageManager();
        if (packageManager == null) {
            Log.e(TAG, "null package manager is impossible");
            return null;
        }

        try {
            return packageManager.getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "package not found is impossible", e);
            return null;
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLogReceived(String logString) {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        logString = String.format("[%1$02d:%2$02d:%3$02d] %4$s\n",
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                mCalendar.get(Calendar.SECOND),
                logString);

        Log.d(Constant.TAG, logString);

        if (textViewLog.getLineCount() > 200) {
            textViewLog.setText("");
        }
        textViewLog.append(logString);
        scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
        GL_HISTORY_LOGS = textViewLog.getText() == null ? "" : textViewLog.getText().toString();
    }

    @Override
    public void onStatusChanged(String status, Boolean isRunning) {
        switchProxy.setEnabled(true);
        switchProxy.setChecked(isRunning);
        onLogReceived(status);
        updateTilte();
        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (LocalVpnService.IsRunning != isChecked) {
            switchProxy.setEnabled(false);
            if (isChecked) {
                Intent intent = LocalVpnService.prepare(this);
                if (intent == null) {
                    startVPNService();
                } else {
                    startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE);
                }
            } else {
                LocalVpnService.IsRunning = false;
            }
        }
    }

    private void startVPNService() {
        textViewLog.setText("");
        GL_HISTORY_LOGS = null;
        onLogReceived("Поехали...");
        startService(new Intent(this, LocalVpnService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == START_VPN_SERVICE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startVPNService();
            } else {
                switchProxy.setChecked(false);
                switchProxy.setEnabled(true);
                onLogReceived("canceled.");
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_switch);
        if (menuItem == null) {
            return false;
        }

        switchProxy = (SwitchCompat) menuItem.getActionView();
        if (switchProxy == null) {
            return false;
        }

        switchProxy.setChecked(LocalVpnService.IsRunning);
        switchProxy.setOnCheckedChangeListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.menu_item_about:
            //    new AlertDialog.Builder(this)
            //            .setTitle(getString(R.string.app_name) + " " + getVersionName())
            //            .setMessage(R.string.about_info)
            //            .setPositiveButton(R.string.btn_ok, null)
            //            .show();
            //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        LocalVpnService.removeOnStatusChangedListener(this);
        super.onDestroy();
    }

}
