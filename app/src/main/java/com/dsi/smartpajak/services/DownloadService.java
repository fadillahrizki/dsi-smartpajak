package com.dsi.smartpajak.services;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class DownloadService extends IntentService {
    private static final String FILE_NAME = "com.dsi.smartpajak_download_file_name";
    private static final String FILE_URL = "com.dsi.smartpajak_download_file_url";

    public DownloadService() {
        super("DownloadService");
    }

    public static Intent getDownloadService(final Context callingClassContext, final String fileName, final String fileURL) {
        return new Intent(callingClassContext, DownloadService.class)
                .putExtra(FILE_NAME, fileName)
                .putExtra(FILE_URL, fileURL);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String fileName = intent.getStringExtra(FILE_NAME);
        String fileURL = intent.getStringExtra(FILE_URL);
        startDownload(fileName, fileURL);
    }

    private void startDownload(String fileName, String fileURL) {
        Uri uri = Uri.parse(fileURL);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(fileName);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/SMARTPAJAK/" + fileName);
        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
    }
}