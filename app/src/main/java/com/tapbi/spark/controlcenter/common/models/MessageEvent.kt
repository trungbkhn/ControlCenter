package com.tapbi.spark.controlcenter.common.models

class MessageEvent {
    var typeEvent: Int
    var stringValue: String = ""
    var intValue: Int = 0


    //item people
    var contactID: String = ""
    var name: String = ""
    var phone: String = ""
    var image: String = ""
    var uri: String = ""


    var isAutoStart = false;
    var isAccessibility = false;




    constructor(typeEvent: Int, stringValue: String) {
        this.typeEvent = typeEvent
        this.stringValue = stringValue
    }




    constructor(typeEvent: Int, stringValue: String, intValue: Int) {
        this.typeEvent = typeEvent
        this.stringValue = stringValue
        this.intValue = intValue
    }

    constructor(typeEvent: Int, intValue: Int) {
        this.typeEvent = typeEvent
        this.intValue = intValue
    }

    constructor(typeEvent: Int, autoStart: Boolean) {
        this.typeEvent = typeEvent
        this.isAutoStart = autoStart
    }



    constructor(typeEvent: Int) {
        this.typeEvent = typeEvent
    }

    constructor(typeEvent: Int, contactID: String, name: String, phone: String, image: String) {
        this.typeEvent = typeEvent
        this.contactID = contactID
        this.name = name
        this.phone = phone
        this.image = image
    }


}