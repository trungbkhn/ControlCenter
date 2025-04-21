package com.tapbi.spark.controlcenter.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import timber.log.Timber

abstract class BaseDialog : DialogFragment() {

    private val mViewSparseArray = SparseArray<View>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (getLayoutId() != 0) {
            initStyle()
            val view: View = inflater.inflate(getLayoutId(), container, false)
            initView(view)
            return view
        }
        return container
    }

    private var mView: View? = null
    private fun initView(view: View) {
        mView = view
    }


    fun <V> findView(id: Int): View {
        var view = mViewSparseArray[id]
        if (view == null) {
            view = mView?.findViewById(id)
            mViewSparseArray.put(id, view)
        }
        return view
    }

    fun getDialogView(): View {
        return mView!!
    }


    override fun onResume() {
        val win = dialog!!.window!!
        dialog?.window?.decorView?.setPadding(0, 0, 0, 0)
        val lp = win.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        win.attributes = lp
        win.setBackgroundDrawable(null)
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }


    abstract fun initData()

    abstract fun initStyle()

    abstract fun getLayoutId(): Int


    fun setDialogCancelable(isCancelable: Boolean) {
        dialog?.setCancelable(isCancelable)
    }

    @SuppressLint("CommitTransaction")
    fun showDialog(manger: FragmentManager, tag :String) {
        val transaction = manger.beginTransaction()
        val dialog = manger.findFragmentByTag(tag)
        if (dialog != null) {
            transaction.remove(dialog)
        }
        transaction.addToBackStack(null)
        Timber.e("NVQ showDialog $tag")
        show(manger, tag)
    }


    fun hintDialog(manger: FragmentManager,tag: String) {
        val prev = manger.findFragmentByTag(tag)
        if (prev != null) {
            (prev as DialogFragment).dismiss()
        }
    }
}