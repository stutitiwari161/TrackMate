package com.example.trackmate

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private val listContacts:ArrayList<ContactModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
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

        val recycler = requireView().findViewById<RecyclerView>(R.id.recycler_member)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter=adapter


        Log.d("FetchContact89","fetchContact: about to start")

        Log.d("FetchContact89","fetchContact: started ${listContacts.size}")
        val inviteAdapter=InviteAdapter(listContacts)
        Log.d("FetchContact89","fetchContact: now ended")

        CoroutineScope(Dispatchers.IO).launch {

            Log.d("FetchContact89","fetchContact: Coroutine about to start")
            listContacts.addAll(fetchContacts())
            insertDatabaseContacts(listContacts)
            withContext(Dispatchers.Main){
                inviteAdapter.notifyDataSetChanged()
            }

            Log.d("FetchContact89","fetchContact: coroutine ended ${listContacts.size} ")
        }



        val inviteRecycler=requireView().findViewById<RecyclerView>(R.id.recycler_invite)
        inviteRecycler.layoutManager=
            LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        inviteRecycler.adapter=inviteAdapter
    }

    private suspend fun insertDatabaseContacts(listContacts: ArrayList<ContactModel>) {
        val database = TrackMateDatabase.getDatabase(requireContext())
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