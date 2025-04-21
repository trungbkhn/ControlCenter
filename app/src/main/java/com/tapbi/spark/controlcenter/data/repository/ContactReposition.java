package com.tapbi.spark.controlcenter.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.data.db.room.ControlCenterDataBase;
import com.tapbi.spark.controlcenter.data.model.ItemPeople;
import com.tapbi.spark.controlcenter.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ContactReposition {
    private final ControlCenterDataBase controlCenterDataBase;

    @Inject
    public ContactReposition(ControlCenterDataBase controlCenterDataBase) {
        this.controlCenterDataBase = controlCenterDataBase;
    }

    private void sortByName(List<ItemPeople> list) {
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                ItemPeople p1 = (ItemPeople) o1;
                ItemPeople p2 = (ItemPeople) o2;
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        });
    }

    public Single<List<ItemPeople>> getListAllPeople(Context context, boolean isSelection) {
        return Single.fromCallable(() -> getContacts(context, isSelection)).subscribeOn(Schedulers.io());
    }

    @SuppressLint("Range")
    private List<ItemPeople> getContacts(Context context, boolean isSelection) {
        List<ItemPeople> contactList = new ArrayList<>();
        String selection;
        boolean isStart;
        if (isSelection) {
            selection = ContactsContract.Contacts.STARRED + "='1'";
            isStart = true;
        } else {
            selection = null;
            isStart = false;
        }
        String[] fieldListProjection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.PHOTO_URI, ContactsContract.Contacts.STARRED};
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC";
        try (Cursor phones = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                fieldListProjection,
                selection,
                null,
                sort
        )) {
            HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
            if (phones != null && phones.getCount() > 0) {
                while (phones.moveToNext()) {
                    String normalizedNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    if (Integer.parseInt(phones.getString(phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                            String uri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                            if (uri == null) {
                                uri = "";
                            }
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (phoneNumber == null) {
                                phoneNumber = "";
                            }
                            contactList.add(
                                    new ItemPeople(
                                            phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)),
                                            phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                                            phoneNumber,
                                            uri,
                                            isStart, ""
                                    )
                            );
                        }
                    }
                }

            }
        } catch (Exception ignored) {
        }

        sortByName(contactList);
        return contactList;
    }

    public Single<List<ItemPeople>> getAllowedPeople(String name) {
        return Single.fromCallable(() -> controlCenterDataBase.focusDao().getAllItemAllowedPeople(name)).subscribeOn(Schedulers.io());
    }


    public Single<Boolean> insertAllowedPeople(ItemPeople itemPeople) {
        return Single.fromCallable(() -> insertAllowedPeopleDatabase(itemPeople)).subscribeOn(Schedulers.io());
    }

    public Single<Boolean> deleteItemAllowPeople(String nameFocus) {
        return Single.fromCallable(() -> deleteAllowedPeopleDatabase(nameFocus)).subscribeOn(Schedulers.io());
    }

    public Single<Boolean> updateFocusIOS(int mode, String nameFocus) {
        return Single.fromCallable(() -> updateItemFocus(mode, nameFocus)).subscribeOn(Schedulers.io());
    }

    public Single<Boolean> insertCustomNewAllowedPeople(List<ItemPeople> listItemPeople, String nameFocus) {
        return Single.fromCallable(() -> insertCustomAllowedPeople(listItemPeople, nameFocus)).subscribeOn(Schedulers.io());
    }

    private boolean insertAllowedPeopleDatabase(ItemPeople itemPeople) {
        try {
            controlCenterDataBase.focusDao().insertPeople(itemPeople);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteAllowedPeopleDatabase(String nameFocus) {
        try {
            controlCenterDataBase.focusDao().deleteAllItemAllowed(nameFocus);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateItemFocus(int mode, String nameFocus) {
        try {
            controlCenterDataBase.focusDao().updateStartItemFocusIosAutoTime(mode, nameFocus);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean insertCustomAllowedPeople(List<ItemPeople> listItemPeople, String nameFocus) {
        try {
            for (ItemPeople item : listItemPeople) {
                item.setNameFocus(nameFocus);
                controlCenterDataBase.focusDao().insertPeople(item);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkAllowPeople(Context context, String name, String incomingNumber) {
        List<ItemPeople> listAllowedPeople = controlCenterDataBase.focusDao().getAllItemAllowedPeople(name);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        for (ItemPeople itemPeople : listAllowedPeople) {
            if (StringUtils.INSTANCE.formatNumberToE164(itemPeople.getPhone(), countryCodeValue.toUpperCase(Locale.ROOT)).equals(incomingNumber)) {
                return true;
            }
        }
        return false;
    }


    public boolean checkAllowPeopleNoifi(String name, String title) {
        List<ItemPeople> listAllowedPeople = controlCenterDataBase.focusDao().getAllItemAllowedPeople(name);
        for (ItemPeople itemPeople : listAllowedPeople) {
            if (itemPeople.getName().equals(title)) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("Range")
    public ItemPeople getUriContactUpdate() {
        ItemPeople itemPeople = null;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        };

        try (Cursor cursor = App.mContext.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP + " DESC"
        )) {
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    // get id contact updated
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (phoneNumber == null) {
                        phoneNumber = "";
                    }
                    String uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    if (uri == null) {
                        uri = "";
                    }
                    itemPeople = new ItemPeople(id, name, phoneNumber, uri, false, "");
                    controlCenterDataBase.focusDao().updatePeople(id, name, phoneNumber, uri);

                }
            }
        } catch (Exception ignored) {
        }
        return itemPeople;
    }


    @SuppressLint("Range")
    public String getIdContactDelete() {
        String selection = ContactsContract.DeletedContacts.CONTACT_DELETED_TIMESTAMP + " > ?";
        String[] selectionArgs = new String[]{java.lang.String.valueOf(System.currentTimeMillis() - 5000)};
        Cursor cursorDelete = App.mContext.getContentResolver().query(
                ContactsContract.DeletedContacts.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null
        );
        if (cursorDelete != null) {
            if (cursorDelete.moveToNext()) {
                String id = cursorDelete.getString(cursorDelete.getColumnIndex(ContactsContract.DeletedContacts.CONTACT_ID));
                controlCenterDataBase.focusDao().deleteItemAllowedPeople(id);
                return id;

            }
            cursorDelete.close();
        }
        return "";
    }


}
