package sumon.com.sheetApi;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.ArrayList;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity implements InternetConnectivityListener {
    private final String TAG = MainActivity.this.getClass().getName();
    TextInputEditText _name;
    TextInputEditText _phnNo;
    TextInputEditText _address;
    TextInputEditText _issue;
    Button _submitButton;

    Data data = new Data();

    Realm realm;
    RealmHelper realmHelper;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //---------------> Realm <----------------------//
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("realm.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
        //  Realm.deleteRealm(configuration);

        /*realm = Realm.getDefaultInstance();
        realmHelper = new RealmHelper(realm);
        final ArrayList<Data> allLocalData = realmHelper.getAllTask();
        for (int i = 0; i < allLocalData.size(); i++){
            Data data = allLocalData.get(i);
            if(!data.isSync){
                //realmHelper.addTask(data);


            }
        }*/

        InternetAvailabilityChecker.init(this);
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);

        _name = findViewById(R.id.et_name);
        _phnNo = findViewById(R.id.et_phnNo);
        _address = findViewById(R.id.et_address);
        _issue = findViewById(R.id.et_issue);

        _submitButton = findViewById(R.id.btn_submit);
        _submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        // check internet connection
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        onInternetConnectivityChanged(isConnected);

    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            Toast.makeText(getBaseContext(), "Submission failed", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Submitting...");
        progressDialog.show();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        Toast.makeText(getBaseContext(), "Submission successful", Toast.LENGTH_SHORT).show();
                        data.setSync(false);
                        pushDataToLocal(data);
                       // syncData(data);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public boolean validate() {

        boolean valid = true;

        String name = Objects.requireNonNull(_name.getText()).toString();
        String phnNo = Objects.requireNonNull(_phnNo.getText()).toString();
        String address = Objects.requireNonNull(_address.getText()).toString();
        String issue = Objects.requireNonNull(_issue.getText()).toString();

        if (name.isEmpty() || name.length() < 4) {
            _name.setError("minimum 4 character required");
            valid = false;
        } else {
            _name.setError(null);
            data.setName(name);
        }
        if (phnNo.isEmpty() || phnNo.length() < 11) {
            _phnNo.setError("enter a valid mobile no");
            valid = false;
        } else {
            _phnNo.setError(null);
            data.setPhnNo("+88"+phnNo);
        }
        if (address.isEmpty() || address.length() < 4) {
            _address.setError("minimum 4 character required");
            valid = false;
        } else {
            _address.setError(null);
            data.setAddress(address);
        }
        if (issue.isEmpty() || issue.length() < 15) {
            _issue.setError("minimum 15 character required");
            valid = false;
        } else {
            _issue.setError(null);
            data.setIssue(issue);
        }

        return valid;
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if(isConnected) {
         //   Toast.makeText(this,"internet on", Toast.LENGTH_LONG).show();
            realm = null;
            ArrayList<Data> allLocalData;
            try {
                realm = Realm.getDefaultInstance();
                realmHelper = new RealmHelper(realm);
                allLocalData = realmHelper.getAllTask();
                for (int i = 0; i < allLocalData.size(); i++) {
                    Data data = allLocalData.get(i);
                    boolean isSync = data.isSync();
                    if (!isSync) {
                        syncData(realm.copyFromRealm(data));
                    } else {
                        removeFromLocal(realm.copyFromRealm(data));
                    }
                }
            }finally {
                if (realm!= null){
                    realm.close();
                }
            }

        } // Toast.makeText(this,"internet off", Toast.LENGTH_LONG).show();


    }

    private void pushDataToLocal(Data data) {
        realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realmHelper = new RealmHelper(realm);
            realmHelper.addTask(data);
            Log.d(TAG, "pushDataToLocal: "+ data.getName());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void removeFromLocal(Data data) {
        realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realmHelper = new RealmHelper(realm);
            realmHelper.deleteTask(data);
            Log.d(TAG, "removeFromLocal: "+ data.getName());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void syncData(Data data) {
        realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realmHelper = new RealmHelper(realm);
            realmHelper.editTask(data);
            Log.d(TAG, "removeFromLocal: "+ data.getName());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
            SheetApiHelper sheetApiHelper = new SheetApiHelper();
            sheetApiHelper.execute(data);
            Log.d(TAG, "syncData: "+ data.getName());

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
    }
}
