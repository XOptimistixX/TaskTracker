package com.jeffsieu.tasktracker.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.jeffsieu.tasktracker.Channel;
import com.jeffsieu.tasktracker.ChannelOverviewFragment;
import com.jeffsieu.tasktracker.DashboardFragment;
import com.jeffsieu.tasktracker.R;
import com.jeffsieu.tasktracker.TagsFragment;
import com.jeffsieu.tasktracker.Task;
import com.jeffsieu.tasktracker.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private static String TAG = MainActivity.class.getName();
    public static final int CREATE_NEW_TASK = 0;
    public static final int EDIT_TASK = 1;
    public static MainActivity sReference;

    public static List<Task> tasks = new ArrayList<>();
    public static List<String> taskKeys = new ArrayList<>();
    public static List<Channel> channels = new ArrayList<>();
    public static List<String> channelKeys = new ArrayList<>();
    private static final DashboardFragment dashboardFragment = new DashboardFragment();
    private static final TagsFragment tagsFragment = new TagsFragment();
    private static final ChannelOverviewFragment channelOverviewFragment = ChannelOverviewFragment.newInstance();

    private static State currentState = State.DASHBOARD;

    private GoogleApiClient mGoogleApiClient;
    private NavigationView navigationView;
    private FloatingActionButton mFab;
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;

    private static String UID;

    private SubMenu tagSubMenu;

    public enum State {
        DASHBOARD, CHANNELS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sReference = this;
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Log", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Log", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Product Sans.ttf");
        toolbarTitle.setTypeface(face);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentState) {
                    case DASHBOARD:
                        Intent intent = new Intent(getApplicationContext(), EditTaskActivity.class);
                        startActivityForResult(intent, CREATE_NEW_TASK);
                        break;
                    case CHANNELS:
                        showAddChannelDialog();
                        break;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        if (savedInstanceState == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            final OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

            if (pendingResult.isDone()) {
                // There's immediate result available.
                Log.d(TAG, "Immediate");
                silentSignIn(pendingResult.get());
            } else {
                // There's no immediate result ready, displays some progress indicator and waits for the
                // async callback.
                pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult result) {
                        Log.d(TAG, "Not immediate");
                        silentSignIn(result);
                    }
                });
            }
        }
        tagSubMenu = navigationView.getMenu().findItem(R.id.nav_group_tags).getSubMenu();

        channels.add(new Channel("Testing", "1002i", getUID()));
        channelKeys.add("iov");
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void silentSignIn(GoogleSignInResult result) {
        DatabaseUtils.handleSignInResult(result, this);
        //Log.d(TAG, result.getSignInAccount().getDisplayName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CREATE_NEW_TASK) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Task task = data.getParcelableExtra("task");
                DatabaseUtils.pushTask(task);
            }
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == DatabaseUtils.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            DatabaseUtils.handleSignInResult(result, this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            currentState = State.DASHBOARD;
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_fragment, dashboardFragment).commit();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_channels) {
            currentState = State.CHANNELS;
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_fragment, channelOverviewFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void addTag(String name) {
        MenuItem tag = tagSubMenu.add(name);
        tag.setIcon(R.drawable.label);
        tag.setCheckable(true);
    }

    public void showFab() {
        mFab.show();
    }

    public DashboardFragment getDashboardFragment() {
        return dashboardFragment;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    private void showAddChannelDialog() {
        LayoutInflater li = getLayoutInflater();
        View promptsView = li.inflate(R.layout.dialog_add_channel, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.add_channel_name);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static String getUID() {
        return UID;
    }

    public static void setUID(String UID) {
        MainActivity.UID = UID;
    }
}
