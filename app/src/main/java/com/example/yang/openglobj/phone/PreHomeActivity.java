package com.example.yang.openglobj.phone;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.greendao.User;
import com.example.greendao.UserDao;
import com.example.yang.openglobj.BaseApplication;
import com.example.yang.openglobj.R;
import com.example.yang.openglobj.util.ToastUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class PreHomeActivity extends ListActivity {

    private EditText editText;
    private Cursor cursor;
    public static final String TAG = "DaoExample";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_home);

        String textColumn = UserDao.Properties.Name.columnName;
        String orderBy = textColumn + " COLLATE LOCALIZED ASC";
        cursor = getDb().query(getUserDao().getTablename(), getUserDao().getAllColumns(), null, null, null, null, orderBy);
        String[] from = {textColumn, UserDao.Properties.Password.columnName};
        int[] to = {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from,
                to);
        setListAdapter(adapter);

        editText = (EditText) findViewById(R.id.editTextNote);
    }

    private UserDao getUserDao() {
        // 通过 BaseApplication 类提供的 getDaoSession() 获取具体 Dao
        return ((BaseApplication) this.getApplicationContext()).getDaoSession().getUserDao();
    }

    private SQLiteDatabase getDb() {
        // 通过 BaseApplication 类提供的 getDb() 获取具体 db
        return ((BaseApplication) this.getApplicationContext()).getDb();
    }

    /**
     * Button 点击的监听事件
     *
     * @param view
     */
    public void onMyButtonClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAdd:
                addNote();
                break;
            case R.id.buttonSearch:
                search();
                break;
            default:
                ToastUtils.show(getApplicationContext(), "What's wrong ?");
                break;
        }
    }

    private void addNote() {
        /*String name = editText.getText().toString();
        editText.setText("");

        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());

        if (name == null || name.equals("")) {
            ToastUtils.show(getApplicationContext(), "Please enter a name to add");
        } else {
            // 插入操作，简单到只要你创建一个 Java 对象
            User user = new User(null, name, comment);
            getUserDao().insert(user);
            Log.d(TAG, "Inserted new note, ID: " + user.getId());
            cursor.requery();
        }*/
        HomeActivity.actionStart(this, true);
    }

    private void search() {
        String noteText = editText.getText().toString();
        editText.setText("");
        if (noteText == null || noteText.equals("")) {
            ToastUtils.show(getApplicationContext(), "Please enter a name to query");
        } else {
            // Query 类代表了一个可以被重复执行的查询
            Query query = getUserDao().queryBuilder()
                    .where(UserDao.Properties.Name.eq(noteText))
                    .orderAsc(UserDao.Properties.Name)
                    .build();
            // 查询结果以 List 返回
            List notes = query.list();
            ToastUtils.show(getApplicationContext(), "There have " + notes.size() + " records");
        }
        // 在 QueryBuilder 类中内置两个 Flag 用于方便输出执行的 SQL 语句与传递参数的值
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    /**
     * ListView 的监听事件，用于删除一个 Item
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // 删除操作，你可以通过「id」也可以一次性删除所有
        getUserDao().deleteByKey(id);
//        getNoteDao().deleteAll();
        ToastUtils.show(getApplicationContext(), "Deleted user, ID: " + id);
        Log.d(TAG, "Deleted user, ID: " + id);
        cursor.requery();
    }

}
