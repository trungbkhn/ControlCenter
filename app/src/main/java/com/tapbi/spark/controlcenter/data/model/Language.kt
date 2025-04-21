package com.tapbi.spark.controlcenter.data.model

class Language {
    var codeLocale: String = ""
    var nameLanguage: String = ""

    constructor(codeLocale: String) {
        this.codeLocale = codeLocale
    }

    constructor(codeLocale: String, nameLanguage: String) {
        this.codeLocale = codeLocale
        this.nameLanguage = nameLanguage
    }
}