package com.example.myapplication

import android.animation.ObjectAnimator
import android.util.Log
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.util.isEmpty
import androidx.core.util.isNotEmpty
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.placeholder.PlaceholderContent
import kotlin.properties.Delegates

class ListItemAdapter(private val values: List<PlaceholderContent.PlaceholderItem>
) : RecyclerView.Adapter<ListItemAdapter.ItemViewHolder>() {

    private lateinit var itemClick: OnItemClick
    private var selectedIndex: Int = -1
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var isActive: Boolean = false
    private var activateAnimation: Boolean = false


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

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.item_number).text = values[position].id
            findViewById<TextView>(R.id.content).text = values[position].content
        }

        holder.itemView.findViewById<CardView>(R.id.list_item).setOnClickListener {
            itemClick.onItemClick(values[position], position)
        }

        holder.itemView.findViewById<CardView>(R.id.list_item).setOnLongClickListener {
            itemClick.onLongPress(values[position], position)
            true
        }

        toggleIcon(holder, position)
    }

    override fun getItemCount(): Int {
        return values.size
    }


    private fun itemTransition(holder: ItemViewHolder){
        val animator = ObjectAnimator.ofFloat(holder.itemView.findViewById(R.id.list_item), View.TRANSLATION_X, 150f)
        animator.start()
    }

    private fun itemTransitionBack(holder: ItemViewHolder){
        val animator = ObjectAnimator.ofFloat(holder.itemView.findViewById(R.id.list_item), View.TRANSLATION_X, 0f)
        animator.start()
    }


    private fun toggleIcon(holder: ItemViewHolder, position: Int){
        val checkBox = holder.itemView.findViewById<RadioButton>(R.id.is_selected)
        if(selectedItems.get(position, false)){
            checkBox.isGone = false
            checkBox.isChecked = true
        }
        else{
            checkBox.isGone = true
            checkBox.isChecked = false
        }

        /*
        The use of isActive to make sure that when one item is selected ,checkboxes of other items is set to be visible.
         */
        if(isActive)
            checkBox.isGone = false


        if(activateAnimation){
            itemTransition(holder)
        }
        else
            itemTransitionBack(holder)


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
        activateAnimation = selectedItems.isNotEmpty()

        if(selectedItemCount() == 1 || selectedItemCount() == 0){
            notifyDataSetChanged()
        }

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

