package com.dosu.sellu.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.RelativeSizeSpan
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dosu.sellu.R
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream
import java.util.*

fun ImageView.loadImage(context: Context, uri: Uri?){
    Glide.with(context).load(uri).into(this)
}

fun ByteArray.toDrawable(resources: Resources): Drawable {
    return BitmapDrawable(resources, BitmapFactory.decodeByteArray(this, 0, this.size))
}

fun Uri.toByteArray(context: Context): ByteArray{
    return context.contentResolver.openInputStream(this)!!.buffered().use { it.readBytes() }  // Me inserted non null but it may appear null, check hear
}

@Suppress("DEPRECATION")
fun Uri.toThumbnailByteArray(context: Context): ByteArray{
    val cSIZE = 144
    val bitmap = if(Build.VERSION.SDK_INT < 28) MediaStore.Images.Media.getBitmap(context.contentResolver, this)
                else ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
    val bW = bitmap.width
    val bH = bitmap.height
    val width: Int = if(bW<=bH) cSIZE else (bW.toDouble()/bH*cSIZE).toInt()
    val height: Int = if(bH<=bW) cSIZE else (bH.toDouble()/bW*cSIZE).toInt()
    val resize = Bitmap.createScaledBitmap(bitmap, width, height, true)
    val crop = Bitmap.createBitmap(resize, (width-cSIZE)/2, (height-cSIZE)/2, cSIZE, cSIZE)
    val stream = ByteArrayOutputStream()
    crop.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

val Double.dp: Float get() = (this * Resources.getSystem().displayMetrics.density).toFloat()

val Double.price: SpannableString get() {
    val span = if(this == this.toInt().toDouble()) SpannableString("${this.toInt()} сом")
        else SpannableString("$this сом")
    span.setSpan(RelativeSizeSpan(1.618f), 0,this.toInt().toString().length, 0)
    return span
}

val Timestamp.hmm : String get() {
    val cal = Calendar.getInstance()
    cal.time = this.toDate()
    val h = cal.get(Calendar.HOUR_OF_DAY)
    val m = cal.get(Calendar.MINUTE)
    return if(m<10) "$h:0$m"
    else "$h:$m"
}

val Timestamp.mmddyy : String get() {
    val cal = Calendar.getInstance()
    cal.time = this.toDate()
    return when {
        DateUtils.isToday(cal.timeInMillis) -> SellU.res.getString(R.string.today)
        isYesterday(cal.timeInMillis) -> SellU.res.getString(R.string.yesterday)
        else -> {
            val m = cal.get(Calendar.MONTH)+1
            val d = cal.get(Calendar.DAY_OF_MONTH)
            val y = cal.get(Calendar.YEAR)
            String.format("%02d/%02d/%02d", m, d, y%100)
        }
    }
}

val Timestamp.date : String get() {
    val cal = Calendar.getInstance()
    cal.time = this.toDate()
    return when {
        DateUtils.isToday(cal.timeInMillis) -> SellU.res.getString(R.string.today)
        isYesterday(cal.timeInMillis) -> SellU.res.getString(R.string.yesterday)
        else -> {
            val mInd = cal.get(Calendar.MONTH)
            val m = SellU.res.getStringArray(R.array.month)[mInd]
            val d = cal.get(Calendar.DAY_OF_MONTH)
            val y = cal.get(Calendar.YEAR)
            "$m $d $y"
        }
    }
}

fun isYesterday(timeInMillis: Long): Boolean{
    return DateUtils.isToday(timeInMillis + DateUtils.DAY_IN_MILLIS)
}
//import android.app.Activity
//import android.content.Context
//import android.graphics.Color
//import android.util.Log
//import android.util.TypedValue
//import android.view.View
//import android.view.inputmethod.InputMethodManager
//import android.widget.ProgressBar
//import android.widget.RelativeLayout
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.google.android.material.snackbar.Snackbar
//import java.text.SimpleDateFormat
//import java.util.*
//
///**
// * Show the soft keyboard. On phones with a hard keyboard has the unfortunate side effect
// * of leaving the keyboard showing until we or the user dismiss it, even when going
// * to another application.
// */
//fun View.showSoftKeyboard(force: Boolean = false) {
//    val inputMethodManager =
//        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    if (force) {
//        inputMethodManager.toggleSoftInput(
//            InputMethodManager.SHOW_FORCED,
//            InputMethodManager.HIDE_IMPLICIT_ONLY
//        )
//    }
//}
//
//fun Fragment.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
//    Snackbar.make(requireView(), message, duration).show()
//}
//
//inline var View.rightPaddingDp: Float
//    get() = TypedValue.applyDimension(
//        TypedValue.COMPLEX_UNIT_DIP, paddingRight.toFloat(),
//        resources.displayMetrics
//    )
//    set(value) {
//        val rightPx = resources.displayMetrics.density * value
//        setPadding(paddingLeft, paddingTop, rightPx.toInt(), paddingBottom)
//    }
//
//inline var View.leftPaddingDp: Float
//    get() = TypedValue.applyDimension(
//        TypedValue.COMPLEX_UNIT_DIP, paddingLeft.toFloat(),
//        resources.displayMetrics
//    )
//    set(value) {
//        val leftPx = resources.displayMetrics.density * value
//        setPadding(leftPx.toInt(), paddingTop, paddingRight, paddingBottom)
//    }
//
//fun TextView.hide() {
//    visibility = View.GONE
//}
//
//fun TextView.show() {
//    visibility = View.VISIBLE
//}
//
//fun Context.toast(message: String) {
//    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//}
//
//fun RelativeLayout.show() {
//    visibility = View.VISIBLE
//}
//
//fun RelativeLayout.hide() {
//    visibility = View.GONE
//}
//
//fun View.showRegSnackBar(message: String) {
//    Snackbar.make(this, message, Snackbar.LENGTH_LONG).setTextColor(Color.WHITE).show()
//}
//
//fun ProgressBar.show() {
//    visibility = View.VISIBLE
//}
//
//fun ProgressBar.hide() {
//    visibility = View.GONE
//}
//
//fun Fragment.hideKeyboard() {
//    view?.let {
//        activity?.hideKeyboard(it)
//    }
//}
//
//fun Activity.hideKeyboard() {
//    hideKeyboard(currentFocus ?: View(this))
//}
//
//fun Context.hideKeyboard(view: View) {
//    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//}
//
//fun getDateToday(context: Context, date: String, type: Int): String {
//
//    val calendar = Calendar.getInstance()
//    val sdf = if (type == 1)
//        SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
//    else
//        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
//    val today = sdf.format(calendar.time)
//    val yesterday = sdf.format(calendar.time.time - 86400000)
//
//    Log.d("NNNNN", "getDateToday (line 117): $date $today")
//
//    return when {
//        today == date -> context.getString(R.string.today)
//        yesterday == date -> context.getString(R.string.yesterday)
//        else -> getDateMonth(date, context, type)
//    }
//}

//fun getDateMonth(date: String, context: Context, type: Int): String {
//
//    return if (type == 1) {
//        val year = date.takeLast(4)
//        val day = date.take(2)
//        val temp = date.take(5)
//        val month = getMonth(context, temp.takeLast(2))
//        "$day $month $year"
//    } else {
//        val year = date.take(4)
//        val day = date.takeLast(2)
//        val temp = date.takeLast(5)
//        val month = getMonth(context, temp.take(2))
//        "$day $month $year"
//    }
//}
////
//fun getMonth(context: Context, month: String): String {
//    return when (month) {
//        "01" -> context.getString(R.string.jan)
//        "02" -> context.getString(R.string.feb)
//        "03" -> context.getString(R.string.march)
//        "04" -> context.getString(R.string.apr)
//        "05" -> context.getString(R.string.may)
//        "06" -> context.getString(R.string.june)
//        "07" -> context.getString(R.string.july)
//        "08" -> context.getString(R.string.aug)
//        "09" -> context.getString(R.string.sep)
//        "10" -> context.getString(R.string.oct)
//        "11" -> context.getString(R.string.nov)
//        else -> context.getString(R.string.dec)
//    }
//}
//
//fun getTimeToShow(diff: Long, context: Context): String {
//    val diffM = diff / 60
//    val diffH = diffM / 60
//    val diffD = diffH / 24
//    val diffMonth = diffD / 30
//    val diffYear = diffMonth / 12
//    return when {
//        diffYear > 0 -> "$diff ${context.getString(R.string.y)}"
//        diffMonth > 0 -> "$diffMonth ${context.getString(R.string.m)}"
//        diffD > 0 -> "$diffD ${context.getString(R.string.d)}"
//        diffH > 0 -> "$diffH ${context.getString(R.string.h)}"
//        diffM > 0 -> "$diffM ${context.getString(R.string.min)}"
//        else -> "$diff ${context.getString(R.string.s)}"
//    }
//}