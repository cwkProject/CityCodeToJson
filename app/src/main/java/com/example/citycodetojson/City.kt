package com.example.citycodetojson

/**
 * 城市对象，省、市、区共同使用的结构
 *
 * @author 超悟空
 * @version 1.0 2018/8/22
 * @since 1.0 2018/8/22
 *
 * @property id 城市adcode
 * @property name 城市名称
 * @property cityList 下属城市列表
 **/
data class City(val id: String, val name: String, var cityList: MutableList<City>? = null)