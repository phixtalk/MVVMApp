package com.mvvmapp.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mvvmapp.data.network.MyApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {

    fun userLogin(email: String, password: String) : LiveData<String>{

        val loginResonse = MutableLiveData<String>()

        MyApi().userLogin(email, password)
            .enqueue(object : Callback<ResponseBody>{

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    loginResonse.value = t.message
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful){
                        loginResonse.value = response.body()?.string()
                    }else{
                        loginResonse.value = response.errorBody()?.string()
                    }
                }

            })

        return  loginResonse
    }

}