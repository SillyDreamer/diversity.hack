package com.mysite.mywavesapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.crypto.WavesCrypto
import com.wavesplatform.sdk.keeper.interfaces.KeeperCallback
import com.wavesplatform.sdk.keeper.model.KeeperResult
import com.wavesplatform.sdk.model.request.node.DataTransaction
import com.wavesplatform.sdk.model.request.node.InvokeScriptTransaction
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import com.wavesplatform.sdk.model.response.node.transaction.TransferTransactionResponse
import com.wavesplatform.sdk.net.NetworkException
import com.wavesplatform.sdk.net.OnErrorListener
import com.wavesplatform.sdk.utils.RxUtil
import com.wavesplatform.sdk.utils.SignUtil
import com.wavesplatform.sdk.utils.WavesConstants
import com.wavesplatform.sdk.utils.getScaledAmount
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {



    // For Activity or Fragment add Observables in CompositeDisposable
    private val compositeDisposable = CompositeDisposable()
    private val seed = "place ocean topple tongue intact usual reason dwarf market morning stomach ball"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val pb = findViewById<ProgressBar>(R.id.progressBar)
        pb.progress = 56

        val gf = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.gifImageView)


        // First you must init() WavesSdk in Application and add Internet permission
        // Put your seed in const [MainActivity.Companion.SEED] from https://testnet.wavesplatform.com
        // or https://client.wavesplatform.com

        fab.setOnClickListener {
            // Generate or add your seed
            val newSeed = WavesCrypto.randomSeed()
           // seedTextView.text = "New seed is: $newSeed"

            val address =
                WavesCrypto.addressBySeed(seed, WavesSdk.getEnvironment().chainId.toString())

            // Create request to Node service about address balance
            getWavesBalance(address)

            // Examples of transactions available in [WavesServiceTest]
        }




        // You must configure dApp if you want to use Waves Keeper. Look at App
        // Try to send or sign data-transaction via mobile Keeper

//        val dataTransaction = DataTransaction(mutableListOf(
//            DataTransaction.Data("key0", "string", "This is Data TX"),
//            DataTransaction.Data("key1", "integer", 100),
//            DataTransaction.Data("key2", "integer", -100),
//            DataTransaction.Data("key3", "boolean", true),
//            DataTransaction.Data("key4", "boolean", false),
//            DataTransaction.Data("key5", "binary", "SGVsbG8h")))

        val transaction = TransferTransaction(
            assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            recipient = "3P8ys7s9r61Dapp8wZ94NBJjhmPHcBVBkMf",
            amount = 10000000,
            attachment = SignUtil.textToBase58("Hello-!"),
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY
        )
        transaction.fee = WavesConstants.WAVES_MIN_FEE

        fab_d_app_send.setOnClickListener {
            gf.setImageResource(R.drawable.eat)
            Handler().postDelayed({
                gf.setImageResource(R.drawable.ab)
            }, 4000)
           // val eee = Intent(this, Eat::class.java)
            //startActivity(eee)
            //val ee = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.gifImageView)

            val call = InvokeScriptTransaction.Call(
                function = "feed",
                args = mutableListOf()
            )

            val payment = mutableListOf(
                InvokeScriptTransaction.Payment(
                    assetId = null,
                    amount = 100000L))

            val transaction = InvokeScriptTransaction(
                dApp = "3N7cKXykq32Xk6z4jSbECdcjyswJy84iSLZ",
                call = call,
                payment = payment)

            transaction.fee = 900000L
            transaction.sign(seed)

            WavesSdk.service()
                .getNode()
                .transactionsBroadcast(transaction)
                .compose(RxUtil.applyObservableDefaultSchedulers())
                .subscribe({ response ->
                    // Do someth3MyenyZ4nuu7o8sfGaX9Rb7SfXs7tm2LgoYing on success, now we have wavesBalance.balance in satoshi in Long
                }, { error ->
                    print(error.toString())
                    Toast.makeText(
                        this@MainActivity,
                        "Erorr" + error.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    // Do something on fail
                })

        }

        fab_d_app_sign.setOnClickListener {

            WavesSdk.keeper().sign(this, transaction, object : KeeperCallback<TransferTransaction> {
                override fun onSuccess(result: KeeperResult.Success<TransferTransaction>) {
                    Log.d("KEEPERTEST", result.toString())
                }

                override fun onFailed(error: KeeperResult.Error) {
                    Log.d("KEEPERTEST", error.toString())
                }
            })
        }

        handleServiceErrors()
    }

    private fun handleServiceErrors() {
        WavesSdk.service().addOnErrorListener(object : OnErrorListener {
            override fun onError(exception: NetworkException) {
                // Handle NetworkException here
            }
        })
    }

    private fun getWavesBalance(address: String) {
        compositeDisposable.add(
            WavesSdk.service()
                .getNode() // You can choose different Waves services: node, matcher and data service
                .addressesBalance(address) // Here methods of service
                .compose(RxUtil.applyObservableDefaultSchedulers())
                .subscribe({ wavesBalance ->
                    // Do something on success, now we have wavesBalance.balance in satoshi in Long
                    Toast.makeText(
                        this@MainActivity,
                        "Balance is : ${getScaledAmount(wavesBalance.balance, 8)} Waves",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }, { error ->
                    // Do something on fail
                    val errorMessage = "Can't get addressesBalance! + ${error.message}"
                    Log.e("MainActivity", errorMessage)
                    error.printStackTrace()
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                })
        )
    }

    fun signUp(view: View) {
        val su = Intent(this, Avtorization::class.java)
        startActivity(su)
    }

//    fun openpage(view: View) {
//        val op = Intent(this, Java_activity::class.java)
//        startActivity(op)
//    }

    // Unsubscribe after destroy
    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val SEED = ""
    }

}
