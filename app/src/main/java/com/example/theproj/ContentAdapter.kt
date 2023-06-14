package com.example.theproj

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ContentAdapter(private val conTentList : MutableList<list_View_Item>) : BaseAdapter() {
    override fun getCount(): Int {
        return conTentList.size
    }

    override fun getItem(position: Int): Any {
      return conTentList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {

        var convertView = view

        var tv_title = convertView?.findViewById<TextView>(R.id.tv_title)
        var tv_fac = convertView?.findViewById<TextView>(R.id.tv_fac)


        if (convertView == null) {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.park_list_item, parent, false)
        }

        val item: list_View_Item = conTentList[position]
        tv_title?.text = item.title
        tv_fac?.text = item.fac

        return convertView!!
    }


}