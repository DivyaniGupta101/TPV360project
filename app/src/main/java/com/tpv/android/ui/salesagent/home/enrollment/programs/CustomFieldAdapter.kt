package com.tpv.android.ui.salesagent.home.enrollment.programs

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.livinglifetechway.k4kotlin.onClick
import com.tpv.android.R
import com.tpv.android.model.network.*
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment


open class CustomFieldAdapter(val list:ArrayList<GasCustomFieldsItem>):RecyclerView.Adapter<CustomFieldAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_field_adapter, parent, false))

    }

    override fun getItemCount(): Int {
        return list.size
    }
  

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.label.text=list.get(position).label+": "
        holder.name.text=list.get(position).value

    }
     class ViewHolder(view: View):RecyclerView.ViewHolder(view){
         val label=view.label_txt
         val name=view.name_txt


    }




}