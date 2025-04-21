package com.tapbi.spark.controlcenter.feature

import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.common.models.ItemAddedNoty
import com.tapbi.spark.controlcenter.common.models.ItemRemovedNoty
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyGroup
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel
import com.tapbi.spark.controlcenter.utils.MethodUtils.getAppNameFromPackageName
import timber.log.Timber
import java.util.Collections


object NotyManager {
    @kotlin.jvm.JvmField
    var isFirstLoad = true

    @kotlin.jvm.JvmField
    var listenerConnected = false

    fun clearAll() {
        synchronized(listNotyGroup) {
            listNotyGroup.clear()
            listNotyGroup = ArrayList()
        }
    }/*list*/

    /*ArrayList<NotyGroup> list = new ArrayList<>();
     list.addAll(notyGroup);
     list.addAll(notyGroup);
     list.addAll(notyGroup);
     list.addAll(notyGroup);
     list.addAll(notyGroup);
     list.addAll(notyGroup);
     list.addAll(notyGroup);*/
//    val notyGroup: ArrayList<NotyGroup>
//        get() =/*ArrayList<NotyGroup> list = new ArrayList<>();
//         list.addAll(notyGroup);
//         list.addAll(notyGroup);
//         list.addAll(notyGroup);
//         list.addAll(notyGroup);
//         list.addAll(notyGroup);
//         list.addAll(notyGroup);
//         list.addAll(notyGroup);*/
//            /*list*/notyGroup

    fun setStateExpandNoty() {
        synchronized(listNotyGroup) {
            for (group in listNotyGroup) {
                group.state = NotyGroup.STATE.NONE
            }
        }
        synchronized(listNotyNow) {
            for (group in listNotyNow) {
                group.state = NotyGroup.STATE.NONE
            }
        }
    }

    fun addNoty(notyModel: NotyModel): ItemAddedNoty {
        return addNoty(listNotyGroup, notyModel)
    }

    fun addNotyNow(notyModel: NotyModel): ItemAddedNoty {
        //Timber.e("hoangld: them now size: " + listNotyNow.size());

        val itemAddedNoty = addNoty(listNotyNow, notyModel)
        var indexRemove = -1
        synchronized(listNotyGroup) {
            for (i in listNotyGroup.indices) {
                val group: NotyGroup = listNotyGroup[i]
                if (group.packageName.equals(notyModel.pakage)) {
                    var indexNotyRemove = -1
                    for (j in 0 until group.notyModels.size) {
                        val notyGroup: NotyModel = group.notyModels[j]
                        Timber.e("hoangld: id cu: "+ notyGroup.keyNoty + "  / notyModel: " + notyModel.keyNoty)
                        if (notyGroup.keyNoty.equals(notyModel.keyNoty)) {
                            indexNotyRemove = j
                            break
                        }
                    }
                    if (indexNotyRemove != -1) {
                        itemAddedNoty.posGroupNotify = i
                        itemAddedNoty.posChildRemove = indexNotyRemove
                        group.notyModels.removeAt(indexNotyRemove)
                    }
                    Timber.e("hoangld size: "+group.notyModels.size + " / "+indexNotyRemove);
                    if (group.notyModels.size == 0) {
                        indexRemove = i
                    }
                    break
                }
            }
            Timber.e("hoangld: indexRemove: " + indexRemove)
            if (indexRemove != -1) {
                listNotyGroup.removeAt(indexRemove)
                itemAddedNoty.posGroupRemove = indexRemove
            }
        }
        //Timber.e("hoangld: them now size2: "+listNotyNow.size());
        return itemAddedNoty
    }


    fun addNotyGroup(notyModel: NotyModel): ItemAddedNoty {
        //Timber.e("hoangld: them now size: " + listNotyNow.size());
        var indexRemove = -1
        val itemAddedNoty = addNoty(listNotyGroup, notyModel)
        synchronized(listNotyGroup) {
            for (i in listNotyGroup.indices) {
                val group: NotyGroup = listNotyGroup[i]
                if (group.packageName.equals(notyModel.pakage)) {
                    indexRemove = i
                    var indexNotyRemove = -1
                    for (j in 0 until group.notyModels.size) {
                        val notyGroup: NotyModel = group.notyModels[j]
                        if (notyGroup.keyNoty.equals(notyModel.keyNoty)) {
                            indexNotyRemove = j
                            break
                        }


                    }
                    if (indexNotyRemove != -1) {
                        itemAddedNoty.posChildRemove = indexNotyRemove
                    }
                    break
                }
            }

            if (indexRemove != -1) {
                itemAddedNoty.posGroupRemove = indexRemove
            }

        }
        return itemAddedNoty
    }

    //return insert new group
    private fun addNoty(listNotyGroup: ArrayList<NotyGroup>, notyModel: NotyModel): ItemAddedNoty {

        val itemAddedNoty = ItemAddedNoty()
        itemAddedNoty.packageName = notyModel.pakage
        itemAddedNoty.keyNoty = notyModel.keyNoty
        synchronized(listNotyGroup) {
            for (notyGroup in listNotyGroup) {
                if (notyGroup.groupKey.equals(notyModel.pakage)) {
                    if (!isNotyExist(notyModel, notyGroup.notyModels)) {
                        notyGroup.notyModels.add(0, notyModel)
                    } else {
                        removeNotyWhenAdd(notyGroup, notyModel)
                        notyGroup.notyModels.add(0, notyModel)
                    }
                    itemAddedNoty.posChildAdd = 0
                    try {
                        synchronized(notyGroup.notyModels) {
                            notyGroup.notyModels.sortByDescending { it.time }
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                    try {
                        Collections.sort(listNotyGroup, sort)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                    itemAddedNoty.posGroupNotify = listNotyGroup.indexOf(notyGroup)
                    itemAddedNoty.isNewGroupListNow = false
                    return itemAddedNoty
                }
            }
            val notyModels: ArrayList<NotyModel> = ArrayList<NotyModel>()
            notyModels.add(notyModel)
            listNotyGroup.add(
                NotyGroup(
                    notyModel.pakage,
                    notyModel.pakage,
                    getAppNameFromPackageName(App.mContext, notyModel.pakage),
                    notyModels
                )
            )
//            Timber.e("hoangld them moi: ")
            try {
                Collections.sort(listNotyGroup, sort)
            } catch (e: Exception) {
                Timber.e(e)
            }
            itemAddedNoty.isNewGroupListNow = true
            return itemAddedNoty
        }


    }

    fun addFirstTime(notyModel: NotyModel) {
        synchronized(listNotyGroup) {
            for (notyGroup in listNotyGroup) {
                if (notyGroup.groupKey.equals(notyModel.pakage)) {
                    if (!isNotyExist(notyModel, notyGroup.notyModels)) {
                        notyGroup.notyModels.add(0, notyModel)
                    } else {
                        removeNotyWhenAdd(notyGroup, notyModel)
                        notyGroup.notyModels.add(0, notyModel)
                    }
                    return
                }
            }
            val notyModels: ArrayList<NotyModel> = ArrayList()
            notyModels.add(notyModel)
            listNotyGroup.add(
                NotyGroup(
                    notyModel.pakage,
                    notyModel.pakage,
                    getAppNameFromPackageName(App.mContext, notyModel.pakage),
                    notyModels
                )
            )
        }
    }

    fun sortFirstTime() {
        synchronized(listNotyGroup) {
            for (i in listNotyGroup.indices) {
                try {
                    synchronized(listNotyGroup[i].notyModels) {
                        listNotyGroup[i].notyModels.sortByDescending { it.time }
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            try {
                Collections.sort(listNotyGroup, sort)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

    }

    private val sort: Comparator<NotyGroup> = Comparator<NotyGroup> { o1, o2 ->
        try {
            return@Comparator if (o1.notyModels[0].time > o2.notyModels[0].time) {
                -1
            } else 1
        } catch (e: Exception) {
            return@Comparator 1
        }
    }

    fun removeNoty(notyModel: NotyModel?): ItemRemovedNoty {
        var itemRemovedNoty: ItemRemovedNoty = removeNoty(listNotyNow, notyModel)
        itemRemovedNoty.isNotyNow = true
        if (itemRemovedNoty.posNotyModel == -1) {
            itemRemovedNoty = removeNoty(listNotyGroup, notyModel)
            itemRemovedNoty.isNotyNow = false
        }
        return itemRemovedNoty
    }

    private fun removeNoty(
        listNotyGroup: ArrayList<NotyGroup>, notyModel: NotyModel?,
    ): ItemRemovedNoty {

        val itemRemovedNoty = ItemRemovedNoty()
        synchronized(listNotyGroup) {
            for (i in listNotyGroup.indices) {
                val group: NotyGroup = listNotyGroup[i]
                if (group.packageName != null && notyModel != null && notyModel.pakage != null && group.packageName == notyModel.pakage) {
                    val iterator = group.notyModels.listIterator()
                    while (iterator.hasNext()) {
                        val noty = iterator.next()
                        if (noty != null && noty.keyNoty == notyModel.keyNoty) {
                            iterator.remove()
                            itemRemovedNoty.posNotyModel = iterator.previousIndex() + 1
                            itemRemovedNoty.posNotyGroup = i
                            itemRemovedNoty.keyNoty = notyModel.keyNoty
                            if (group.notyModels.isEmpty()) {
                                listNotyGroup.removeAt(i)
                                itemRemovedNoty.isRemoveGroups = true
                                return itemRemovedNoty
                            }
                            break
                        }
                    }
                }
            }
        }
        return itemRemovedNoty
    }

    private fun removeNotyWhenAdd(notyGroup: NotyGroup, notyModel: NotyModel) {
        val iterator = notyGroup.notyModels.iterator()
        while (iterator.hasNext()) {
            val currentNoty = iterator.next()
            val groupKey: String? = currentNoty.keyNoty
            val groupKeyModel: String? = notyModel.keyNoty
            if (checkExits(currentNoty, notyModel)) {
                iterator.remove()
            }
        }

    }

    private fun isNotyExist(notyModel: NotyModel, notyModels: ArrayList<NotyModel>): Boolean {
        for (i in notyModels.indices) {
            if (notyModels[i].keyNoty.equals(notyModel.keyNoty)) {
                return true
            } else {
                if (notyModels[i].time == notyModel.time
                    && notyModels[i].pakage.equals(notyModel.pakage)
                    && notyModels[i].title.equals(notyModel.title)
                    && notyModels[i].content.equals(notyModel.content)
                ) return true
            }
        }
        return false
    }

    private fun checkExits(m1: NotyModel, m2: NotyModel): Boolean {
        if (m1.keyNoty.equals(m2.keyNoty)) {
            return true
        } else {
            if (m1.time == m2.time
                && m1.pakage.equals(m2.pakage)
                && m1.title.equals(m2.title)
                && m1.content.equals(m2.content)
            ) return true
        }
        return false
    }

    fun clearNotyNow() {
        val listNotyOld: ArrayList<NotyGroup> = ArrayList()
        synchronized(listNotyGroup) {
            for (groupNow in listNotyNow) {
                for (group in listNotyGroup) {
                    if (groupNow.packageName.equals(group.packageName)) {
                        val listHave: ArrayList<NotyModel> = ArrayList<NotyModel>()
                        for (noty in group.notyModels) {
                            for (notyNow in groupNow.notyModels) {
                                if (noty.keyNoty.equals(notyNow.keyNoty)) {
                                    //Timber.e("hoangld: "+ noty.getPakage());
                                    listHave.add(noty)
                                }
                            }
                        }
                        //Timber.e("hoangld listHave: "+ listHave.size() + " / new: "+groupNow.getNotyModels().size());
                        group.notyModels.removeAll(listHave.toSet())
                        group.notyModels.addAll(0, groupNow.notyModels)
                        listNotyOld.add(groupNow)
                        break
                    }
                }
            }
            listNotyNow.removeAll(listNotyOld.toSet())
            listNotyGroup.addAll(0, listNotyNow)

            try {
                Collections.sort(
                    listNotyGroup, sort
                )
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        listNotyNow.clear()
    }

    var listNotyGroup = ArrayList<NotyGroup>()
    val listNotyNow = ArrayList<NotyGroup>()
}
