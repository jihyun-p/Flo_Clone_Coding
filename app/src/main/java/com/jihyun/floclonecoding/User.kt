package com.jihyun.floclonecoding

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserTable")
data class User(
    var email : String,
    var password : String
){ // 사용자 추가 될 때마다 카운트
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}
