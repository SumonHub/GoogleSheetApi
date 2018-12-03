package sumon.com.sheetApi;

import android.support.annotation.NonNull;
import android.util.Log;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by SumOn on 24/11/2017
 */
public class RealmHelper {
    public static String TAG = "REALM_HELPER";

    private Realm realm;
    private RealmResults<Data> allTask;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    //SAVE
    public void addTask(final Data data) {
        final RealmResults<Data> task = realm.where(Data.class)
                .equalTo("phnNo", data.phnNo)
                .findAll();
        if (task.isEmpty()) {

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    //   realm.createObject(Data.class);
                    realm.copyToRealm(data);
                }
            });
        }

    }

    public void deleteTask(final Data data) {
        final RealmResults<Data> task = realm.where(Data.class)
                .equalTo("name", data.getName())
                .equalTo("phnNo", data.getPhnNo())
                .findAll();
        if (!task.isEmpty()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.where(Data.class)
                            .equalTo("name", data.getName())
                            .equalTo("phnNo", data.getPhnNo())
                            .findFirst()
                            .deleteFromRealm();
                }

            });
        }
       // realm.close();
    }

    public ArrayList<Data> getAllTask() {
        allTask = realm.where(Data.class).findAll();
        ArrayList<Data> list = new ArrayList<>();
        list.addAll(allTask);
        Log.d(TAG, "getAllTask: " + allTask.toString());
        return list;
    }

    public void editTask(final Data data) {
        final RealmResults<Data> task = realm.where(Data.class)
                .equalTo("name", data.getName())
                .equalTo("phnNo", data.getPhnNo())
                .findAll();
        if (!task.isEmpty()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.where(Data.class)
                            .equalTo("name", data.getName())
                            .equalTo("phnNo", data.getPhnNo())
                            .findFirst()
                            .setSync(true);
                }

            });
        }
    }
}
