package com.coffee.zdevsuper.ui

import android.content.Intent
import android.graphics.Color
import android.view.ContextMenu
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.coffee.base.ui.BaseActivity
import com.coffee.zdevsuper.R
import com.coffee.zdevsuper.ui.coroutine.CoroutineActivity
import com.coffee.zdevsuper.databinding.ActivityMainBinding
import com.coffee.zdevsuper.bean.SettingData
import com.coffee.zdevsuper.ui.ktx.KtxPracticeActivity
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup

class MainActivity: BaseActivity<ActivityMainBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private val toolBar by lazy { mBinding.includeContent.headerInclude.toolbar }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
    }*/

    override fun initData() {
    }



    override fun initView(vb: ActivityMainBinding) {
        setSupportActionBar(toolBar)

        //(第一种获取导航控制器)此API只有在容器是fragment的时候才有用
        navController = findNavController(R.id.nav_controller_view)

        //(第二种获取导航控制器)如果容器是用FragmentContainerView，则需改为从 NavHostFragment 检索 NavController
        /*val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = fragment.navController*/

        appBarConfiguration = AppBarConfiguration(navController.graph, vb.drawerLayout)
        //如果是自设的ToolBar，添加导航支持 则调用 ToolBar.setupWithNavController(navController, appBarConfiguration)
        toolBar.setupWithNavController(navController, appBarConfiguration)

        //如果是默认的ActionBar，添加导航支持 则调用 setupActionBarWithNavController(navController, appBarConfiguration)
        /*setupActionBarWithNavController(navController, appBarConfiguration)*/

        vb.drawerLayout.setScrimColor(Color.TRANSPARENT)

        vb.includeContent.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab).setAction("Action", null).show()
        }

        createSetting()
    }

    private fun createSetting() {
        val list = mutableListOf<SettingData>().also {
            it.add(SettingData("FloatingWindow", null))
            it.add(SettingData("ViewDragHelper", null))
            it.add(SettingData("Navigation", null))
            it.add(SettingData("RoundAny", null))
            it.add(SettingData("ShadowAny", null))
            it.add(SettingData("WeatherLineGraph", null))
            it.add(SettingData("Coroutine", CoroutineActivity::class.java))
            it.add(SettingData("Flow", null))
            it.add(SettingData("Compose", null))
            it.add(SettingData("WorkManager", null))
            it.add(SettingData("KtxPractice", KtxPracticeActivity::class.java))
        }

        mBinding.includeMenu.settingRecycler
            .linear()
            .setup {
                addType<SettingData>(R.layout.layout_item_setting)

                R.id.title.onClick {
                    val data = getModel<SettingData>()
                    data.clazz?.let {
                        startActivity(Intent(context, it))
                    }
                }
            }
            .models = list
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onSupportNavigateUp(): Boolean {
        /*val up = navController.navigateUp(appBarConfiguration)
        if (up) return true
        mBinding.drawerLayout.open()
        return false*/
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        return super.onSupportNavigateUp()
    }
}