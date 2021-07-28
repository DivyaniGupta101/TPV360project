package com.tpv.android.ui.salesagent.home.enrollment.programs

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.k4kotlin.hide
import com.livinglifetechway.k4kotlin.onClick
import com.livinglifetechway.k4kotlin.onLongClick
import com.tpv.android.R
import com.tpv.android.model.network.*
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
import kotlinx.android.synthetic.main.item_programs_adapter.view.*
import kotlinx.android.synthetic.main.new_utiltiy_program_adapter.view.*


open class ElectricUtilityAdapter(val list:ArrayList<ElectricdataItem>, val listener:Onitemclicklistener):RecyclerView.Adapter<ElectricUtilityAdapter.ViewHolder>() {

    val context: Context? =null
    var last_selected=-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.new_utiltiy_program_adapter, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface Onitemclicklistener{
          fun onclick(position: Int)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val customlist=list.get(position).electricCustomFields  as ArrayList<ElectricCustomFieldsItem>
        holder.programname.text=list.get(position).programName
        holder.premisetypename.text=list.get(position).premiseTypeName
        holder.programcode.text=list.get(position).programCode
        holder.program_rate.text=list.get(position).rate+"("+list.get(position).unitOfMeasureName+")"
        holder.item_term.text=list.get(position).term
        holder.msf.text=list.get(position).monthlysf
        holder.etf.text=list.get(position).earlyterminationfee
        holder.custom_adapter.layoutManager=LinearLayoutManager(context)
        val adapter=ElectricCustomFieldAdapter(customlist)
        holder.custom_adapter.adapter=adapter
        holder.main_container.setOnClickListener(View.OnClickListener {
            if(DynamicFormFragment.back_pressed==true ){
                Log.e("onmainclick","onmainclick")
                if(last_selected!=position){
                    last_selected=position
                    list[last_selected].is_selected="true"
                    ElectricListingFragment.positon=last_selected
                    notifyDataSetChanged()
                    listener.onclick(position)

                }
            }else{
                if(last_selected!=position){
                    last_selected=position
                    list[last_selected].is_selected="true"
                    ElectricListingFragment.positon=last_selected
                    notifyDataSetChanged()
                    listener.onclick(position)

                }
            }


        })


        if(last_selected!=position){
            holder.main_container.background=null
            holder.image_enroll.hide()
        }else{
            holder.image_enroll.show()
            holder.main_container.setBackgroundResource(R.drawable.bg_rectangle_border)

        }
        if(list.get(position).is_selected.equals("true")){
            if(ElectricListingFragment.positon!=position){
                holder.main_container.background=null
                holder.image_enroll.hide()
            }else{
                holder.image_enroll.show()
                holder.main_container.setBackgroundResource(R.drawable.bg_rectangle_border)

            }
        }

        if(customlist.isEmpty()){
            holder.divider_view.visibility=View.GONE
        }else{
            holder.divider_view.visibility=View.VISIBLE
        }



    }
     class ViewHolder(view: View):RecyclerView.ViewHolder(view){
         val  programname=view.program_names
         val premisetypename=view.premise_type_names
         val programcode=view.program_codes
         val program_rate=view.program_rates
         val item_term=view.item_terms
         val msf=view.monthlymnsfs
         val etf=view.terminate_feess
         val custom_adapter=view.custom_fields
         var main_container=view.constraintLayout
         var image_enroll=view.imageEnrolls
         var divider_view=view.dividerView


    }




}


