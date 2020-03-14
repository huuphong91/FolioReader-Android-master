package xyz.phongtoanhuu.danmei.utils

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

class InterstitialAdUtils(
    private val interstitialAd: InterstitialAd,
    private val adRequest: AdRequest
) {

    private var isReloaded = false
    private var adCloseListener: AdCloseListener? = null

    init {
        interstitialAd.adUnitId = "ca-app-pub-7147375377527505/7324584693"
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                loadInterstitial()
                adCloseListener?.onAdClosed()
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                if (!isReloaded) {
                    isReloaded = true
                    loadInterstitial()
                }
            }
        }
        loadInterstitial()
    }

    private fun loadInterstitial() {
        if (!interstitialAd.isLoaded && !interstitialAd.isLoading) {
            interstitialAd.loadAd(adRequest)
        }
    }

    fun showInterstitialAd(adCloseListener: AdCloseListener?) {
        if (interstitialAd.isLoaded) {
            isReloaded = false
            this.adCloseListener = adCloseListener
            interstitialAd.show()
        } else {
            loadInterstitial()
            adCloseListener?.onAdClosed()
        }
    }

    interface AdCloseListener {
        fun onAdClosed()
    }
}