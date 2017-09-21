package com.educareapps.jsonreader;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.educareapps.jsonreader.dao.Item;
import com.educareapps.jsonreader.dao.Task;
import com.educareapps.jsonreader.dao.TaskPack;
import com.educareapps.jsonreader.manager.DatabaseManager;
import com.educareapps.jsonreader.manager.IDatabaseManager;
import com.educareapps.jsonreader.share.Share;
import com.educareapps.jsonreader.utilitis.DialogNavBarHide;
import com.educareapps.jsonreader.utilitis.StaticAccess;
import com.limbika.material.dialog.FileDialog;
import com.limbika.material.dialog.SelectorDialog;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener {

    MainActivity activity;
    ArrayList<Item> allItemArr;
    ArrayList<TaskPack> allTaskPackArr;
    private IDatabaseManager databaseManager;
    Button ibtnImportId;
    Button ibtnShareId;
    ItemAdapter itemAdapter;
    ListView lvItem;
    ProgressDialog pDialog;
    Share share;

    private class DeleteAsync extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getResources().getString(R.string.importPdialog));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            taskPackDelete();
            return null;
        }

        protected void onPostExecute(String s) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            reloadItems(0);
            FileDialog fileDialog = new FileDialog(activity, FileDialog.Strategy.FILE);
            fileDialog.setCancelable(false);
            fileDialog.show();
            fileDialog.setOnSelectListener(new SelectorDialog.OnSelectListener() {
                @Override
                public void onSelect(String filePath) {

                    if (filePath == null || !filePath.endsWith(StaticAccess.DOT_JSON.toUpperCase())) {
                        Toast.makeText(activity, "no file path", Toast.LENGTH_LONG).show();
                    } else {
                        new JsonReading(filePath).execute();
                    }
                    Toast.makeText(activity, filePath, Toast.LENGTH_LONG).show();
                }


            });
        }
    }

    private class GenerateJson extends AsyncTask<String, String, String> {
        String filePath;

        private GenerateJson() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this.activity);
            pDialog.setMessage(MainActivity.this.getResources().getString(R.string.importPdialog));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            allTaskPackArr = new ArrayList<>();
            allTaskPackArr = (ArrayList<TaskPack>) databaseManager.listTaskPacks();
            if (allTaskPackArr.size() > 0) {
                try {
                    share.generateTaskPackJSON(allTaskPackArr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String s) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    private class JsonReading extends AsyncTask<String, String, String> {
        String filePath;

        public JsonReading(String filePath) {
            this.filePath = filePath;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getResources().getString(R.string.importPdialog));

            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... params) {

            share.readSharedTaskPackJSONtoDatabase(filePath);

            return null;
        }

        protected void onPostExecute(String s) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            reloadItems(0);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        share = new Share(this.activity);
        databaseManager = new DatabaseManager(this.activity);
        ibtnImportId = (Button) findViewById(R.id.ibtnImportId);
        ibtnShareId = (Button) findViewById(R.id.ibtnShareId);
        lvItem = (ListView) findViewById(R.id.lvItem);
        reloadItems(0);
        ibtnImportId.setOnClickListener(this);
        ibtnShareId.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnImportId:
                new DeleteAsync().execute();
                return;
            case R.id.ibtnShareId:
                new GenerateJson().execute();
                return;
            default:
                return;
        }
    }

    public void taskPackDelete() {
        this.allTaskPackArr = new ArrayList<>();
        this.allTaskPackArr = (ArrayList<TaskPack>) databaseManager.listTaskPacks();
        if (allTaskPackArr != null) {
            for (TaskPack taskPack : allTaskPackArr) {
                ArrayList<Task> tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(taskPack.getId());
                if (tasks != null) {
                    for (Task task : tasks) {
                        LinkedHashMap<Long, Item> items = databaseManager.loadTaskWiseItem(task);
                        if (items != null) {
                            for (Map.Entry<Long, Item> itemValue : items.entrySet()) {
                                Item item = itemValue.getValue();
                                databaseManager.deleteItemById(item.getId());
                            }
                        }
                        databaseManager.deleteTaskById(task.getId());
                    }
                }
                databaseManager.deleteTaskPackById(taskPack.getId());
            }
        }
    }

    private void reloadItems(int position) {
        allItemArr = new ArrayList<>();
        allItemArr = (ArrayList<Item>) databaseManager.listItems();
        itemAdapter = new ItemAdapter(activity, allItemArr);
        lvItem.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogForText(allItemArr.get(position), position);
            }
        });
//        this.lvItem.setOnItemClickListener(new C01901());


        if (position > 0) {
            this.lvItem.setSelection(position);
        }
    }

    public void dialogForText(Item item, int pos) {
        final Dialog dialog = new Dialog(activity, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_edit_text_mode);
        dialog.setCancelable(false);
        final EditText edtTextEdit = (EditText) dialog.findViewById(R.id.edtTextEdit);
        Button btnCancelEdit = (Button) dialog.findViewById(R.id.btnCancelEdit);
        Button btnOkEdit = (Button) dialog.findViewById(R.id.btnOkEdit);
        if (item.getUserText() != null) {
            edtTextEdit.setText(item.getUserText());
        }
        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final Item item2 = item;
        final int i = pos;
        btnOkEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (item2 != null) {
                    item2.setUserText(edtTextEdit.getText().toString());
                    MainActivity.this.databaseManager.updateItem(item2);
                    MainActivity.this.reloadItems(i);
                }
                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(activity, dialog);
    }

}
