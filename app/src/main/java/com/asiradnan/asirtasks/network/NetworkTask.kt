package com.asiradnan.asirtasks.network

import com.asiradnan.asirtasks.data.Task
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTask(
    val uuid: String,
    val user: Int? = null,
    val name: String,
    @SerialName("completed")
    val isCompleted: Boolean = false,
    val date: Long?,
    val time: Long?,
    @SerialName("last_modified")
    val modificationTime: Long = System.currentTimeMillis()
)

fun NetworkTask.toEntity(): Task {
    return Task(
        uuid = this.uuid,
        name = this.name,
        isCompleted = this.isCompleted,
        date = this.date,
        time = this.time,
        modificationTime = this.modificationTime
    )
}

// Convert from Local (Room) to Network (Retrofit)
fun Task.toNetwork(): NetworkTask {
    return NetworkTask(
        uuid = this.uuid,
        name = this.name,
        isCompleted = this.isCompleted,
        date = this.date,
        time = this.time,
        modificationTime = this.modificationTime
    )
}
