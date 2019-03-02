package com.company.googledrive.entity

//todo implements sync with GDrive
enum class Role(val caption: String) {
    ADMIN("Администратор"),
    MEMBER("Участник клуба"),
    KICKED("Исключён"),
    BANNED("Забанен"),
    STRANGER("Мимокрокодил"),
    UNKNOWN("Неизвестен"),
    LEAVER("Вышел"),
    TWINK("Твинк");

    companion object {

        fun fromCaption(caption: String): Role? {
            return values().find { it.caption == caption }
        }
    }
}
