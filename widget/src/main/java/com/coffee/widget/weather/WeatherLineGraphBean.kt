package com.coffee.widget.weather

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2022/11/24 10:45
 */
data class WeatherLineGraphBean(
    val temp: Float?,
    val highTemp: Float?,
    val lowTemp: Float?,
    val weekTime: String,   //星期、时段
    val dateTime: String?,   //日期
    val weatherType: String,
    val weather: String
) {

    override fun toString(): String {
        return "WeatherLineGraphBean{temp= $temp, highTemp= $highTemp, lowTemp= $lowTemp, dateTime= $dateTime, weatherType= $weatherType, weather= $weather}"
    }

}
