/*
 * Copyright (C) 2012 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.itmp.rootexample;

import top.itmp.rootcommands.RootCommands;
import top.itmp.rootcommands.Shell;
import top.itmp.rootcommands.Toolbox;
import top.itmp.rootcommands.command.Command;
import top.itmp.rootcommands.command.SimpleExecutableCommand;
import top.itmp.rootcommands.command.SimpleCommand;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class BaseActivity extends Activity {
    public static final String TAG = "Demo";
    AlertDialog.Builder builder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        builder = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null);

        // enable debug logging
        RootCommands.DEBUG = true;
    }

    private class MyCommand extends Command {
        private static final String LINE = "hosts";
        boolean found = false;

        public MyCommand() {
            super("ls -la /system/etc/");
        }

        public boolean isFound() {
            return found;
        }

        @Override
        public void output(int id, String line) {
            if (line.contains(LINE)) {
                Log.d(TAG, "Found it!");
                found = true;
            }
        }

        @Override
        public void afterExecution(int id, int exitCode) {
        }

    }

    public void commandsTestOnClick(View view) {
        try {
            // start root shell
            Shell shell = Shell.startRootShell();

            // simple commands
            SimpleCommand command0 = new SimpleCommand("echo this is a command",
                    "echo this is another command");
            SimpleCommand command1 = new SimpleCommand("toolbox ls");
            SimpleCommand command2 = new SimpleCommand("ls -la /system/etc/hosts");

            shell.add(command0).waitForFinish();
            shell.add(command1).waitForFinish();
            shell.add(command2).waitForFinish();

            Log.d(TAG, "Output of command2: " + command2.getOutput());
            Log.d(TAG, "Exit code of command2: " + command2.getExitCode());

            builder.setTitle("Command Test")
                    .setMessage("command0: " + command0.getOutput()
                            + "\ncommand1: " + command1.getOutput()
                            + "\ncommand2: " + command2.getOutput());
            builder.create()
                    .show();
            // custom command classes:
            MyCommand myCommand = new MyCommand();
            shell.add(myCommand).waitForFinish();

            Log.d(TAG, "myCommand.isFound(): " + myCommand.isFound());

            // close root shell
            shell.close();
        } catch (Exception e) {
            Log.e(TAG, "Exception!", e);
        }
    }

    public void toolboxTestOnClick(View view) {
        try {
            Shell shell = Shell.startRootShell();

            Toolbox tb = new Toolbox(shell);

            if (tb.isRootAccessGiven()) {
                Log.d(TAG, "Root access given!");
            } else {
                Log.d(TAG, "No root access!");
            }

            Log.d(TAG, tb.getFilePermissions("/system/etc/hosts"));

            builder.setTitle("Toolbox Test")
                    .setMessage(tb.isRootAccessGiven() ? "Root access given!\n" + tb.getFilePermissions("/system/etc/hosts") : "No root access!" );
            builder.create()
                    .show();

            shell.close();
        } catch (Exception e) {
            Log.e(TAG, "Exception!", e);
        }
    }

    public void binariesTestOnClick(View view) {
        try {
            SimpleExecutableCommand binaryCommand;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                // Run the file which was created using APP_PLATFORM := android-16
                binaryCommand = new SimpleExecutableCommand(this, "hello_world", "");
                } else {
                // Run the file which was created using APP_PLATFORM := android-9
                binaryCommand = new SimpleExecutableCommand(this, "hello_world_nopie", "");
                }
            //SimpleExecutableCommand binaryCommand = new SimpleExecutableCommand(this, "hello_world", "");

            // started as normal shell without root, but you can also start your binaries on a root
            // shell if you need more privileges!
            Shell shell = Shell.startShell();

            shell.add(binaryCommand).waitForFinish();

            Log.v("binaryCommand", binaryCommand.getOutput() + " ");
            builder.setTitle("Binaries Test")
                    .setMessage(binaryCommand.getOutput());
            builder.create().show();
            Toolbox tb = new Toolbox(shell);
            if (tb.killAllExecutable("hello_world")) {
                Log.d(TAG, "Hello World daemon killed!");
            } else {
                Log.d(TAG, "Killing failed!");
            }

            shell.close();
        } catch (Exception e) {
            Log.e(TAG, "Exception!", e);
        }
    }

}