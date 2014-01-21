package com.defyunlock.andres;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by andres on 1/21/14.
 */
public class Unlocker {

    private static int n;
    private static Command command;
    private static ContentResolver resolver;
    private static Context context;
    private static OnUnlocked onUnlocked;

    public static void unlock(ContentResolver mResolver, Context mContext, OnUnlocked mOnUnlocked){

        onUnlocked = mOnUnlocked;
        resolver = mResolver;
        context = mContext;

        // Entro en modo avión
        Log.i("Unlocker", "Entrando en modo avion");
        Settings.System.putInt(resolver, Settings.System.AIRPLANE_MODE_ON, 1);
        context.sendBroadcast(new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED).putExtra("state", 1));
        n = 0;

        Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Ver http://forum.xda-developers.com/showthread.php?t=1483297 para mas informacion de los comandos
                command = new Command(0, "bp_ptc") {
                    @Override
                    public void commandOutput(int i, String s) {
                        Log.i("Unlocker", "Command Output: " + s);

                        if(s.contains("write 4")) ++n;
                        if(n == 2){
                            Log.i("Unlocker", "Terminado");

                            command.terminate("Finish");
                            try{ RootTools.getShell(true).close(); }
                            catch(RootDeniedException e) { e.printStackTrace(); }
                            catch(IOException e) { e.printStackTrace(); }
                            catch(TimeoutException e) { e.printStackTrace(); }
                        }
                    }

                    @Override
                    public void commandTerminated(int i, String s) {
                        Log.i("Unlocker", "Command Terminated: " + s);
                        // Salgo de modo avión
                        Settings.System.putInt(resolver, Settings.System.AIRPLANE_MODE_ON, 0);
                        context.sendBroadcast(new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED).putExtra("state", 0));

                        onUnlocked.OnPhoneUnlocked();
                    }

                    @Override
                    public void commandCompleted(int i, int i2) {
                        Log.i("Unlocker", "Command completed");
                    }
                };
                Log.i("Unlocker", "Ejecutando comando");
                // Ejecuto el comando como root
                RootTools.default_Command_Timeout = 10000;
                try{ RootTools.getShell(true).add(command); }
                catch(RootDeniedException e) { e.printStackTrace(); }
                catch(IOException e) { e.printStackTrace(); }
                catch(TimeoutException e) { e.printStackTrace(); }
            }
        });
        mThread.start();
    }

}
