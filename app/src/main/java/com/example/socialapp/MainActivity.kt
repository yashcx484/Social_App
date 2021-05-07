package com.example.socialapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.daos.PostDao
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.Post
import com.example.socialapp.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_in.*


class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var postDao: PostDao
    private lateinit var adapter: PostAdapter
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }





    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener{
            val intent=Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }

        setUpRecyclerView()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()


        mGoogleSignInClient= GoogleSignIn.getClient(this, gso)


        logoutButton.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                auth.signOut()
                val intent= Intent(this, SignInActivity::class.java)
                startActivity(intent)

            }
        }


    }


    private fun setUpRecyclerView() {
        postDao= PostDao()
        val postsCollection=postDao.postCollections
        val query=postsCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions=FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

    adapter= PostAdapter(recyclerViewOptions, this)

        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
    postDao.updateLikes(postId)

    }

    override fun onAddclicked(postId: String, commentText: String) {
        TODO("Not yet implemented")
    }


}