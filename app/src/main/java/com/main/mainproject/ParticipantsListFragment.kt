package com.main.mainproject

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.main.mainproject.com.main.mainproject.adapters.AdapterParticipants
import com.main.mainproject.com.main.mainproject.models.ModelParticipants
import org.w3c.dom.Text


class ParticipantsListFragment(private val roomCode:String,private val roomName:String) : Fragment() {
    val myUid = Firebase.auth.currentUser!!.uid
    private lateinit var mContext: Context
    private lateinit var participantsRv:RecyclerView
    private lateinit var participantsArrayList:ArrayList<ModelParticipants>
    private lateinit var theadmin:String
    private var storage = Firebase.storage
    var storageRef = storage.reference
    private lateinit var roomNameTv:TextView
    private lateinit var roomCodeTv:TextView
    private lateinit var database: DatabaseReference
    private lateinit var adapterParticipants: AdapterParticipants
    companion object {
        private const val TAG = "PARTICIPANTS_LIST_FRAGMENT"
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        database = Firebase.database.reference

        return inflater.inflate(R.layout.fragment_participants_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        participantsRv = view.findViewById(R.id.participantsRv)
        roomNameTv = view.findViewById(R.id.roomNameTv)
        roomCodeTv = view.findViewById(R.id.roomCodeTv)
        roomNameTv.text = "$roomName"
        roomCodeTv.text = "$roomCode"
        loadParticipants()
    }

    @SuppressLint("LongLogTag")
    private fun loadParticipants() {
        Log.d(TAG, "loadParticipants: ")
        participantsArrayList = ArrayList()

        database.child("Rooms").child(roomCode).addListenerForSingleValueEvent(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(ParticipantsListFragment.TAG, "onDataChange: $snapshot")
                    var admin = snapshot.value as Map<String, Any>
                    Log.d(ParticipantsListFragment.TAG, "onDataChange: ${admin?.get("admin")}")
                    theadmin = admin.get("admin").toString()
                    adapterParticipants = AdapterParticipants(mContext,participantsArrayList,theadmin)
                    participantsRv.adapter = adapterParticipants
                    database.child("Participants").child(roomCode).addListenerForSingleValueEvent(
                        object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val readData = snapshot.value as Map<String,Any>
                                for(each in readData){
                                    val modelParticipants = ModelParticipants(each.value.toString(),each.key)
                                    participantsArrayList.add(modelParticipants)
                                    adapterParticipants.notifyItemInserted(participantsArrayList.size)

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        }
                    )

                }

                override fun onCancelled(error: DatabaseError) {

                }


            }
        )

    }


}