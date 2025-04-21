package com.tapbi.spark.controlcenter.data.db.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.data.model.ItemPeople
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel

@Dao
interface FocusDao {
    //noti
    @Insert
    fun insertNotification(model: NotyModel)

    @Query("DELETE FROM NotyModel")
    fun deleteNotification()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListFocusDefault(list: List<FocusIOS>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFocus(focusIOS: FocusIOS)

    @Delete
    fun deleteFocus(focusIOS: FocusIOS)

    @Query("DELETE from FocusIOS where name=:name")
    fun deleteFocusByName(name: String)


    @Query("UPDATE  FocusIOS SET colorFocus=:colorFocus where name=:name")
    fun updateColorFocusIos(colorFocus: String, name: String)

    @Query("Select * from FocusIOS")
    fun getListFocus(): List<FocusIOS>

    @Query("UPDATE  FocusIOS SET modeAllowPeople=:modeAllowedPeople where name=:name")
    fun updateStartItemFocusIosAutoTime(modeAllowedPeople: Int, name: String)

    @Query("UPDATE FocusIOS SET name=:name,imageLink=:imageLink,colorFocus=:color WHERE name=:oldName ")
    fun updateFocusIos(name: String, imageLink: String, color: String, oldName: String)

    @Query("UPDATE  FocusIOS SET isStartAutoAppOpen=:isStartAutoAppOpen , isStartCurrent=:isStartCurrent , isStartAutoLocation=:isStartAutoLocation , isStartAutoTime=:isStartAutoTime where name=:name")
    fun updateStartItemFocusIos(
        isStartAutoAppOpen: Boolean,
        isStartCurrent: Boolean,
        isStartAutoLocation: Boolean,
        isStartAutoTime: Boolean,
        name: String,
    )

    @Query("Update FocusIOS Set isStartCurrent=:isStartCurrent , isStartAutoTime=:isAutoTime , isStartAutoLocation=:isAutoLocation , isStartAutoAppOpen=:isAutoApp where name=:name")
    fun turnOffFocus(
        name: String,
        isStartCurrent: Boolean,
        isAutoTime: Boolean,
        isAutoLocation: Boolean,
        isAutoApp: Boolean,
    )

    @Query("Select * from FOCUSIOS where isStartCurrent=:isOn or isStartAutoTime=:isOn or isStartAutoLocation=:isOn or isStartAutoAppOpen=:isOn")
    fun getFocusOn(isOn: Boolean): FocusIOS

    @Query("Select * from FOCUSIOS where isStartCurrent=:isOn")
    fun getFocusOnStart(isOn: Boolean): FocusIOS


    @Query("SELECT * FROM FocusIOS WHERE id=:id")
    fun getFocusById(id: Int): FocusIOS

    @Query("SELECT * FROM FocusIOS WHERE name=:name")
    fun getFocusByName(name: String): FocusIOS

    //item people
    @Query("SELECT * FROM ItemPeople where nameFocus=:nameFocus")
    fun getAllItemAllowedPeople(nameFocus: String): List<ItemPeople>


    @get:Query("SELECT * FROM ItemPeople")
    val allItemAllowedPeople: List<ItemPeople>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPeople(itemPeople: ItemPeople)

    @Query("DELETE FROM ItemPeople where nameFocus=:name")
    fun deleteAllItemAllowed(name: String)

    @Query("DELETE FROM ItemPeople where contactId=:contactId")
    fun deleteItemAllowedPeople(contactId: String)

    @Query("DELETE FROM ItemPeople where nameFocus=:nameFocus")
    fun deleteItemAllowedPeopleByNameFocus(nameFocus: String)

    @Query("UPDATE  ItemPeople SET nameFocus=:nameFocus where contactId=:contactID and nameFocus=:oldName")
    fun updatePeopleChange(contactID: String, nameFocus: String, oldName: String)

    @Query("UPDATE  ItemPeople SET name=:name, phone=:phone, image=:image where contactId=:contactID")
    fun updatePeople(contactID: String, name: String, phone: String, image: String)

    //item apps
    @Query("SELECT * FROM ItemApp where nameFocus=:nameFocus")
    fun getAllItemAllowedApp(nameFocus: String): List<ItemApp>


    @Query("UPDATE  ItemApp SET nameFocus=:name where packageName=:packageName and nameFocus=:oldName")
    fun updateAppName(packageName: String, name: String, oldName: String)


    @Query("SELECT * FROM ItemApp where isStart=:isStart")
    fun getAllItemAllowedApp(isStart: Boolean): List<ItemApp>

    @Query("DELETE FROM ItemApp where nameFocus=:name")
    fun deleteAllItemAllowedApp(name: String)

    @Query("DELETE FROM ItemApp where  packageName=:packageName")
    fun deleteItemAllowedApp(packageName: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListApp(appList: List<ItemApp>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApp(itemApp: ItemApp)

    //item auto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListItemAutomationFocus(turnOnList: List<ItemTurnOn>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemAutomationFocus(itemTurnOn: ItemTurnOn)

    @Query("SELECT * FROM ItemTurnOn where nameFocus=:name and isStartFocus=:isStart")
    fun getAllItemAutomationFocus(name: String, isStart: Boolean): List<ItemTurnOn>

    @Query("SELECT * FROM ItemTurnOn where nameFocus=:name")
    fun getAllItemAutomationFocus(name: String): List<ItemTurnOn>

    @Query("SELECT * FROM ItemTurnOn where nameFocus=:name and isStart=:isStart")
    fun getAllItemAutomationFocusOn(name: String, isStart: Boolean): List<ItemTurnOn>

    @Query("SELECT * FROM ItemTurnOn where nameFocus=:name and isStartFocus=:isStart and typeEvent=:event")
    fun getAllItemAutomationFocusByEvent(
        name: String,
        isStart: Boolean,
        event: String,
    ): List<ItemTurnOn>

    @Query("SELECT * FROM ItemTurnOn where typeEvent=:typeEvent")
    fun getAllAuto(typeEvent: String): List<ItemTurnOn>

    @Query("SELECT * FROM ItemTurnOn where isStart=:isStart and typeEvent=:typeEvent")
    fun getListTimeFocus(isStart: Boolean, typeEvent: String): List<ItemTurnOn>

    @Query("SELECT * FROM ItemTurnOn where isStart=:isStart and typeEvent=:typeEvent and nameFocus=:name")
    fun getListAutoAppGame(isStart: Boolean, typeEvent: String, name: String): List<ItemTurnOn>

    @Query("SELECT * FROM ItemTurnOn where isStart=:isStart and typeEvent=:typeEvent and nameFocus!=:name")
    fun getListAutoAppNotGame(
        isStart: Boolean,
        typeEvent: String,
        name: String,
    ): List<ItemTurnOn>

    @Query("DELETE FROM ItemTurnOn where nameFocus=:nameFocus ")
    fun deleteAllItemAutomationFocus(nameFocus: String)

    @Query("DELETE FROM ItemTurnOn where lastModify=:lastModify and nameFocus=:nameFocus")
    fun deleteItemTimeAutomationFocus(lastModify: Long, nameFocus: String)

    @Query("DELETE FROM ItemTurnOn where lastModify=:lastModify and nameFocus=:nameFocus and isStartFocus=:isStartFocus")
    fun deleteItemTime(lastModify: Long, nameFocus: String, isStartFocus: Boolean)

    @Query("DELETE FROM ItemTurnOn where nameLocation=:nameLocation and nameFocus=:nameFocus")
    fun deleteItemLocationAutomationFocus(nameLocation: String, nameFocus: String)

    @Query("DELETE FROM ItemTurnOn where packageName=:packageName and nameFocus=:nameFocus")
    fun deleteItemAppAutomationFocus(packageName: String, nameFocus: String)

    @Query("DELETE FROM ItemTurnOn where packageName=:packageName")
    fun deleteItemAppFocus(packageName: String)

    @Query("UPDATE  ItemTurnOn SET lastModify=:lastModify,isStart=:isStart  where nameFocus=:name and packageName=:nameApp")
    fun updateStartItemAppAutomation(
        isStart: Boolean,
        name: String,
        nameApp: String,
        lastModify: Long,
    )

    @Query("UPDATE  ItemTurnOn SET  lastModify=:lastModify, isStart=:isStart  where nameFocus=:name and nameLocation=:location")
    fun updateStartItemLocationAutomation(
        isStart: Boolean,
        name: String,
        location: String,
        lastModify: Long,
    )

    @Query("UPDATE  ItemTurnOn SET  lastModify=:lastModify where nameLocation=:location")
    fun updateItemLocationLastModifyAutomation(location: String, lastModify: Long)


    @Query("UPDATE  ItemTurnOn SET  lastModify=:lastModify, isStart=:isStart  where nameFocus=:name and lastModify=:lastModifyOld")
    fun updateStartItemTimeAutomation(
        isStart: Boolean,
        name: String,
        lastModify: Long,
        lastModifyOld: Long,
    )

    @Query("UPDATE  ItemTurnOn SET timeStart=:timeStart, timeEnd=:timeEnd where nameFocus=:name and lastModify=:lastModifyOld")
    fun updateTimeAutomation(timeStart: Long, timeEnd: Long, name: String, lastModifyOld: Long)

    @Query("SELECT * FROM ItemTurnOn WHERE type!=:type")
    fun getItemTurnOnByControl(type: Int): ItemTurnOn

    @Query("DELETE FROM ItemTurnOn WHERE type!=-1")
    fun deleteItemTurnOnByControl()

    @Query(
        "UPDATE  ItemTurnOn SET timeStart=:timeStart , timeEnd=:timeEnd ," +
                " monDay=:monDay,tueDay=:tueDay,wedDay=:wedDay,thuDay=:thuDay,friDay=:friDay,satDay=:satDay," +
                "sunDay=:sunDay,lastModify=:lastModify where nameFocus=:name and lastModify=:lastModifyOld"
    )
    fun updateItemTimeAutomation(
        name: String,
        timeStart: Long,
        timeEnd: Long,
        monDay: Boolean,
        tueDay: Boolean,
        wedDay: Boolean,
        thuDay: Boolean,
        friDay: Boolean,
        satDay: Boolean,
        sunDay: Boolean,
        lastModify: Long,
        lastModifyOld: Long,
    )

    @Query("UPDATE  ItemTurnOn SET nameLocation=:nameLocation ,latitude=:latitude,longitude=:longitude,lastModify=:lastModify  where nameFocus=:name and nameLocation=:oldLocation")
    fun updateItemLocationAutomation(
        name: String,
        nameLocation: String,
        latitude: Double,
        longitude: Double,
        lastModify: Long,
        oldLocation: String,
    )

    //
    @Query("UPDATE  ItemTurnOn SET packageName=:packageName , nameApp=:nameApp,lastModify=:lastModify where nameFocus=:name and packageName=:oldApp")
    fun updateItemAppAutomation(
        name: String, packageName: String, nameApp: String, lastModify: Long, oldApp: String,
    )

    //
    @Query("UPDATE  ItemTurnOn SET nameFocus=:name  where nameFocus=:oldName")
    fun updateStartItemTimeAutomation(name: String, oldName: String)


    /////////////////////////////////////////////DB Control center

}
