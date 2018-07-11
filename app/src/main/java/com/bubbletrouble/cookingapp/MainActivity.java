package com.bubbletrouble.cookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.bubbletrouble.cookingapp.adapter.GroupAdapter;
import com.bubbletrouble.cookingapp.authentication.LoginActivity;
import com.bubbletrouble.cookingapp.authentication.SettingsActivity;
import com.bubbletrouble.cookingapp.model.FoodCategory;
import com.bubbletrouble.cookingapp.model.Group;
import com.bubbletrouble.cookingapp.model.Meal;
import com.bubbletrouble.cookingapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewClickListener{

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private TextView navHeaderName, navHeaderEmail;
    private NavigationView navigationView;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    List<Group> groups = new ArrayList<>();
    List<User> users = new ArrayList<>();
    List<FoodCategory>foodCategories = new ArrayList<>();
    List<Meal>meals= new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView=findViewById(R.id.rv_main);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        meals.add(new Meal());
        meals.add(new Meal());
        users.add(new User("ronald", "ronald.goedeke@outlook.com"));
        users.add(new User("mario", "mario.emberger@outlook.com"));
        groups.add(new Group(users, foodCategories));
        groups.add(new Group(users, foodCategories));

        mAdapter = new GroupAdapter(groups, this);
        recyclerView.setAdapter(mAdapter);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeaderName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        navHeaderEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_namesub);

        //Check if User is not null
        if(user != null)
        {
            navHeaderName.setText(user.getDisplayName());
            navHeaderEmail.setText(user.getEmail());
        }

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Fetch values from you database child and set it to the specific view object.
                if(auth.getCurrentUser() != null) {
                    if(dataSnapshot.child("users").child(auth.getCurrentUser().getUid()).child("username").getValue() != null)
                    {
                        navHeaderName.setText(dataSnapshot.child("users").child(auth.getCurrentUser().getUid()).child("username").getValue().toString());
                    }else
                        navHeaderName.setText(null);
                    if(dataSnapshot.child("users").child(auth.getCurrentUser().getUid()).child("email").getValue() != null) {
                        navHeaderEmail.setText(dataSnapshot.child("users").child(auth.getCurrentUser().getUid()).child("email").getValue().toString());
                    }
                    else
                        navHeaderEmail.setText(null);
                }
                else
                {
                    navHeaderName.setText(null);
                    navHeaderEmail.setText(null);
                }


                //  String link =dataSnapshot.child("profile_picture").getValue().toString();
              //  Picasso.with(getBaseContext()).load(link).into(mImageView);
            }

            //SIMPLE BRO. HAVE FUN IN ANDROID <3 GOOD LUCK

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            navHeaderName.setText(user.getDisplayName());
        }

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClickBtn(View v)
    {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        Toast.makeText(this, position + "", Toast.LENGTH_SHORT).show();
    }
}
