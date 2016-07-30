package com.github.jk1.ytplugin.search.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.intellij.tasks.Comment
import com.intellij.tasks.Task
import com.intellij.tasks.TaskType
import com.intellij.tasks.impl.LocalTaskImpl
import java.util.*
import javax.swing.Icon

class Issue(item: JsonElement, val repoUrl: String) {

    private val PREDEFINED_FIELDS = arrayOf("projectShortName", "numberInProject", "summary",
            "description", "created", "updated", "updaterName", "updaterFullName", "resolved",
            "reporterName", "reporterFullName", "commentsCount", "votes", "attachments", "links")

    val id: String
    val entityId: String
    val summary: String
    val description: String
    val createDate: Date
    val updateDate: Date
    val resolved: Boolean
    val customFields: List<CustomField>

    init {
        val root = item.asJsonObject
        id = root.get("id").asString
        entityId = root.get("entityId").asString
        summary = getFieldValue("summary", root) ?: ""
        description = getFieldValue("description", root) ?: ""
        createDate = Date(getFieldValue("created", root)?.toLong() ?: 0)
        updateDate = Date(getFieldValue("updated", root)?.toLong() ?: 0)
        resolved = root.getAsJsonArray("field").any { "resolved".equals(it.asJsonObject.get("name").asString) }
        customFields = root.getAsJsonArray("field").filter { it.isCustomField() }.map { CustomField(it) }
    }

    private fun getFieldValue(name: String, root: JsonObject): String? {
        return root.getAsJsonArray("field").firstOrNull {
            name.equals(it.asJsonObject.get("name").asString)
        }?.asJsonObject?.get("value")?.asString
    }

    private fun JsonElement.isCustomField(): Boolean {
        val name = asJsonObject.get("name")
        return name != null && !PREDEFINED_FIELDS.contains(name.asString)
    }

    fun asTask(): Task = object : Task() {

        override fun getId(): String = this@Issue.id

        override fun getSummary(): String = this@Issue.summary

        override fun getDescription(): String = this@Issue.description

        override fun getCreated() = this@Issue.createDate

        override fun getUpdated() = this@Issue.updateDate

        override fun isClosed() = this@Issue.resolved

        override fun getComments(): Array<out Comment> = arrayOf()

        override fun getIcon(): Icon = LocalTaskImpl.getIconFromType(type, isIssue)

        override fun getType(): TaskType = TaskType.OTHER

        override fun isIssue() = true

        override fun getIssueUrl() = "$repoUrl/issue/$id"
    }
}

