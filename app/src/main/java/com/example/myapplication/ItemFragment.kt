package com.example.myapplication

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

            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
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


                recyclerViewH = recyclerView
                currentViewHolder = viewHolder
                canvas = c

                super.onChildDraw(c, recyclerView, viewHolder,
                    dX / 2  , dY , actionState, isCurrentlyActive)

                c.clipRect(0f, viewHolder.itemView.top.toFloat(),
                    dX / 2, viewHolder.itemView.bottom.toFloat())

                if(dX < c.width / 3)
                    c.drawColor(Color.GRAY)
                else{
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

        alertDialog = dialogBuilder.create();
        alertDialog.window!!.attributes.windowAnimations = R.style.Animation_AppCompat_Dialog
        alertDialog.show()
    }



}