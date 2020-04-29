package com.e.martineetalk.view

import com.e.martineetalk.R
import com.e.martineetalk.model.ChatMessage
import com.e.martineetalk.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<GroupieViewHolder>() {
    var chatPartnerUser: User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.latest_textview_latestmessage.text = chatMessage.text

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.latest_textview_username.text = chatPartnerUser?.username

                val targetImageView = viewHolder.itemView.latest_imageView
                Picasso.get().load(chatPartnerUser?.profileImageUri).into(targetImageView)
            }

        })



    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}