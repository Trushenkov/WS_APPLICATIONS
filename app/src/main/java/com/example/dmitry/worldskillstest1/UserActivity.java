package com.example.dmitry.worldskillstest1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserActivity extends AppCompatActivity {

    EditText loginBox;
    EditText passwordBox;
    EditText roleBox;
    EditText nameBox;
    EditText idBox;
    Button delButton;
    Button saveButton;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_user);

        //textviews
        idBox = findViewById(R.id.id);
        loginBox = findViewById(R.id.login);
        passwordBox = findViewById(R.id.password);
        roleBox = findViewById(R.id.role);
        nameBox = findViewById(R.id.name);
        //buttons
        saveButton = findViewById(R.id.saveButton);
        delButton = findViewById(R.id.deleteButton);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();

        Bundle extras = getIntent().getExtras();
//        System.out.println(extras);
//        Log.d("asd", extras.toString());
        if (extras != null) {
            userId = extras.getLong("id");
        }

        // если 0, то добавление
        if (userId > 0) {
            // получаем элемент по id из бд
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();

            idBox.setText(userCursor.getString(0));
            loginBox.setText(userCursor.getString(1));
            passwordBox.setText(userCursor.getString(2));
            roleBox.setText(userCursor.getString(3));
            nameBox.setText(userCursor.getString(4));

            userCursor.close();
        } else {
            // скрываем кнопку удаления
//            delButton.setVisibility(View.GONE);
        }
    }

    public void save(View view) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_ID, Integer.parseInt(idBox.getText().toString()));
        cv.put(DatabaseHelper.COLUMN_LOGIN, loginBox.getText().toString());
        cv.put(DatabaseHelper.COLUMN_PASSWORD, passwordBox.getText().toString());
        cv.put(DatabaseHelper.COLUMN_ROLE, roleBox.getText().toString());
        cv.put(DatabaseHelper.COLUMN_NAME, nameBox.getText().toString());

        if (userId > 0) {
            db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + String.valueOf(userId), null);
        } else {
            db.insert(DatabaseHelper.TABLE, null, cv);
        }
        goHome();
    }

    public void delete(View view) {
        db.delete(DatabaseHelper.TABLE,  DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        goHome();
    }

    private void goHome() {
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
