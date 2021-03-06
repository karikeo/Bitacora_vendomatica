package com.shevchenko.staffapp;
/*
This class uploads the completed task data in android service mode to the web server.
This class is for the auto sincronize in background after the user complete the tasks.
 */
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.DetailCounter;
import com.shevchenko.staffapp.Model.LogEvent;
import com.shevchenko.staffapp.Model.LogFile;
import com.shevchenko.staffapp.Model.PendingTasks;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.db.DBManager;
import com.shevchenko.staffapp.net.NetworkManager;

import java.io.File;
import java.util.ArrayList;

public class UploadService extends Service {

    public UploadService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            new UploadThread().start();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    //This is the thread for posting completed task info.
    class UploadThread extends Thread {

        @Override
        public void run() {
            postAllPendingTask();
            postAllTinPendingTask();
            postAllDetailCounters();
            postAllLogEvent();
            postAllLogFile();
            Common.getInstance().isUpload = false;
            mHandler_pendingtasks.sendEmptyMessage(0);

        }
    }
    //This function gets the log file data from the android sqlite db at first.
    //then in for instruction each log file is uploaded to the server.
    //NetworkManager postLogFile function is called.
    //after upload the log file, the log file from the android sqlite db and sdcard is deleted.
    private int postAllLogFile(){
        ArrayList<LogFile> logs = DBManager.getManager().getLogFiles();
        int sum = 0;
        for (int i = 0; i < logs.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postLogFile(logs.get(i));
            if (bRet1) {
                File f = new File(logs.get(i).getFilePath());
                if(f.exists())
                    f.delete();

                DBManager.getManager().deleteLogFile(logs.get(i));
            } else
                return 0;
        }
        return 1;
    }
    //This function gets the log event data from the android sqlite db at first.
    //then in for instruction each log event is uploaded to the server.
    //NetworkManager postLogevent function is called.
    //after upload the log event, the log event from the android sqlite db is deleted.
    private int postAllLogEvent(){
        ArrayList<LogEvent> logs = DBManager.getManager().getLogEvents(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < logs.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postLogEvent(logs.get(i));
            if (bRet1)
                DBManager.getManager().deleteLogEvent(Common.getInstance().getLoginUser().getUserId(), logs.get(i).datetime);
            else
                return 0;
        }
        return 1;
    }
    //This function gets the Pendingtask data from the android sqlite db at first.
    //then in for instruction each Pendingtask is uploaded to the server.
    //NetworkManager Pendingtask function is called.
    //after upload the Pendingtask, the Pendingtask from the android sqlite db is deleted.
    private int postAllPendingTask() {
        ArrayList<PendingTasks> tasks = DBManager.getManager().getPendingTask(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {
            String[] arrPhotos = new String[]{"", "", "", "", ""};
            int nCurIndex = 0;
            if (!tasks.get(i).file1.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file1;
                nCurIndex++;
            }
            if (!tasks.get(i).file2.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file2;
                nCurIndex++;
            }
            if (!tasks.get(i).file3.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file3;
                nCurIndex++;
            }
            if (!tasks.get(i).file4.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file4;
                nCurIndex++;
            }
            if (!tasks.get(i).file5.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file5;
                nCurIndex++;
            }

            Boolean bRet1 = NetworkManager.getManager().postTask(tasks.get(i).taskid, tasks.get(i).date, tasks.get(i).tasktype, tasks.get(i).RutaAbastecimiento, tasks.get(i).TaskBusinessKey, tasks.get(i).Customer, tasks.get(i).Adress, tasks.get(i).LocationDesc, tasks.get(i).Model, tasks.get(i).latitude, tasks.get(i).longitude, tasks.get(i).epv, tasks.get(i).logLatitude, tasks.get(i).logLongitude, tasks.get(i).ActionDate, tasks.get(i).MachineType, tasks.get(i).Signature, tasks.get(i).NumeroGuia, tasks.get(i).Aux_valor1, tasks.get(i).Aux_valor2, tasks.get(i).Aux_valor3, tasks.get(i).Aux_valor4, tasks.get(i).Aux_valor5, tasks.get(i).Glosa, arrPhotos, nCurIndex, tasks.get(i).Completed, tasks.get(i).Comment, tasks.get(i).Aux_valor6, tasks.get(i).QuantityResumen, tasks.get(i).comment_notcap);
            if (bRet1)
                DBManager.getManager().deletePendingTask(Common.getInstance().getLoginUser().getUserId(), tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }
    //This function gets the TinTask data from the android sqlite db at first.
    //then in for instruction each TinTask is uploaded to the server.
    //NetworkManager TinTask function is called.
    //after upload the TinTask, the TinTask from the android sqlite db is deleted.
    private int postAllTinPendingTask() {
        ArrayList<TinTask> tasks = DBManager.getManager().getTinPendingTask(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postTinTask(tasks.get(i));
            if (bRet1)
                DBManager.getManager().deletePendingTinTask(Common.getInstance().getLoginUser().getUserId(), tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }
    //This function gets the DetailCounter data from the android sqlite db at first.
    //then in for instruction each DetailCounter is uploaded to the server.
    //NetworkManager DetailCounter function is called.
    //after upload the DetailCounter, the DetailCounter from the android sqlite db is deleted.
    private int postAllDetailCounters() {
        ArrayList<DetailCounter> tasks = DBManager.getManager().getDetailCounter();
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postDetailCounter(tasks.get(i));
            if (bRet1)
                DBManager.getManager().deleteDetailTask(tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }
    private Handler mHandler_pendingtasks = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            //loadTasks();
            Common.getInstance().isUpload = false;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
