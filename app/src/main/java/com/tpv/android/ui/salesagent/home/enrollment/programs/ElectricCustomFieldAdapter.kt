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
import kotlinx.android.synthetic.main.custom_field_adapter.view.*
import kotlinx.android.synthetic.main.customer_info_adapter.view.*
import kotlinx.android.synthetic.main.item_programs_adapter.view.*

open class ElectricCustomFieldAdapter(val list:ArrayList<ElectricCustomFieldsItem>):RecyclerView.Adapter<ElectricCustomFieldAdapter.ViewHolder>() {

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