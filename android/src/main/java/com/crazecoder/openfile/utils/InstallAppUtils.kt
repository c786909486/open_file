package com.crazecoder.openfile.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URLConnection

/**
 *@packageName com.yyt.hongtaiposapp.utils
 *@author kzcai
 *@date 2020/3/16
 */
object InstallAppUtils {

    fun installApp(activity: Context, file: File) {
        val intent: Intent = validatedFileIntent(activity, file)!!
        if (intent != null) {
            activity.startActivity(intent)

        } else {

        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val b: Boolean = activity.getPackageManager().canRequestPackageInstalls()
//            if (b) {
//                Log.e(javaClass.name, "一获取")
//                install(context = activity,file = file)
//            } else {
//                Log.e(javaClass.name, "为获取")
//                PermissionRequest.request(activity, arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES), object : OnPermissionCallback() {
//                    fun onPermissionSuccess() {
//                        Log.e(javaClass.name, "获取成功")
//                        install(activity,file)
//                    }
//
//                    fun onPermissionFailed() {
//                        Log.e(javaClass.name, "获取失败")
//                        SimpleDialog.getInstance(activity).create()
//                                .setMessage("更新app需要开启未知来源权限，是否前往打开？")
//                                .setCancelButton { dialog, position -> dialog.dismiss() }.setPositiveButton { dialog, position ->
//                                    dialog.dismiss()
//                                    val packageURI = Uri.parse("package:" + activity.getPackageName())
//                                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
//                                    (context as Activity).startActivityForResult(intent, com.axun.ccrcmanage.market.presenter.UpdatePresenter.INSTALL_REQUEST)
//                                }.show()
//                    }
//                })
//            }
//        } else {
////            install(file)
//        }
    }

    fun buildIntent(context:Context,file: File):Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri: Uri = FileProvider.getUriForFile(context, context.packageName + ".fileProvider", file)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            //                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return intent
    }

    @Synchronized
    fun validatedFileIntent(context: Context?, file: File): Intent? {
        //        if (path.endsWith("apk")){
//            Log.d("123123123","anzhuangb");
//            contentType = "application/vnd.android.package-archive";
//        }
        var intent: Intent = buildIntent(context!!, file)
        if (validateIntent(context!!, intent)) {
            return intent
        }
        var mime: String? = null
        var inputFile: FileInputStream? = null
        try {
            inputFile = FileInputStream(file)
            mime = URLConnection.guessContentTypeFromStream(inputFile) // fails sometime
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (inputFile != null) {
                try {
                    inputFile.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        if (mime == null) {
            mime = URLConnection.guessContentTypeFromName(file.absolutePath) // fallback to check file extension
        }
        if (mime != null) {
            intent =buildIntent(context, file)
            if (validateIntent(context, intent)) return intent
        }
        return null
    }

    private fun validateIntent(context: Context, intent: Intent): Boolean {
        val manager = context.packageManager
        val infos = manager.queryIntentActivities(intent, 0)
        return if (infos.size > 0) { //Then there is an Application(s) can handle this intent
            true
        } else { //No Application can handle this intent
            false
        }
    }
}