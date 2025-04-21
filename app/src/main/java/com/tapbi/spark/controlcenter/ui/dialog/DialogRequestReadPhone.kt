package com.tapbi.spark.controlcenter.ui.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.eventbus.EventCheckControlState
import com.tapbi.spark.controlcenter.utils.PermissionUtils
import org.greenrobot.eventbus.EventBus

class DialogRequestReadPhone : DialogRequestPermissionWriteSetting() {

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        super.onCreatedView(view, savedInstanceState)
        setText(
            getString(R.string.You_need_to_enable_permissions_to_use_this_feature),
            getString(R.string.go_to_setting),
            ""
        )
        setDialogListener(object : ClickListener {
            override fun onClickOke() {
                context?.let {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", it.packageName, null)
                    }
                    it.startActivity(intent)
                }

            }

            override fun onClickCancel() {

            }

            override fun onBackPress() {
                dismiss()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        if (PermissionUtils.checkPermissionService()) {
            EventBus.getDefault().post(EventCheckControlState())
            dismiss()
        }

    }
}