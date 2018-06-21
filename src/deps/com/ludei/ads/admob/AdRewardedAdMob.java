package com.ludei.ads.admob;

import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.ludei.ads.AbstractAdInterstitial;
import com.ludei.ads.AdInterstitial;

class AdRewardedAdMob extends AbstractAdInterstitial
{
    private RewardedVideoAd _interstitial;
    private String adUnit;
    private boolean rewardCompleted = false;
    private boolean loading = false;

    AdRewardedAdMob(Context ctx, String adunit) {
        _interstitial = MobileAds.getRewardedVideoAdInstance(ctx);
        this.adUnit = adunit;
        _interstitial.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                loading = false;
                notifyOnLoaded();
            }

            @Override
            public void onRewardedVideoAdOpened() {
                notifyOnShown();
            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                if (!rewardCompleted) {
                    rewardCompleted = true;
                    AdInterstitial.Reward result = new AdInterstitial.Reward();
                    AdInterstitial.Error error = new AdInterstitial.Error();
                    error.message = "Rewarded video closed or skipped";
                    notifyOnRewardCompleted(result, error);
                }
                notifyOnDismissed();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                rewardCompleted = true;
                AdInterstitial.Reward result = new AdInterstitial.Reward();
                result.amount = Math.max(rewardItem.getAmount(),1);
                result.currency = rewardItem.getType();
                result.itmKey = rewardItem.getType();
                notifyOnRewardCompleted(result, null);
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                notifyOnClicked();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
                loading = false;
                Error error = new Error();
                error.code = errorCode; error.message = "Error with code: " + errorCode;
                notifyOnFailed(error);
            }
        });
    }

    @Override
    public void loadAd() {
        if (!loading) {
            loading = true;
            _interstitial.loadAd(adUnit, new AdRequest.Builder().build());
        }
    }

    @Override
    public void show() {
        this.rewardCompleted = false;
        if (_interstitial.isLoaded()) {
            _interstitial.show();
        }
        else {
            if (!loading) {
                loadAd();
            }
            AdInterstitial.Reward result = new AdInterstitial.Reward();
            AdInterstitial.Error error = new AdInterstitial.Error();
            error.message = "Rewarded Video not ready yet";
            notifyOnRewardCompleted(result, error);
        }

    }

    @Override
    public void destroy() {

    }
}
