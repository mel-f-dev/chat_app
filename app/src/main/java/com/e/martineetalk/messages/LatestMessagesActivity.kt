package com.e.martineetalk.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.e.martineetalk.R
import com.e.martineetalk.model.ChatMessage
import com.e.martineetalk.model.User
import com.e.martineetalk.registerlogin.LoginActivity
import com.e.martineetalk.registerlogin.RegisterActivity
import com.e.martineetalk.view.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }

    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        latest_recyclerview.adapter = adapter
        latest_recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // set item click listener on your adapter
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "123")
            val intent = Intent(this, ChatLogActivity::class.java)

            // we are missing the chat partner user
            // for safe casting
            val row = item as LatestMessageRow
            row.chatPartnerUser

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

//        setupDummyRows()
        listenForLatestMessages()

        fetchCurrentUser()

        verifyUserIsLoggedIn()
    }

    val latestMessageMap = HashMap<String, ChatMessage> ()

    private fun listenForLatestMessages() {
        val fromId = auth.uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object :ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()

            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessageMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
//    private fun setupDummyRows() {
//
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//    }

    private fun fetchCurrentUser() {
        val uid = auth.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.profileImageUri}")
            }
        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = auth.uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_log_out -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
