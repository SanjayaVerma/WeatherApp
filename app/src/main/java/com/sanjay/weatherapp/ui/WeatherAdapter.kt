package com.sanjay.weatherapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanjay.weatherapp.R
import com.sanjay.weatherapp.data.model.DataList
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class WeatherAdapter @Inject constructor(
    dataList: List<DataList>
) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    private var itemList: List<DataList> = dataList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item, parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(itemList[position])


    override fun getItemCount(): Int {
        return itemList?.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: DataList) {
            itemView.tv_date.text = model.dtTxt
            //Convert  Fahrenheit to Celsius
            val celsius = ((model.main.temp - 32) * 5) / 9
            val fahrenheitToCelsius: String = String.format(
                "%.2f",
                celsius
            )
            itemView.tv_temp.text = "$fahrenheitToCelsius °C"
            itemView.tv_description.text = model.weather[0].description
        }
    }
}