package com.example.myapplication

import android.animation.ObjectAnimator
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.util.isNotEmpty
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.placeholder.PlaceholderContent

class ListItemAdapter(private val values: List<PlaceholderContent.PlaceholderItem>
) : RecyclerView.Adapter<ListItemAdapter.ItemViewHolder>() {

    private lateinit var itemClick: OnItemClick
    private var selectedIndex: Int = -1
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var isActive: Boolean = false

    fun setItemClick(itemClick: OnItemClick) {
        this.itemClick = itemClick
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListItemAdapter.ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_item,
            parent,
            false
        )
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListItemAdapter.ItemViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.item_number).text = values[position].id
            findViewById<TextView>(R.id.content).text = values[position].content
        }

        holder.itemView.setOnClickListener {
            itemClick.onItemClick(values[position], position)
        }

        holder.itemView.setOnLongClickListener {
            itemClick.onLongPress(values[position], position)
            true
        }
        toggleIcon(holder, position)
    }

    override fun getItemCount(): Int {
        return values.size
    }



    fun toggleIcon(holder: ItemViewHolder, position: Int){
        val checkBox = holder.itemView.findViewById<RadioButton>(R.id.is_selected)
        if(selectedItems.get(position, false)){
            checkBox.isGone = false
            checkBox.isChecked = true
        }
        else{
            checkBox.isGone = true
            checkBox.isChecked = false
        }
        if(isActive) checkBox.isGone = false
        if(selectedIndex == position) selectedIndex = - 1
    }

    fun selectedItemCount() = selectedItems.size()

    fun toggleSelection(position: Int){

        selectedIndex = position
        if (selectedItems.get(position, false)){
            selectedItems.delete(position)
        }else {
            selectedItems.put(position, true)
        }
        notifyItemChanged(position)

        isActive = selectedItems.isNotEmpty()
        notifyDataSetChanged()
    }

    fun clearSelection(){
        selectedItems.clear()
        notifyDataSetChanged()
    }

    interface OnItemClick {
        fun onItemClick(item: PlaceholderContent.PlaceholderItem, position: Int)
        fun onLongPress(item: PlaceholderContent.PlaceholderItem, position: Int)
    }

    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    }

}

