package com.tapbi.spark.controlcenter.service.observer

import android.annotation.SuppressLint
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.utils.PermissionUtils
import org.greenrobot.eventbus.EventBus

class ContactObserver : ContentObserver(Handler(Looper.getMainLooper())) {
    @SuppressLint("Range")
    override fun onChange(selfChange: Boolean, uri: Uri?) {
        // this is NOT UI thread, this is a BACKGROUND thread
        //Timber.e("hachung : onChange   ${PermissionUtils.checkPermissionReadContact()}")
//        if (!PermissionUtils.checkPermissionReadContact()) return
//        val itemPeopleUpdate = App.ins.contactReposition?.uriContactUpdate
//        itemPeopleUpdate?.let {itemPeople ->
//            itemPeople.name?.let {
//                EventBus.getDefault()
//                    .post(
//                        MessageEvent(
//                            Constant.CONTACT_CHANGE,
//                            itemPeople.contactId,
//                            it,
//                            itemPeople.phone,
//                            itemPeople.image
//                        )
//                    )
//            }
//
//        }
//
//
//        App.ins.contactReposition?.idContactDelete.apply {
//            EventBus.getDefault()
//                .post(this?.let { MessageEvent(Constant.CONTACT_DELETE, it) })
//        }

    }

}