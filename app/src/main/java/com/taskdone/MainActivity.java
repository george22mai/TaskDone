package com.taskdone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.taskdone.Fragments.AllTasksFragment;
import com.taskdone.Fragments.FolderFragment;
import com.taskdone.Utils.FolderModel;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new AllTasksFragment());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddTaskActivity.class));
                overridePendingTransition(R.anim.to_add_anim_start, R.anim.to_add_anim_end);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setTitle("TaskDone");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new AllTasksFragment()).commit();

        fillMenuWithFolders(navigationView);
        infosFromFacebook(navigationView);
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

        if (id == 9000) {
            findViewById(R.id.fab).setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("TaskDone");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new AllTasksFragment()).commit();
        } else if (id == 991) {
            resetDb();
        } else if (id == 992) {
            shareApp();
        } else if (id == 993) {
            sendFeedback();
        } else if (id == 994) {
            logout();
        } else {
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                FolderFragment folderFragment = new FolderFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", item.getTitle().toString());
                folderFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, folderFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void fillMenuWithFolders(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        final SubMenu subMenu = menu.addSubMenu("Folders");
        subMenu.add(0, 9000, 1, "All tasks").setIcon(R.drawable.ic_folder);
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("folders").addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    subMenu.add(0, 1, 1, ((FolderModel) dataSnapshot.getValue(FolderModel.class)).getTitle()).setIcon(R.drawable.ic_folder);
                }
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
        SubMenu general = menu.addSubMenu("General");
        general.add(0, 991, 1, "Reset your tasks").setIcon(R.drawable.ic_reset);
        general.add(0, 992, 1, "Share application for a friend").setIcon(R.drawable.ic_share);
        general.add(0, 993, 1, "Send feedback").setIcon(R.drawable.ic_feedback);
        general.add(0, 994, 1, "Disconnect").setIcon(R.drawable.ic_power);
    }

    void infosFromFacebook(NavigationView navigationView) {
        TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        TextView email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        ProfilePictureView picture = (ProfilePictureView) navigationView.getHeaderView(0).findViewById(R.id.profilePic);
        SharedPreferences sharedPreferences = getSharedPreferences("general", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getString("username", "TaskDone") == "TaskDone") {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                public void onCompleted(JSONObject object, GraphResponse response) {
                    if (response != null) {
                        try {
                            editor.putString("id", object.getString("id"));
                            editor.putString("username", object.getString("name"));
                            editor.putString("email", object.getString("email"));
                            editor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Bundle param = new Bundle();
            param.putString(GraphRequest.FIELDS_PARAM, "id,name,email");
            request.setParameters(param);
            request.executeAsync();
        }
        if (sharedPreferences.getString("username", "TaskDone") == "TaskDone") {
            name.setVisibility(4);
        } else {
            name.setText(sharedPreferences.getString("username", "TaskDone"));
        }
        if (sharedPreferences.getString("email", "contact@taskdone.com") == "contact@taskdone.com") {
            email.setVisibility(4);
        } else {
            email.setText(sharedPreferences.getString("email", "contact@taskdone.com"));
        }
        String userID = sharedPreferences.getString("id", null);
        if (userID != null) {
            picture.setProfileId(userID);
        }
    }

    void resetDb() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.fragment), getResources().getString(R.string.resetSecure), 0).setActionTextColor(getResources().getColor(R.color.white)).setAction("YES, RESET", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("list").setValue(null);
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tasksCompleted").removeValue();
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("folders").removeValue();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction("android.intent.action.SEND");
        sendIntent.putExtra("android.intent.extra.TEXT", "Hey check out TaskDone at: https://play.google.com/store/apps/details?id=com.taskdone");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    void sendFeedback() {
        Intent test = new Intent("android.intent.action.VIEW");
        test.setData(Uri.parse("mailto:?subject=TaskDone Feedback&to=Email@me.com"));
        startActivity(test);
    }

    void logout() {
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
        finish();

    }
}
