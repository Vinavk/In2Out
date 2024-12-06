package com.example.in2out

sealed class Screen(var value : String ){
    object HomeScreen : Screen("HomeSCren")
    object MonthlyScreen : Screen("MonthlyScreen")
}
