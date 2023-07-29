package com.example.trackmate

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.trackmate.databinding.ItemInviteMailBinding
import java.text.FieldPosition

class InviteMailAdapter(
    private val listInvites:List<String>,
    private val onActionClick: OnActionClick
    ):
RecyclerView.Adapter<InviteMailAdapter.ViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType:Int
    ):InviteMailAdapter.ViewHolder{
        val inflater=LayoutInflater.from(parent.context)
        val item=ItemInviteMailBinding.inflate(inflater,parent,false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: InviteMailAdapter.ViewHolder,position: Int) {
        val item = listInvites[position]
        holder.name.text=item

        holder.accept.setOnClickListener {
            onActionClick.onAcceptClick(item)
        }

        holder.deny.setOnClickListener {
            onActionClick.onDenyClick(item)
        }


    }

    override fun getItemCount(): Int {
        return listInvites.size
    }
    class ViewHolder(private  val item:ItemInviteMailBinding):RecyclerView.ViewHolder(item.root){
        val name=item.mail
        val accept=item.accept
        val deny= item.deny
    }

    interface OnActionClick{
        fun onAcceptClick(mail:String)
        fun onDenyClick(mail:String)

    }

}