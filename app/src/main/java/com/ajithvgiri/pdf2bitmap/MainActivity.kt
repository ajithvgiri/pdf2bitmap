package com.ajithvgiri.pdf2bitmap

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.ajithvgiri.runtimepermissions.CheckPermissionResult
import com.ajithvgiri.runtimepermissions.PermissionHandler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_DOC = 100
        private const val PERMISSION_REQUEST_STORAGE = 101
        private const val PAGE_NUMBER = 0
        private val PERMISSION_STORAGE =
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonOpenDocument.setOnClickListener {

            PermissionHandler.checkPermission(this, PERMISSION_STORAGE) { result ->
                when (result) {
                    CheckPermissionResult.PermissionGranted -> {
                        openDocument()
                    }
                    CheckPermissionResult.PermissionAsk -> {
                        ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, PERMISSION_REQUEST_STORAGE)
                    }
                    CheckPermissionResult.PermissionPreviouslyDenied -> {
                        ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, PERMISSION_REQUEST_STORAGE)
                    }
                }
            }
        }

    }

    private fun openDocument() {
        val intentPDF = Intent(Intent.ACTION_GET_CONTENT)
        intentPDF.type = "application/pdf"
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intentPDF, "Choose PDF"), REQUEST_CODE_DOC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DOC && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                val bitmap = PDFThumbnailUtils.convertPDFtoBitmap(this, uri, PAGE_NUMBER)
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }


    //Permission Request Result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_STORAGE -> if (grantResults.isNotEmpty()) {
                val readStoragePermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val writeStoragePermissionAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (readStoragePermissionAccepted && writeStoragePermissionAccepted) {
                    openDocument()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        ) {
                            return
                        }
                    }
                    Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
