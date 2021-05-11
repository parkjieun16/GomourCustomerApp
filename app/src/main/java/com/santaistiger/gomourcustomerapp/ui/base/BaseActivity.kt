package com.santaistiger.gomourcustomerapp.ui.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pedro.library.AutoPermissions
import com.santaistiger.gomourcustomerapp.R

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        // Auto permission
        AutoPermissions.loadAllPermissions(this, 1)
    }
}