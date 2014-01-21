package com.defyunlock.andres;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

/**
 * Created by andres on 1/21/14.
 */
public class BootReceiver extends BroadcastReceiver implements OnUnlocked{

    private static Context mContext;
    private static boolean rootAccess = true;
    private static Handler mHandler;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("DefyUnlocker-Boot", "Liberando en arranque");
        mContext = context;
        mHandler = new Handler();

        Toast.makeText(mContext, "Defy Unlocker - Liberando", Toast.LENGTH_LONG).show();

        // Compruebo si tengo acceso root, de no tenerlo alerto al usuario
        if(!RootTools.isAccessGiven()){
            rootAccess = false;
            new AlertDialog.Builder(mContext)
                    .setTitle("No hay acceso root")
                    .setMessage("No se pudo obtener accesso root, por favor verifique" +
                            "que su teléfono este rooteado")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        }
        if(rootAccess)
            Unlocker.unlock(context.getContentResolver(), context, this);
    }

    @Override
    public void OnPhoneUnlocked() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "Defy Unlocker - Liberado!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
