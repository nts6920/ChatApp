package com.example.chatapp1.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDiskIOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Font;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import company.librate.RateDialog;
import timber.log.Timber;

public class Utils {
    public static String transDateString(long date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm aa");
        Date date1 = new Date(date);
        return dateFormat.format(date1);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        return displaymetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        return displaymetrics.heightPixels;
    }

//    public static boolean isOnline(Context context) {
//        if (context == null) {
//            return false;
//        }
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
//        return (netInfo != null && netInfo.isConnected());
//    }

    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    public static int pxToDp(float px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static Locale getLocaleCompat() {
        Configuration configuration = Resources.getSystem().getConfiguration();
        return isAtLeastSdkVersion(Build.VERSION_CODES.N) ? configuration.getLocales().get(0) : configuration.locale;
    }

    public static boolean isAtLeastSdkVersion(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            Timber.e("Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
        }
    }
    public static List<String> getListFromAssets(Context context, String folder) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String[] images = context.getAssets().list(folder);
            for (int i = 0; i < images.length; i++) {
                list.add("file:///android_asset/" + folder + File.separator + images[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Font> getListFont(Context context, String folder){
        List<Font> fonts = new ArrayList<>();
        List<String> list= getListFromAssets(context,folder);
        for (int i = 0; i < list.size(); i++) {
            String[] strFont = list.get(i).split(Constant.REGEX);
            String tempStrFont = strFont[5];
            String[] fontName = tempStrFont.split(Constant.DOT);
            Font font = new Font(tempStrFont);
            font.setName(fontName[0]);
            fonts.add(font);
        }
        return fonts;
    }

    public static void sendEmail(Context context, String supportEmail) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/email");
        emailIntent.setPackage("com.google.android.gm");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{supportEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Report (" + context.getPackageName() + ")");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        context.startActivity(
                Intent.createChooser(
                        emailIntent,
                        context.getString(R.string.send_email_report_app)
                )
        );
    }

    public static void closeKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Uri getContactPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

        return photoUri;
    }

    public static Bitmap getPhoto(Context context, String contactId) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }

    public static Uri getPicture(Context context, String ID){
        ContentResolver cr = context.getContentResolver();
        Uri rawContactUri = null;
        Cursor rawContactCursor =  cr.query(ContactsContract.RawContacts.CONTENT_URI, new String[] {ContactsContract.RawContacts._ID}, ContactsContract.RawContacts.CONTACT_ID + " = " + ID, null, null);
        if(!rawContactCursor.isAfterLast()) {
            rawContactCursor.moveToFirst();
            rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendPath(""+rawContactCursor.getLong(0)).build();
        }
        rawContactCursor.close();

        return rawContactUri;
    }

}
