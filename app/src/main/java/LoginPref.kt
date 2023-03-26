
package com.main.mainproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class LoginPref {
    private lateinit var pref : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var context: Context
    var PRIVATE_MODE:Int = 0

    constructor(context: Context){
        this.context = context
        pref = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE)
        editor = pref.edit()
    }

    companion object{
        val PREF_NAME = "Login_Preference"
        val IS_LOGIN = "isLoggedIn"
        val KEY_EMAIL = "email"
    }

    fun createLoginSession(name: String?, email:String?){
        editor.putBoolean(IS_LOGIN,true)
        editor.putString(KEY_EMAIL,email)
        editor.putString(PREF_NAME,name)
        editor.commit()
    }

    fun checkLogin(){
        if(!this.isLoggedIn()){
            var i : Intent = Intent(context,MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }

    }

    fun getUserDetails():HashMap<String,String>{
        var user:Map<String,String> = HashMap<String,String>()
        (user as HashMap).put(KEY_EMAIL,pref.getString(KEY_EMAIL,null)!!)
        (user as HashMap).put(PREF_NAME,pref.getString(PREF_NAME,null)!!)
        return user
    }


    fun LogoutUser(){
        editor.clear()
        editor.commit()
        var i : Intent = Intent(context,MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }

    fun isLoggedIn():Boolean{
        return pref.getBoolean(IS_LOGIN,false)
    }
}
