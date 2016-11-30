package com.enterchange.enterchange;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class Generics
{
    private Context mContext;

    public Generics(Context context)
    {
        mContext=context;
    }

    public SQLiteDatabase AbroBaseDatos() {
        // boolean huboError = false;
        String motivoError = "";
        try {
            ManejadorDeBaseDeDatos accesoBD = new ManejadorDeBaseDeDatos(mContext, "baseDeDatos", null,1);
            return accesoBD.getWritableDatabase();
        } catch (Exception error) {

            motivoError = error.getMessage();
            Toast.makeText(mContext, motivoError, Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    private static ConnectivityManager manager;

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}
