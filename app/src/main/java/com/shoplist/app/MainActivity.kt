package com.shoplist.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.shoplist.app.presentation.navigation.ShopListNavGraph
import com.shoplist.app.presentation.theme.ShopListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShopListTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ShopListNavGraph()
                }
            }
        }
    }
}
