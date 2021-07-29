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
            ItemTouchHelper.RIGHT) {
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
                Log.d("TEST_TAG", "Selecyio")
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

                recyclerView.setOnTouchListener { v, event ->
                    swipeBack = event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL
                    false
                }


                super.onChildDraw(c, recyclerView, viewHolder,
                    dX / 4  , dY , actionState, isCurrentlyActive)

                c.clipRect(0f, viewHolder.itemView.top.toFloat(),
                    dX / 4, viewHolder.itemView.bottom.toFloat())

                if(dX < c.width / 3){
                    c.drawColor(Color.GRAY)
                }
                else{
                    showBottomSheet = true
                    c.drawColor(Color.RED)
                }

                val textMargin = resources.getDimension(R.dimen.text_margin)
                    .roundToInt()
                trashBinIcon.bounds = Rect(
                    textMargin,
                    viewHolder.itemView.top + textMargin,
                    textMargin + trashBinIcon.intrinsicWidth,
                    viewHolder.itemView.top + trashBinIcon.intrinsicHeight
                            + textMargin
                )
                trashBinIcon.draw(c)

            }

            override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
                return if (swipeBack)
                    0
                else
                    super.convertToAbsoluteDirection(flags, layoutDirection)
            }


        }

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS)
                val myHelper = ItemTouchHelper(myCallback)
                myHelper.attachToRecyclerView(this)
            }
        }




        return view
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