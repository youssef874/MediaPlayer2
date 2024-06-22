package com.example.mpcore.audio.api.exception

class MissingPermissionException(val permissionNeeded: List<String>): IllegalAccessException("Missing permission: $permissionNeeded")

class AddAudioToPlayListThatAlreadyExist(val audioId: Long, val playListName: String): IllegalAccessException("$audioId already exist in $playListName")