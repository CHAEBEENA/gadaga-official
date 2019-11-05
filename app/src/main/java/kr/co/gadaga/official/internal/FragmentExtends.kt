package kr.co.gadaga.official.internal

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.addFragment(@IdRes containerViewId: Int, fragment: Fragment) = supportFragmentManager.beginTransaction().add(containerViewId, fragment).commit()
fun AppCompatActivity.replaceFragment(@IdRes containerViewId: Int, fragment: Fragment) = supportFragmentManager.beginTransaction().replace(containerViewId, fragment, "TAG").commit()