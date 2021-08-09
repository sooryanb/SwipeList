package com.example.myapplication

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.myapplication.placeholder.PlaceholderContent
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.roundToInt

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {

    private var columnCount = 1
    private lateinit var alertDialog: AlertDialog
    private lateinit var adapter: ListItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)


        val trashBinIcon = resources.getDrawable(R.drawable.ic_baseline_delete_24, null)
        var recyclerViewH: RecyclerView ?= null
        var currentViewHolder: RecyclerView.ViewHolder ?= null
        var canvas: Canvas ?= null
        var showBottomSheet = false

        var swipeBack = false

        val myCallback = object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }



            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.d("TEST_TAG", "Swi[ed")
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                if (showBottomSheet)  showBottomSheet(view.context)
                showBottomSheet = false
                Log.d("TEST_TAG", "Cleared")
            }



            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

//                recyclerView.setOnTouchListener { v, event ->
////                    swipeBack = event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL
////                    Log.d("INSIDESWIPE", "$swipeBack")
////                    false
////                }






                super.onChildDraw(c, recyclerView, viewHolder,
                     dX / 4 ,  dY / 6, actionState, isCurrentlyActive)


                c.clipRect(viewHolder.itemView.right.toFloat() + dX / 3, viewHolder.itemView.top.toFloat(),
                    viewHolder.itemView.right.toFloat(), viewHolder.itemView.bottom.toFloat())

                /*
                * swipeBack should only be true when the item is swiped, ie when dX changes from 0 to -ve value.
                * If swipe back is always true then the swiping wont happen so at not dragged state it should be false.
                * Using this method rather onTouchListener method is because onTouch won't work with onClick and/or OnLongPress.
                * */
                swipeBack = false
                if (-dX > 0) swipeBack = true


                if(-dX < c.width / 4){
                    c.drawColor(Color.GRAY)
                    Log.d("DXV", "$dX")
                }
                else{
                    Log.d("TWID", "${-dX}, ${c.width}, ${c.width / 4} ")
                    showBottomSheet = true
                    c.drawColor(Color.RED)
                }

                val height = viewHolder.itemView.bottom.toFloat() - viewHolder.itemView.top.toFloat()
                val width = (height / 3).toInt()

                val textMargin = resources.getDimension(R.dimen.text_margin)
                    .roundToInt()
                trashBinIcon.bounds = Rect(
                    viewHolder.itemView.right - 2 * width - 16,
                    viewHolder.itemView.top + width - 8 ,
                    viewHolder.itemView.right - width,
                    viewHolder.itemView.bottom - width + 8
                )
                trashBinIcon.draw(c)

            }

            override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
                Log.d("SWIPE_BACK", "$swipeBack")
                return if (swipeBack)
                    0
                else
                    super.convertToAbsoluteDirection(flags, layoutDirection)
            }


        }

        adapter = ListItemAdapter(PlaceholderContent.ITEMS)
        val recyclerViewList = view.findViewById<RecyclerView>(R.id.list)
        recyclerViewList.adapter = adapter
        recyclerViewList.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        val myHelper = ItemTouchHelper(myCallback)
        myHelper.attachToRecyclerView(recyclerViewList)

        adapter.setItemClick(object : ListItemAdapter.OnItemClick{
            override fun onItemClick(
                item: PlaceholderContent.PlaceholderItem,
                position: Int
            ) {
                if(adapter.selectedItemCount() > 0)
                    toggleSelection(position)


            }

            override fun onLongPress(
                item: PlaceholderContent.PlaceholderItem,
                position: Int
            ) {
                toggleSelection(position)
            }

        })


        return view
    }

    private fun toggleSelection(position: Int){
        adapter.toggleSelection(position)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    private fun showCustomDialog() {
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_feedback_view, null)

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(dialogView.context)
        dialogBuilder.setOnDismissListener(object : DialogInterface.OnDismissListener {
            override fun onDismiss(arg0: DialogInterface) {

            }
        })
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.Animation_AppCompat_Dialog
        alertDialog.show()
    }

    private fun showBottomSheet(context: Context) {
        val dialog = BottomSheetDialog(context)
        val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
        val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }



}