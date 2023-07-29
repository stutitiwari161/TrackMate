package com.example.trackmate

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackmate.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    lateinit var inviteAdapter :InviteAdapter
    lateinit var mContext:Context
    private val listContacts:ArrayList<ContactModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext= context
    }

    lateinit var binding:FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listMembers= listOf<MemberModel>(
            MemberModel("Stuti",
                "ED-84 Moti Jheel Sachivalaya colony Aishbagh, Lucknow",
                 "30%",
                "210"),
            MemberModel("Ayushi",
                "ED-84 Moti Jheel Sachivalaya colony Aishbagh, Lucknow",
                 "80%",
                "111"),
            MemberModel("Asita",
                 "H-24, B Block Rajajajipuram, Lucknow.",
                 "12%",
                "134"),
            MemberModel("Kushagra",
                "ED-84 Moti Jheel Sachivalaya colony Aishbagh, Lucknow",
                 "92%",
                "200"),
            MemberModel("Apoorv",
                "H-62, niti khand Indirapuram, Gaziabaad, UP",
                "64%", "112"
            ),
        )

        val adapter =MemberAdapter(listMembers)


        binding.recyclerMember.layoutManager = LinearLayoutManager(mContext)
        binding.recyclerMember.adapter=adapter


        Log.d("FetchContact89","fetchContact: about to start")

        Log.d("FetchContact89","fetchContact: started ${listContacts.size}")
        inviteAdapter=InviteAdapter(listContacts)
        fetchDatabaseContacts()
        Log.d("FetchContact89","fetchContact: now ended")

        CoroutineScope(Dispatchers.IO).launch {
            Log.d("FetchContact89","fetchContact: Coroutine about to start")

            insertDatabaseContacts(fetchContacts())

            Log.d("FetchContact89","fetchContact: coroutine ended ${listContacts.size} ")
        }




        binding.recyclerInvite.layoutManager=
            LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerInvite.adapter=inviteAdapter



        binding.iconThreeDots.setOnClickListener {

            SharedPref.putBoolean(prefConstants.IS_USER_LOGGED_IN,false)
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun fetchDatabaseContacts() {
        val database = TrackMateDatabase.getDatabase(mContext)

       database.contactDao().getAllContacts().observe(viewLifecycleOwner){

           Log.d("fetchContacts()","fetch contacts from database")
           listContacts.clear()
           listContacts.addAll(it)

           inviteAdapter.notifyDataSetChanged()
       }
    }

    private suspend fun insertDatabaseContacts(listContacts: ArrayList<ContactModel>) {
        val database = TrackMateDatabase.getDatabase(mContext)
        database.contactDao().insertAll(listContacts)
    }


    @SuppressLint("Range")
    private fun fetchContacts(): ArrayList<ContactModel> {
        Log.d("fetchContact89", "fetchContacts: Start")

        val cr= requireActivity().contentResolver
        val cursor=cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null)
        val listContacts:ArrayList<ContactModel> = ArrayList()

       if(cursor!=null && cursor.count > 0){

           while(cursor!=null && cursor.moveToNext()){
               val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
               val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
               val hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

               if(hasPhoneNumber > 0){
                  val pCur= cr.query(
                      CommonDataKinds.Phone.CONTENT_URI,
                      null,
                      CommonDataKinds.Phone.CONTACT_ID+"=?",
                      arrayOf(id),
                      ""
                  )

                   if(pCur!=null && pCur.count>0){
                       while (pCur!=null && pCur.moveToNext()){
                           val phoneNum=
                               pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                           listContacts.add(ContactModel(name,phoneNum))
                       }
                       pCur.close()
                   }
               }

           }
           if(cursor!=null){
               cursor.close()
           }


       }
        Log.d("FetchContacts89", "fetchContacts: Ends")
        return listContacts
    }

    companion object {

        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}