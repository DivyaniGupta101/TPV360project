package com.tpv.android.ui.salesagent.home.enrollment.customerinfo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpv.android.R
import com.tpv.android.model.network.ProgramDetailItem
import com.tpv.android.model.network.TmpDataItem
import kotlinx.android.synthetic.main.customer_info_adapter.view.*
import kotlinx.android.synthetic.main.item_programs_adapter.view.*

open class UtilityProgramAdapter(val programDetailsItem:List<ProgramDetailItem>):RecyclerView.Adapter<UtilityProgramAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_programs_adapter, parent, false))

    }

    override fun getItemCount(): Int {
        return programDetailsItem.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.utility_name.text= programDetailsItem.get(position).utility
        holder.program_name.text = programDetailsItem.get(position).utilityName
        holder.premise_type.text=programDetailsItem.get(position).customerType
        holder.code_Value.text=programDetailsItem.get(position).code
        holder.program_rate.text=programDetailsItem.get(position).rate
        holder.item_term.text= programDetailsItem.get(position).term
        holder.msf.text=programDetailsItem.get(position).msf
        holder.etf.text= programDetailsItem.get(position).etf



    }
     class ViewHolder(view: View):RecyclerView.ViewHolder(view){
         val program_name=view.program_name
         val premise_type=view.premise_type_name
         val code_Value=view.program_code
         val program_rate=view.program_rate
         val item_term=view.item_term
         val msf=view.monthlymnsf
         val etf=view.terminate_fees
         val utility_name=view.utility_name


     }
}