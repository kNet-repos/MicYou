package com.lanrhyme.micyou.viewmodel

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class SavedHost(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val port: Int,
    val mode: ConnectionMode,
    val note: String = ""
)

internal object SavedHostCodec {
    fun encode(hosts: List<SavedHost>): String = JSONArray().apply {
        hosts.forEach { host -> put(JSONObject().apply {
            put("id", host.id); put("name", host.name); put("address", host.address)
            put("port", host.port); put("mode", host.mode.name); put("note", host.note)
        }) }
    }.toString()

    fun decode(raw: String): List<SavedHost> = try {
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            try {
                val obj = array.getJSONObject(index)
                val host = SavedHost(obj.getString("id"), obj.getString("name"), obj.getString("address"),
                    obj.getInt("port"), ConnectionMode.valueOf(obj.getString("mode")), obj.optString("note", ""))
                host.takeIf { it.id.isNotBlank() && it.name.isNotBlank() && it.address.isNotBlank() && it.port in 1..65534 }
            } catch (_: Exception) { null }
        }
    } catch (_: Exception) { emptyList() }
}
