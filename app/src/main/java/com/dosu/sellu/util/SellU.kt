package com.dosu.sellu.util

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.*
import com.dosu.sellu.data.network.FirebaseService
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.selling.SellingRepository
import com.dosu.sellu.data.network.user.UserRepository
import com.dosu.sellu.ui.history.viewmodel.HistoryViewModelFactory
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModelFactory
import com.dosu.sellu.ui.selling.viewmodel.SellingViewModelFactory
import com.dosu.sellu.ui.user_profile.viewmodel.UserViewModel
import com.dosu.sellu.ui.user_profile.viewmodel.UserViewModelFactory
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule

class SellU : Application(), DIAware {

    override val di by DI.lazy {
        import(androidXModule(this@SellU))

        bind { singleton { FirebaseService() } }

        bind<ProductRepository>() with singleton { ProductRepository(instance()) }
        bind { provider { ProductsViewModelFactory(instance()) }}

        bind<SellingRepository>() with singleton { SellingRepository(instance()) }
        bind { provider { SellingViewModelFactory(instance(), instance()) }}
        bind { provider { HistoryViewModelFactory(instance(), instance()) }}

        bind<UserRepository>() with singleton { UserRepository(instance())}
        bind { provider { UserViewModelFactory(instance()) }}
    }

    override fun onCreate() {
        super.onCreate()
        sellU = this
        mResources = resources
    }

    companion object{
        private lateinit var sellU: SellU
        private lateinit var mResources: Resources

        val instance get() = sellU
        val res get() = mResources
    }
}