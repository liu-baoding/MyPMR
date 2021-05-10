package com.example.todolist

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_choix_list.view.*
import kotlinx.android.synthetic.main.list.view.*

class newList(context: Context, text: String) : androidx.appcompat.widget.AppCompatTextView(context) {
    companion object{
        var num = 0
        var last_list = 0
    }

    val text_size = 24F

//    var default_params = default_list.layoutParams as ConstraintLayout.LayoutParams

//    var parent_id = default_params.startToStart

    val newParams = ConstraintLayout.LayoutParams(
//        ConstraintLayout.LayoutParams.WRAP_CONTENT,
        0,
        ConstraintLayout.LayoutParams.WRAP_CONTENT
    )

    public fun get_params(): ConstraintLayout.LayoutParams {
        newParams.setMargins(8,8,8,8)
        newParams.startToStart = this.parent
        if (newList.num==1){
            default_params.topToBottom = liste3.id
            newList.last_list = this.id
        }else{
            default_params.topToBottom = last_list
        }
        return default_params
    }

    init {
        newList.num += 1
        this.setTextSize(text_size)
        this.setText(text)
        this.id = generateViewId()
        newList.last_list = this.id

    }

}