package com.jeffsieu.tasktracker.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeffsieu.tasktracker.Channel;
import com.jeffsieu.tasktracker.Task;
import com.jeffsieu.tasktracker.activity.MainActivity;

import java.util.Calendar;

/**
 * Created by Jeff on 4/7/2016.
 */
public class DatabaseUtils {

    private static final String TAG = "Database Utils";

    public static final int RC_SIGN_IN = 1;
    private static DatabaseReference sTasksDatabase = FirebaseDatabase.getInstance().getReference().child("test");
    private static DatabaseReference sChannelsDatabase = FirebaseDatabase.getInstance().getReference().child("test");
    private static DatabaseReference sUsersDatabase = FirebaseDatabase.getInstance().getReference().child("test");

    public static void setUser(String name) {
        MainActivity.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        sTasksDatabase = FirebaseDatabase.getInstance().getReference().child("tasks").child(name);
        sUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(name);
        sChannelsDatabase = FirebaseDatabase.getInstance().getReference().child("channels");

        importTasksFromDatabase();
        importChannelsFromDatabase();
    }

    public static void handleSignInResult(GoogleSignInResult result, Activity activity) {
        Log.d("Sign in", "handleSignInResult:" + result.isSuccess() + result.getSignInAccount());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account, activity);
        } else {
            manualSignIn(activity);
        }
    }

    private static void firebaseAuthWithGoogle(GoogleSignInAccount acct, final Activity activity) {
        Log.d("Log", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        MainActivity.sReference.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        Log.d("Log", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Log", "signInWithCredential", task.getException());
                            Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                    // ...

                });

        DatabaseUtils.setUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private static void manualSignIn(final Activity activity) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(MainActivity.sReference.getGoogleApiClient());
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static void importTasksFromDatabase() {
        DatabaseReference ref = sUsersDatabase.child("tasks");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "child added");
                final String taskKey = (String) dataSnapshot.getValue();
                final int index = MainActivity.taskKeys.indexOf(taskKey);
                sTasksDatabase.child(taskKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Task task = getTask(dataSnapshot);
                        if (index > -1) {
                            Task oldTask = MainActivity.tasks.get(index);
                            oldTask.importFromTask(task);
                            MainActivity.sReference.getDashboardFragment().notifyItemChanged(index);
                        } else {
                            MainActivity.tasks.add(task);
                            MainActivity.taskKeys.add(taskKey);
                            MainActivity.sReference.getDashboardFragment().updateDashboard();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("child changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                System.out.println("child removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void pushTask(Task task) {
        DatabaseReference reference = sUsersDatabase.child("tasks").push();
        setTask(task, sTasksDatabase.child(reference.getKey()));
    }

    public static void updateTask(Task task, String taskKey) {
        DatabaseReference reference = sUsersDatabase.child("tasks").child(taskKey);
        setTask(task, sTasksDatabase.child(taskKey));
    }

    public static void setTask(Task task, DatabaseReference reference) {
        reference.child("name").setValue(task.getName());
        Calendar calendar = task.getDate();
        reference.child("date").setValue(calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.YEAR));
    }

    public static Task getTask(DataSnapshot dataSnapshot) {
        Task task = new Task();
        String name = (String) dataSnapshot.child("name").getValue();
        task.setName(name);
        String date = (String) dataSnapshot.child("date").getValue();
        int day = 1, month = 1, year = 2000;
        if (date != null) {
            String[] values = date.split(" ");
            if (values.length == 3) {
                day = Integer.parseInt(values[0]);
                month = Integer.parseInt(values[1]);
                year = Integer.parseInt(values[2]);
                task.setDate(year, month, day);
            }
        }
        return task;
    }

    public static void removeTask(String key) {
        sTasksDatabase.child(key).removeValue();
        sUsersDatabase.child("tasks").child(key).removeValue();
    }

    public static void setChannel(Channel channel, DatabaseReference ref) {
        ref.child("name").setValue(channel.getName());
        ref.child("key").setValue(channel.getKey());
        DatabaseReference listRef = ref.child("admins");
        for (String s : channel.getAdmins()) {
            DatabaseReference adminRef = listRef.push();
            adminRef.setValue(s);
        }
    }

    public static Channel getChannel(DataSnapshot dataSnapshot) {
        String name = (String) dataSnapshot.child("name").getValue();
        String key = (String) dataSnapshot.child("key").getValue();
        Channel channel = new Channel(name, key);

        DataSnapshot listRef = dataSnapshot.child("admins");
        for (DataSnapshot child : listRef.getChildren()) {
            channel.addAdmin((String) child.getValue());
        }

        return channel;
    }

    public static void pushChannel(Channel channel) {
        DatabaseReference ref = sChannelsDatabase.push();
        channel.setKey(ref.getKey());
        setChannel(channel, ref);
        sUsersDatabase.child("channels").push().setValue(channel.getKey());
    }

    public static void updateChannel(Channel channel) {
        DatabaseReference ref = sChannelsDatabase.child(channel.getKey());
        setChannel(channel, ref);
    }

    public static void importChannelsFromDatabase() {
        DatabaseReference userChannelsRef = sUsersDatabase.child("channels");
        userChannelsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String channelKey = (String) dataSnapshot.getValue();
                MainActivity.channelKeys.add(channelKey);

                DatabaseReference channelRef = sChannelsDatabase.child(channelKey);
                channelRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Channel channel = getChannel(dataSnapshot);
                        //Toast.makeText(MainActivity.sReference, channel.getName(), Toast.LENGTH_LONG).show();
                        MainActivity.channels.add(channel);

                        try {
                            MainActivity.sReference.getChannelOverviewFragment().updateChannels();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int index = MainActivity.channelKeys.indexOf(dataSnapshot.getKey());
                Channel channel = MainActivity.channels.get(index);
                Channel newChannel = getChannel(dataSnapshot);
                channel.setChannel(newChannel);
                MainActivity.sReference.getChannelOverviewFragment().updateChannels();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}