package com.tpv.android.ui.salesagent.home.enrollment.customerinfo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.livinglifetechway.k4kotlin.onClick
import com.tpv.android.R
import com.tpv.android.model.network.ProgramDetailItem
import com.tpv.android.model.network.RequestCustomer
import com.tpv.android.model.network.TmpDataItem
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
import kotlinx.android.synthetic.main.customer_info_adapter.view.*

 open class CustomerInformationAdapter(val list:ArrayList<TmpDataItem>,val listener:Onitemclicklistener):RecyclerView.Adapter<CustomerInformationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.customer_info_adapter, parent, false))

    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface Onitemclicklistener{
          fun onclick(position: Int)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val programlist= list.get(position).programDetail as List<ProgramDetailItem>
        val program_adapter=UtilityProgramAdapter(programlist)
        holder.person_name.text= list.get(position).firstName+" "+list.get(position)?.middleInitial+" "+list.get(position)?.lastName
        if(list.get(position).accountNumber!=null){
            holder.account_number.text=list.get(position).accountNumber
            holder.account_number.visibility=View.VISIBLE
            holder.textaccountnumber.visibility=View.VISIBLE
        }else{
            holder.account_number.visibility=View.GONE
            holder.textaccountnumber.visibility=View.GONE
        }
//        if(list.get(position).serviceAddress1!=null){
//            holder.service_address.visibility=View.GONE
//            holder.text_serviceaddress.visibility=View.GONE
//        }else{
//            holder.service_address.visibility=View.VISIBLE
//            holder.text_serviceaddress.visibility=View.VISIBLE
//
//
//        }
        if(list.get(position).serviceAddress2.equals("")){
            var address:String=list.get(position)?.serviceAddress1+", "+list.get(position)?.city+", "+list.get(position)?.county+", "+list.get(position)?.state+", "+list.get(position)?.zipcode+", "+list.get(position)?.serviceCountry
            holder.service_address.text=address

        }else{
            holder.service_address.text= list.get(position)?.serviceAddress1+", "+list.get(position)?.serviceAddress2+", "+list.get(position)?.city+", "+list.get(position)?.county+", "+list.get(position)?.state+", "+list.get(position)?.zipcode+", "+list.get(position)?.serviceCountry
        }
        if(list.get(position).billingAddress2.equals("")){
            var address:String=list.get(position)?.billingAddress1+", "+list.get(position)?.billingCity+", "+list.get(position)?.billingCounty+", "+list.get(position)?.billingState+", "+list.get(position)?.billingZipcode+", "+list.get(position)?.billingCountry
            holder.billing_address.text=address

        }else{
            holder.billing_address.text= list.get(position)?.billingAddress1+", "+list.get(position)?.billingAddress2+", "+list.get(position)?.billingCity+", "+list.get(position)?.billingCounty+", "+list.get(position)?.billingState+", "+list.get(position)?.billingZipcode+", "+list.get(position)?.billingCountry

        }
        holder.plan_recyclerview.adapter=program_adapter
        if(list.get(position).parentId==0){
            holder.image_click.visibility=View.GONE

        }else{
                holder.image_click.visibility=View.VISIBLE
            }

        holder.image_click.onClick {
            listener.onclick(position)

        }


    }
     class ViewHolder(view: View):RecyclerView.ViewHolder(view){
         val person_name=view.textNameValue
         val account_number=view.textAccountNumberValue
         val service_address=view.textServiceAddressValue
         val billing_address=view.textBillingAddressValue
         val plan_recyclerview=view.program_details
         val textaccountnumber=view.textAccountNumber
         val image_click=view.delete_enrollement
         val text_serviceaddress=view.textServiceAddress


    }

      fun removeItem(dataItem: TmpDataItem){
        val position=list.indexOf(dataItem)
         if(position>-1){
             list.removeAt(position)
             notifyItemRemoved(position)
         }
         notifyDataSetChanged()
     }


}