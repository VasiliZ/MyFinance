package com.github.vasiliz.myfinance;

/**
 * Created by Vasili.Zaitsev on 02.03.2018.
 */

public class PresenterLoginActivity {

    private ModelLoginActivity mModelLoginActivity;
    private IContractView mContractView;

    public void attachView(IContractView pContractView){
        pContractView = mContractView;
    }

    public PresenterLoginActivity(ModelLoginActivity pModelLoginActivity){
        mModelLoginActivity = pModelLoginActivity;
    }

    public void createAccount(String pEmail, String pPassword){

    }

}
