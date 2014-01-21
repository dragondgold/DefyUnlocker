package com.defyunlock.andres;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import com.stericson.RootTools.RootTools;

public class MainActivity extends ActionBarActivity implements OnUnlocked{

    private static TextView tvData;
    private static ContentResolver resolver;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resolver = getContentResolver();
        context = this;

        findViewById(R.id.bLiberar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("DefyUnlocker", "Testeando acceso root");

                // Compruebo si tengo acceso root, de no tenerlo alerto al usuario
                if (!RootTools.isAccessGiven()) {
                    new AlertDialog.Builder(context)
                            .setTitle("No hay acceso root")
                            .setMessage("No se pudo obtener accesso root, por favor verifique" +
                                    "que su teléfono este rooteado")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }

                tvData = (TextView) findViewById(R.id.tvData);
                tvData.setText("Acceso root obtenido...\n");
                tvData.append("Liberando...\n");

                Unlocker.unlock(resolver, context, (OnUnlocked) context);
            }
        });

    }

    @Override
    public void OnPhoneUnlocked() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvData.append("Liberado!");
            }
        });
    }
}
